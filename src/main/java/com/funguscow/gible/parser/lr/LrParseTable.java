package com.funguscow.gible.parser.lr;

import com.funguscow.gible.codegen.ClassSource;
import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.lexer.provider.TokenProvider;
import com.funguscow.gible.parser.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An LR parse table with SHIFT, REDUCE, GOTO, and ACCEPT actions
 */
public class LrParseTable {

    public static class LrParseException extends RuntimeException{
        public LrParseException(String message){
            super(message);
        }
    }

    public static class ParsingPair{
        public ParseTerm term;
        public int state;

        /**
         *
         * @param term The term triggering this action
         * @param state This state's current action
         */
        public ParsingPair(ParseTerm term, int state){
            this.term = term;
            this.state = state;
        }
        public int hashCode(){
            return term.hashCode() * 7 + state;
        }
        public boolean equals(Object other){
            if(other == this)
                return true;
            if(!(other instanceof ParsingPair))
                return false;
            ParsingPair otherPair = (ParsingPair)other;
            return term.equals(otherPair.term) && state == otherPair.state;
        }
        public String toString(){
            return String.format("%d@%s", state, term);
        }
    }

    public static class ParsingAction{
        public enum Action{
            SHIFT,
            REDUCE,
            GOTO,
            ACCEPT
        }
        public Action action;
        public int target;

        /**
         *
         * @param action The action type
         * @param target The next state for SHIFT or GOTO, or the rule for REDUCE
         */
        public ParsingAction(Action action, int target){
            this.action = action;
            this.target = target;
        }
        public boolean equals(Object obj){
            if(obj == this)
                return true;
            if(!(obj instanceof ParsingAction))
                return false;
            ParsingAction other = (ParsingAction)obj;
            return action == other.action && target == other.target;
        }
        public String toString(){
            return action.name() + ":" + target;
        }
    }

    private final Map<ParsingPair, ParsingAction> table;
    private final Map<Integer, List<ParsingPair>> pairsByState;
    private int initialState;

    public LrParseTable(){
        table = new HashMap<>();
        pairsByState = new HashMap<>();
        initialState = -1;
    }

    /**
     * Instructs the table to perform a given action for a particular transition
     * @param state Current state off which to transition
     * @param term The term triggering this transition
     * @param action The action type to execute
     * @param target The target state or rule of the action type
     */
    public void setAction(int state, ParseTerm term, ParsingAction.Action action, int target){
        ParsingPair pair = new ParsingPair(term, state);
        ParsingAction present = table.get(pair);
        ParsingAction pAction = new ParsingAction(action, target);
        if(present != null && !present.equals(pAction)) {
            if(present.action == ParsingAction.Action.REDUCE && pAction.action == ParsingAction.Action.SHIFT){
                table.put(pair, pAction);
            }
            else {
                if (present.action != ParsingAction.Action.SHIFT || pAction.action != ParsingAction.Action.REDUCE) {
                    System.err.print(toString());
                    throw new IllegalStateException(String.format("Incompatible action %s already set for state %d on term %s, attempting to redefine as %s",
                            present,
                            state,
                            term.toString(),
                            pAction));
                }
            }
        }
        table.put(pair, pAction);
        if(present == null)
            pairsByState.computeIfAbsent(state, key -> new ArrayList<>()).add(pair);
    }

    /**
     * Identify which state to start at
     * @param state Starting state
     */
    public void setInitialState(int state){
        if(initialState != -1 && initialState != state)
            throw new IllegalStateException(String.format("Initial state already set to %d when trying to set to %d",
                    initialState, state));
        initialState = state;
    }

    private int panic(int state, Stack<Integer> states, Stack<SyntaxTree> symbols, TokenProvider provider, ParseTermStore store){
        while(true){
            List<ParsingPair> pairs = pairsByState.get(state);
            if(pairs != null){
                pairs = pairs.stream().filter(pair -> !pair.term.isTerminal()).collect(Collectors.toList());
                if(!pairs.isEmpty()) {
                    ParsingPair pair = pairs.get(0);
                    NonTerminalParseTerm nonTerminal = (NonTerminalParseTerm) pair.term;
                    Set<ParseTerm> followSet = nonTerminal.getFollow();
                    while (true) {
                        ParseTerm ahead = store.get(provider.next().getType().getId());
                        if (ahead.equals(store.getEndTerm()))
                            throw new LrParseException("Reached EOF before panic mode could recover");
                        if (followSet.contains(ahead)) {
                            symbols.push(new SyntaxTree(nonTerminal, 0));
                            provider.pushBack();
                            states.push(state);
                            return table.get(pair).target;
                        }
                    }
                }
            }
            if(states.isEmpty())
                throw new LrParseException("Reached end of parsing before panic mode could recover");
            state = states.pop();
            symbols.pop();
        }
    }

