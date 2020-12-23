package com.funguscow.gible.lexer.regex;

import com.funguscow.gible.codegen.ClassSource;
import com.funguscow.gible.lexer.regex.tree.RegexLeaf;
import com.funguscow.gible.lexer.regex.tree.RegexTree;
import com.funguscow.gible.util.CodePointHelper;
import com.funguscow.gible.util.CodePointStream;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Deterministic Finite-state Automaton for matching regular expressions
 */
public class Dfa {

    private Map<DfaState, Integer> stateIds;
    private List<DfaState> states;
    private DfaState initState;

    /**
     * Construct a new DFA from a provided Regex
     * @param regex A parsed Regex
     */
    public Dfa(RegexTree regex){
        stateIds = new HashMap<>();
        states = new ArrayList<>();
        Queue<DfaState> unmarked = new ArrayDeque<>();
        Map<Integer, RegexLeaf> positions = new HashMap<>();
        Map<Set<Integer>, Integer> stateSets = new HashMap<>();
        regex.indexDfs(0, positions);
        regex.calcFollow(positions);
        DfaState startState = new DfaState(regex.getFirst(), positions);
        unmarked.add(startState);
        stateIds.put(startState, 0);
        states.add(startState);
        while(!unmarked.isEmpty()){
            DfaState current = unmarked.poll();
            stateSets.put(current.getPositions(), stateIds.get(current));
            for(CRange transition : current.getTransitions()){
                DfaState next = new DfaState(transition.getValues(), positions);
                if(!stateIds.containsKey(next)){
                    stateIds.put(next, states.size());
                    states.add(next);
                    unmarked.add(next);
                }
            }
        }
        stateIds.clear();
        for (int i = 0; i < states.size(); i++) {
            DfaState state = states.get(i);
            Set<CRange> transitions = state.getTransitions();
            for (CRange transition : transitions) {
                Set<Integer> target = transition.getValues();
                int j = stateSets.get(target);
                target.clear();
                target.add(j);
            }
            stateIds.put(state, i);
        }
        initState = states.get(0);
    }

    /**
     * Used internally to test if a state is in a group given a partition
     * @param a State to test
     * @param bset Group in partition
     * @param partition Overall partition
     * @return True if a is in bset in partition
     */
    private boolean inSamePartition(DfaState a, Set<DfaState> bset, Set<Set<DfaState>> partition){
        if(bset.isEmpty())
            return true;
        DfaState b = bset.stream().findFirst().get();
        Set<CRange> aTran = a.getTransitions(), bTran = b.getTransitions();
        if(aTran.size() != bTran.size())
            return false;
        Iterator<CRange> bIter = bTran.iterator();
        for(CRange tranA : aTran){
            CRange tranB = bIter.next();
            if(tranA.getMin() != tranB.getMin())
                return false;
            if(tranA.getMax() != tranB.getMax())
                return false;
            if(tranA.doesMark() != tranB.doesMark())
                return false;
            DfaState gotoA = states.get(tranA.getValues().stream().findFirst().get());
            DfaState gotoB = states.get(tranB.getValues().stream().findFirst().get());
            for(Set<DfaState> group : partition){
                if(group.contains(gotoA) && !group.contains(gotoB))
                    return false;
            }
        }
        return true;
    }

    /**
     * Merges equivalent states
     * Can reduce size/number of states but may be computationally costly
     */
    public void minimize(){
        Map<Integer, Set<DfaState>> initialPartitions = new HashMap<>();
        Set<Set<DfaState>> partition, swap = new HashSet<>(), subs = new HashSet<>(), temp;
        for (DfaState state : states) {
            initialPartitions.computeIfAbsent(state.getAccepts() + 1, key -> new HashSet<>()).add(state);
        }
        partition = new HashSet<>(initialPartitions.values());
        initialPartitions.clear();
        boolean didChange;
        do{
            didChange = false;
            for(Set<DfaState> group : partition){
                for(DfaState state : group) {
                    Set<DfaState> belongsTo = null;
                    for (Set<DfaState> candidate : subs) {
                        if (inSamePartition(state, candidate, partition)){
                            belongsTo = candidate;
                            break;
                        }
                    }
                    if(belongsTo == null){
                        Set<DfaState> add = new HashSet<>();
                        add.add(state);
                        subs.add(add);
                    }
                    else {
                        subs.remove(belongsTo);
                        belongsTo.add(state);
                        subs.add(belongsTo);
                    }
                }
                if(subs.size() > 1)
                    didChange = true;
                swap.addAll(subs);
                subs.clear();
            }
            temp = swap;
            swap = partition;
            partition = temp;
            swap.clear();
        }while(didChange);
        List<Set<DfaState>> groups = new ArrayList<>(partition);
        Map<DfaState, Integer> finalGroupIds = new HashMap<>();
        for(int i = 0; i < groups.size(); i++){
            Set<DfaState> group = groups.get(i);
            for(DfaState state : group)
                finalGroupIds.put(state, i);
        }
        List<DfaState> newStates = new ArrayList<>();
        stateIds.clear();
        for(int i = 0; i < groups.size(); i++){
            Set<DfaState> group = groups.get(i);
            DfaState repr = group.stream().findFirst().get();
            DfaState newState = new DfaState(repr);
            for(CRange range : repr.getTransitions()){
                Set<Integer> values = range.getValues();
                Set<Integer> mapped = values.stream()
                        .map(v -> finalGroupIds.get(states.get(v)))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                newState.getTransitions().add(new CRange(range.getMin(), range.getMax(), mapped).mark(range.doesMark()));
            }
            newStates.add(newState);
            stateIds.put(newState, i);
            if(inSamePartition(initState, group, partition))
                initState = newState;
        }
        states = newStates;
        System.out.println("IS = " + stateIds.get(initState));
    }

    /**
     * Execute this DFA on an input
     * @param stream Input code points
     * @return The match containing the index, length, and lexeme of matched term
     */
    public DfaMatch execute(CodePointStream stream){
        DfaState state = initState;
        DfaMatch latest = new DfaMatch(-1, 0);
        Deque<Integer> consumed = new ArrayDeque<>();
        int length = 0;
        boolean marked = false;
        boolean markedLast = false;
        while(state != null){
            int symbol;
            try {
                symbol = stream.read();
            }catch(Exception e){
                e.printStackTrace();
                symbol = -1;
            }
            CRange on = state.getNextStateFor(symbol);
            state = null;
            if(on != null)
                state = states.get(on.getValues().stream().findFirst().get());
            if(state == null){ // No transition accepted
                stream.pushBack(symbol);
                break;
            }
            // Transition successful
            if(markedLast && !on.doesMark()){
                marked = true;
                length = consumed.size();
            }
            consumed.addLast(symbol);
            if(!marked)
                length++;
            markedLast = on.doesMark();
            int acc = state.getAccepts();
            if(acc != -1){
                if(markedLast)
                    length = consumed.size();
                latest = new DfaMatch(acc, length);
            }
        }
        if(latest.acceptance == -1)
            latest.length = consumed.size();
        length = latest.length;
        while(consumed.size() > length)
            stream.pushBack(consumed.pollLast());
        latest.setLexeme(CodePointHelper.codePointsToString(consumed));
        return latest;
    }

    public int getInit(){
        return stateIds.get(initState);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(String.format("Size %d\n", states.size()));
        for(int i = 0; i < states.size(); i++){
            if(states.get(i) == initState)
                builder.append(" --> ");
            builder.append(String.format("%d: %s\n", i, states.get(i)));
        }
        return builder.toString();
    }