    /**
     * Execute this table on a stream of tokens
     * @param provider Provides tokens sequentially
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return The produced syntax tree
     */
    public SyntaxTree parse(TokenProvider provider, ProductionRuleStore ruleStore, ParseTermStore termStore){
        Stack<SyntaxTree> symbols = new Stack<>();
        Stack<Integer> states = new Stack<>();
        int state = initialState;
        boolean accepted = false;
        boolean erred = false;
        SyntaxTree lookingAt = null;
        while(!accepted){
            if(lookingAt == null)
                lookingAt = new SyntaxTree(provider.next(), termStore);
            ParsingPair pair = new ParsingPair(lookingAt.getTerm(), state);
            ParsingAction action = table.get(pair);
            if(action == null) {
                Set<String> expected = pairsByState.get(state).stream()
                        .map(p -> p.term)
                        .filter(ParseTerm::isTerminal)
                        .map(p -> p.getName()).collect(Collectors.toSet());
                new LrParseException(String.format("No matching action for state %d on term %s at %d:%d\n" +
                        "Expected one of {%s}", state, lookingAt, lookingAt.getLineNo(), lookingAt.getColNo(), String.join(", ", expected))).printStackTrace();
                erred = true;
                state = panic(state, states, symbols, provider, termStore);
                lookingAt = null;
                continue;
            }
            switch (action.action) {
                case SHIFT, GOTO -> {
                    symbols.push(lookingAt);
                    lookingAt = null;
                    states.push(state);
                    state = action.target;
                }
                case REDUCE -> {
                    ProductionRule rule = ruleStore.get(action.target);
                    ParseTerm resultTerm = rule.getResult();
                    List<SyntaxTree> children = new ArrayList<>();
                    for (int i = 0; i < rule.getTerms().size(); i++) {
                        ProductionRule.Modifier mod = rule.modifierAt(rule.getTerms().size() - 1 - i);
                        SyntaxTree child = symbols.pop();
                        switch (mod) {
                            case NONE -> children.add(child);
                            case EXPAND -> {
                                for (int j = child.numChildren() - 1; j >= 0; j--) {
                                    children.add(child.getChild(j));
                                }
                            }
                            case OMIT_EMPTY -> {
                                if (child.numChildren() != 0)
                                    children.add(child);
                            }
                        }
                        state = states.pop();
                    }
                    Collections.reverse(children);
                    lookingAt = new SyntaxTree(resultTerm, action.target, children);
                    provider.pushBack();
                }
                case ACCEPT -> accepted = true;
            }
        }
        if(symbols.size() > 1)
            throw new LrParseException("Reached EOF before parsing could finish");
        if(erred)
            throw new LrParseException("Error(s) encountered in parsing");
        return symbols.pop();
    }

    /**
     * Generates the source code for a hard-coded parser of this table
     * @param name Name of the class
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return ClassSource representing the generated class
     */
    public ClassSource generateSourceCode(String name, ProductionRuleStore ruleStore, ParseTermStore termStore){
        ClassSource source = new ClassSource(String.format("public class %s", name))
                .extend(AbstractHardcodedParser.class.getSimpleName())
                .annotate("Generated from an LR parse table for hardcoded parsing");
        StringBuilder sourceBuilder = new StringBuilder();
        source.withMethod(generateParseFunc(ruleStore));
        ClassSource.MethodSource initFunc = new ClassSource.MethodSource("public " + name)
                .withParam("TokenTypeStore", "tokenStore", "Provides token types");
        sourceBuilder.append("super(tokenStore);\n");
        for(int i = termStore.getStartTerm().getId() + 1; i < termStore.size(); i++){
            NonTerminalParseTerm nonTerminal = (NonTerminalParseTerm)termStore.get(i);
            sourceBuilder.append("termStore.registerNonTerminal(\"" + nonTerminal.getName() + "\");\n");
        }
        initFunc.withSource(sourceBuilder.toString());
        source.withMethod(initFunc);
        source.withMethod(generatePanicFunc(ruleStore, termStore));
        source.imports(TokenTypeStore.class.getCanonicalName())
                .imports(SyntaxTree.class.getCanonicalName())
                .imports(LrParseTable.class.getCanonicalName())
                .imports(TokenProvider.class.getCanonicalName())
                .imports(AbstractHardcodedParser.class.getCanonicalName())
                .imports(List.class.getCanonicalName())
                .imports(ArrayList.class.getCanonicalName())
                .imports(Collections.class.getCanonicalName())
                .imports(Stack.class.getCanonicalName());
        return source;
    }

    private ClassSource.MethodSource generateParseFunc(ProductionRuleStore ruleStore){
        ClassSource.MethodSource parseFunc = new ClassSource.MethodSource("public SyntaxTree parse")
            .withParam("TokenProvider", "provider", "Provides tokens");
        StringBuilder sourceBuilder = new StringBuilder(
                "boolean accept = false;\n" +
                        "boolean erred = false;\n" +
                        "Stack<SyntaxTree> symbols = new Stack<>();\n" +
                        "Stack<Integer> states = new Stack<>();\n" +
                        "int state = " + initialState + ";\n" +
                        "SyntaxTree lookingAt = null;\n" +
                        "while(!accept){\n" +
                        "   if(lookingAt == null)\n" +
                        "       lookingAt = new SyntaxTree(provider.next(), termStore);\n" +
                        "   switch(state){\n");
        for(Map.Entry<Integer, List<ParsingPair>> state : pairsByState.entrySet()){
            sourceBuilder.append(
                    "       case " + state.getKey() + " -> {\n" +
                            "           switch(lookingAt.getTerm().getId()){\n");
            Map<String, Set<Integer>> joinCases = new HashMap<>();
            for(ParsingPair pair : state.getValue()){
                StringBuilder subBuilder = new StringBuilder();
                ParsingAction action = table.get(pair);
                switch(action.action){
                    case GOTO, SHIFT -> subBuilder.append(
                            "                   symbols.push(lookingAt);\n" +
                                    "                   lookingAt = null;\n" +
                                    "                   states.push(state);\n" +
                                    "                   state = " + action.target + ";\n"
                    );
                    case ACCEPT -> subBuilder.append(
                            "                   accept = true;\n"
                    );
                    case REDUCE -> {
                        subBuilder.append(
                                "                   List<SyntaxTree> children = new ArrayList<>();\n" +
                                        "                   SyntaxTree temp;\n"
                        );
                        ProductionRule rule = ruleStore.get(action.target);
                        int size = rule.getTerms().size();
                        for(int i = size - 1; i >= 0; i--){
                            if(i == 0)
                                subBuilder.append(
                                        "                   state = states.pop();\n"
                                );
                            else
                                subBuilder.append(
                                        "                   states.pop();\n"
                                );
                            ProductionRule.Modifier mod = rule.modifierAt(i);
                            if(mod != ProductionRule.Modifier.OMIT)
                                subBuilder.append(
                                        "                   temp = symbols.pop();\n"
                                );
                            else
                                subBuilder.append(
                                        "                   symbols.pop();\n"
                                );
                            switch(mod){
                                case NONE -> subBuilder.append(
                                        "                   children.add(temp);\n"
                                );
                                case OMIT_EMPTY -> subBuilder.append(
                                        "                   if(temp.numChildren() > 0)\n" +
                                                "                       children.add(temp);\n"
                                );
                                case EXPAND -> subBuilder.append(
                                        "                   for(int i = temp.numChildren() - 1; i >= 0; i--)\n" +
                                                "                       children.add(temp.getChild(i));\n"
                                );
                            }
                        }
                        subBuilder.append(
                                "                   provider.pushBack();\n" +
                                        "                   Collections.reverse(children);\n" +
                                        "                   lookingAt = new SyntaxTree(termStore.get(" + rule.getResult().getId() + "), " + action.target + ", children);\n");
                    }
                }
                joinCases.computeIfAbsent(subBuilder.toString(), key -> new HashSet<>()).add(pair.term.getId());
            }
            for(Map.Entry<String, Set<Integer>> entry : joinCases.entrySet()){
                sourceBuilder.append(
                        "               case "
                ).append(entry.getValue().stream().map(String::valueOf).collect(Collectors.joining(", ")))
                        .append(" -> {\n")
                        .append(entry.getKey())
                        .append(
                                "               }\n"
                        );
            }
            sourceBuilder.append(
                    "               default -> {\n" +
                            "                   state = panic(state, lookingAt, states, symbols, provider);\n" +
                            "                   lookingAt = null;\n" +
                            "                   erred = true;\n" +
                            "               }\n" +
                            "           }\n" +
                            "       }\n");
        }
        sourceBuilder.append(
                "       default -> {\n" +
                        "           state = panic(state, lookingAt, states, symbols, provider);\n" +
                        "           lookingAt = null;\n" +
                        "           erred = true;\n" +
                        "       }\n" +
                        "   }\n" +
                        "}\n" +
                        "if(erred){\n" +
                        "   throw new LrParseTable.LrParseException(\"Error(s) encountered while parsing\");\n" +
                        "}\n" +
                        "return symbols.pop();\n");
        parseFunc.withSource(sourceBuilder.toString());
        return parseFunc;
    }