    /**
     * Indicates a match made on a DFA
     */
    public static class DfaMatch{
        public int acceptance;
        public int length;
        public String lexeme;
        public DfaMatch(int a, int l){
            acceptance = a;
            length = l;
            lexeme = null;
        }
        public DfaMatch setLexeme(String l){
            lexeme = l;
            return this;
        }
        public String toString(){
            return String.format("%d,%d[%s]", acceptance, length, lexeme);
        }
    }

    public ClassSource.MethodSource generateNextStateMethod(int stateNum){
        ClassSource.MethodSource method = new ClassSource.MethodSource(String.format("private CRange transitionFor_%d", stateNum))
                .withParam("int", "currentState")
                .withParam("int", "symbol");
        StringBuilder sourceBuilder = new StringBuilder();
        sourceBuilder.append(
                    "switch(currentState){\n");
        for(int i = 0; i < states.size(); i++){
            DfaState state = states.get(i);
            sourceBuilder.append(
                    "   case " + i + " -> {\n");
            for(CRange range : state.getTransitions()){
                sourceBuilder.append(
                    "       if(symbol >= " + range.getMin() + " && symbol < " + range.getMax() + "){\n" +
                    "           return new CRange(" + range.getMax() + ", " + range.getMax() + ", " +
                            range.getValues().stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")" +
                            (range.doesMark() ? ".mark(true)" : "") + ";\n" +
                    "       }\n");
            }
            sourceBuilder.append(
                    "   }\n");
        }
        sourceBuilder.append(
                    "}\n" +
                    "return null;");
        return method.withSource(sourceBuilder.toString());
    }

    public ClassSource.MethodSource generateAcceptMethod(int stateNum){
        ClassSource.MethodSource method = new ClassSource.MethodSource(String.format("private int acceptFor_%d", stateNum))
                .withParam("int", "state");
        StringBuilder source = new StringBuilder();
        source.append(
                "switch(state){\n");
        Map<Integer, Set<Integer>> accepts = new HashMap<>();
        stateIds.forEach((key1, value) -> accepts.computeIfAbsent(key1.getAccepts(), key -> new HashSet<>()).add(value));
        for(Map.Entry<Integer, Set<Integer>> switchCase : accepts.entrySet()){
            source.append(
                "   case " +
                    switchCase.getValue().stream().map(String::valueOf).collect(Collectors.joining(", ")) +
                    " -> {return " + switchCase.getKey() + ";}\n");
        }
        source.append(
                "}\n" +
                "return -1;");
        return method.withSource(source.toString());
    }

    public static DfaMatch hardExecute(CodePointStream stream, StateTransition transitor, StateAcceptor acceptor, int state){
        DfaMatch latest = new DfaMatch(-1, 0);
        Deque<Integer> consumed = new ArrayDeque<>();
        int length = 0;
        boolean marked = false;
        boolean markedLast = false;
        while(state != -1){
            int symbol;
            try {
                symbol = stream.read();
            }catch(Exception e){
                e.printStackTrace();
                symbol = -1;
            }
            CRange on = transitor.execute(state, symbol);
            state = -1;
            if(on != null)
                state = on.getValues().stream().findFirst().get();
            if(state == -1){ // No transition accepted
                stream.pushBack(symbol);
                break;
            }
            // Transition successful
            if(markedLast && !on.doesMark()){
                marked = true;
                length = consumed.size();
            }
            consumed.addLast(symbol);
            if(!marked)
                length++;
            markedLast = on.doesMark();
            int acc = acceptor.execute(state);
            if(acc != -1){
                if(markedLast)
                    length = consumed.size();
                latest = new DfaMatch(acc, length);
            }
        }
        if(latest.acceptance == -1)
            latest.length = consumed.size();
        length = latest.length;
        while(consumed.size() > length)
            stream.pushBack(consumed.pollLast());
        latest.setLexeme(CodePointHelper.codePointsToString(consumed));
        return latest;
    }

    public interface StateTransition{
        CRange execute(int state, int symbol);
    }

    public interface StateAcceptor{
        int execute(int state);
    }

}