    private ClassSource.MethodSource generatePanicFunc(ProductionRuleStore ruleStore, ParseTermStore termStore){
        StringBuilder sourceBuilder = new StringBuilder();
        StringBuilder subBuilder = new StringBuilder();
        ClassSource.MethodSource method = new ClassSource.MethodSource("public int panic")
                .withParam("int", "state")
                .withParam("SyntaxTree", "symbol")
                .withParam("Stack<Integer>", "states")
                .withParam("Stack<SyntaxTree>", "symbols")
                .withParam("TokenProvider", "provider");
        sourceBuilder.append(
                "StringBuilder expectedBuilder = new StringBuilder();\n" +
                "switch(state){\n");
        Map<String, Set<Integer>> switchCases = new HashMap<>();
        for(Map.Entry<Integer, List<ParsingPair>> state : pairsByState.entrySet()){
            int id = state.getKey();
            List<String> expectedTerminals = state.getValue().stream()
                    .map(pair -> pair.term)
                    .filter(ParseTerm::isTerminal)
                    .filter(term -> !term.equals(termStore.getEndTerm()))
                    .map(ParseTerm::getName)
                    .collect(Collectors.toList());
            String key =
                "       expectedBuilder.append(\"" + String.join(", ", expectedTerminals) + "\");\n";
            switchCases.computeIfAbsent(key, k -> new HashSet<>()).add(id);
        }
        for(Map.Entry<String, Set<Integer>> switchCase : switchCases.entrySet()){
            sourceBuilder.append(
                "   case " + switchCase.getValue().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + " -> {\n");
            sourceBuilder.append(switchCase.getKey());
            sourceBuilder.append(
                "   }\n");
        }
        sourceBuilder.append(
                "}\n" +
                "System.err.printf(\"No matching action for symbol %s on state %d at %d:%d\\nExpected one of {%s}\\n\",\n" +
                        "symbol, state, symbol.getLineNo(), symbol.getColNo(), expectedBuilder.toString());\n" +
                "while(true){\n" +
                "   int targetNonTerminal = -1;\n" +
                "   int targetState = -1;\n" +
                "   switch(state){\n");
        switchCases.clear();
        for(Map.Entry<Integer, List<ParsingPair>> state : pairsByState.entrySet()){
            for(ParsingPair ntPair : state.getValue().stream()
                    .filter(pair -> !pair.term.isTerminal())
                    .collect(Collectors.toList())){
                subBuilder.append(
                "           targetNonTerminal = " + ntPair.term.getId() + ";\n" +
                "           targetState = " + table.get(ntPair).target + ";\n");
                break;
            }
            switchCases.computeIfAbsent(subBuilder.toString(), key -> new HashSet<>()).add(state.getKey());
            subBuilder.setLength(0);
        }
        for(Map.Entry<String, Set<Integer>> switchCase : switchCases.entrySet()){
            sourceBuilder.append(
                "       case " + switchCase.getValue().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + " -> {\n");
            sourceBuilder.append(switchCase.getKey());
            sourceBuilder.append(
                "       }\n");
        }
        sourceBuilder.append(
                "   }\n" +
                "   if(targetNonTerminal != -1){\n" +
                "       while(true){\n" +
                "           int ahead = provider.next().getType().getId();\n" +
                "           if(ahead == " + termStore.getEndTerm().getId() + "){\n" +
                "               throw new LrParseTable.LrParseException(\"EOF reached during panic mode\");\n" +
                "           }\n" +
                "           boolean inFollow = false;\n" +
                "           switch(targetNonTerminal){\n");
        switchCases.clear();
        for(int i = termStore.getStartTerm().getId() + 1; i < termStore.size(); i++){
            Set<String> followIds = ((NonTerminalParseTerm)termStore.get(i)).getFollow().stream()
                    .map(term -> String.valueOf(term.getId())).collect(Collectors.toSet());
            if(!followIds.isEmpty()){
                subBuilder.append(
                "                   switch(ahead){\n" +
                "                       case " + ((NonTerminalParseTerm)termStore.get(i)).getFollow().stream()
                    .map(term -> String.valueOf(term.getId())).collect(Collectors.joining(", ")) +
                        " -> inFollow = true;\n" +
                "                   }\n");
                switchCases.computeIfAbsent(subBuilder.toString(), key -> new HashSet<>()).add(i);
                subBuilder.setLength(0);
            }
        }
        for(Map.Entry<String, Set<Integer>> switchCase : switchCases.entrySet()){
            sourceBuilder.append(
                "               case " + switchCase.getValue().stream()
                    .map(String::valueOf).collect(Collectors.joining(", ")) + " ->{\n"
            )
                    .append(switchCase.getKey())
                    .append(
                "               }\n");
        }
        sourceBuilder.append(
                "           }\n");
        sourceBuilder.append(
                "           if(inFollow){\n" +
                "               symbols.push(new SyntaxTree(termStore.get(targetNonTerminal), 0));\n" +
                "               states.push(state);\n" +
                "               provider.pushBack();\n" +
                "               return targetState;\n" +
                "           }\n" +
                "       }\n" +
                "   }\n" +
                "   if(states.isEmpty())\n" +
                "       throw new LrParseTable.LrParseException(\"Parse stack emptied during error recovery\");\n" +
                "   state = states.pop();\n" +
                "   symbols.pop();\n" +
                "}\n");
        return method.withSource(sourceBuilder.toString());
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        Map<Integer, Map<Integer, ParsingAction>> states = new TreeMap<>();
        for(Map.Entry<ParsingPair, ParsingAction> entry : table.entrySet()){
            states.computeIfAbsent(entry.getKey().state, key -> new TreeMap<>()).put(entry.getKey().term.getId(), entry.getValue());
        }
        for(int state : states.keySet()){
            Map<Integer, ParsingAction> actions = states.get(state);
            builder.append(state).append(": ").append(actions).append("\n");
        }
        builder.append("@").append(initialState);
        return builder.toString();
    }

    /**
     * Create a parse table from the set of LR(1) items
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @param progressiveLalr Whether to merge LALR items on-the-fly
     * @return The generated parse table
     */
    public static LrParseTable naiveLr1Table(ProductionRuleStore ruleStore, ParseTermStore termStore, boolean progressiveLalr){
        Set<Set<LrOneItem>> lrOne = ruleStore.canonicalLr1Sets(termStore, progressiveLalr);
        LrOneSetStore setStore = new LrOneSetStore(lrOne);
        return setStore.generateParseTable(ruleStore, termStore);
    }

    /**
     * Create a LALR(1) parse table
     * @deprecated This function seems to only properly produce parse tables for a subset of LALR(1) grammars
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return Parse table from set of LALR(1) items
     */
    @Deprecated
    public static LrParseTable lalr1Table(ProductionRuleStore ruleStore, ParseTermStore termStore){
        Set<Set<LrZeroItem>> zeroItems = LrZeroItem.kernelsOnly(ruleStore.canonicalLr0Sets(termStore), termStore);
        LrOneSetStore setStore = new LrOneSetStore(zeroItems, ruleStore, termStore);
        return setStore.generateParseTable(ruleStore, termStore);
    }

    public static int panic(int state,
                            Stack<Integer> states,
                            Stack<SyntaxTree> symbols,
                            TokenProvider provider,
                            Map<Integer, Set<Integer>> followSets,
                            Map<Integer, Map<Integer, Integer>> gotoSets,
                            Map<Integer, Set<Integer>> expectSets,
                            ParseTermStore store){
        while(true){
        Map<Integer, Integer> pairs = gotoSets.get(state);
        if(pairs != null){
            if(!pairs.isEmpty()) {
                int termId = pairs.keySet().stream().findFirst().get();
                NonTerminalParseTerm nonTerminal = (NonTerminalParseTerm) store.get(termId);
                Set<Integer> followSet = followSets.get(termId);
                while (true) {
                    ParseTerm ahead = store.get(provider.next().getType().getId());
                    if (ahead.equals(store.getEndTerm()))
                        throw new LrParseException("Reached EOF before panic mode could recover");
                    if (followSet.contains(ahead.getId())) {
                        symbols.push(new SyntaxTree(nonTerminal, 0));
                        provider.pushBack();
                        states.push(state);
                        return pairs.get(termId);
                    }
                }
            }
        }
        if(states.isEmpty())
            throw new LrParseException("Reached end of parsing before panic mode could recover");
        state = states.pop();
        symbols.pop();
    }
    }

}
