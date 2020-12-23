package com.funguscow.gible.lexer;

import com.funguscow.gible.codegen.ClassSource;
import com.funguscow.gible.lexer.regex.Dfa;
import com.funguscow.gible.lexer.regex.tree.RegexTree;
import com.funguscow.gible.parser.SyntaxTree;
import com.funguscow.gible.util.CodePointHelper;
import com.funguscow.gible.util.CodePointStream;

import java.util.*;
import java.util.stream.Collectors;

/**
 * One of one or more states in a lexer
 * Has with it an associated
 */
public class LexerState {

    private final Dfa dfa;
    private final Map<Integer, LexerTokenRule> rules;
    private final Map<Integer, Integer> tokenIds;

    public LexerState(SyntaxTree... trees){
        dfa = new Dfa(RegexTree.regexOf(trees));
        dfa.minimize();
        rules = new HashMap<>();
        tokenIds = new HashMap<>();
    }

    public LexerState setRule(int i, LexerTokenRule rule){
        rules.put(i, rule);
        return this;
    }

    public LexerState setToken(int i, int t){
        tokenIds.put(i, t);
        return this;
    }

    public Dfa.DfaMatch process(CodePointStream input, StringBuilder lexeme, Lexer machine){
        Dfa.DfaMatch result = dfa.execute(input);
        if(result.acceptance != -1){
            LexerTokenRule rule = rules.getOrDefault(result.acceptance, LexerTokenRule.DEFAULT_RULE);
            for(LexerTokenRule.Action action : rule.getActions()){
                switch(action.action){
                    case APPEND -> lexeme.append(result.lexeme);
                    case CONST -> {
                        for(int i : action.args)
                            lexeme.append(CodePointHelper.asSingleString(i));
                    }
                    case STATE -> machine.setState(action.args[0]);
                    case RETURN -> {
                        int t = action.args[0];
                        if(t == -1)
                            t = tokenIds.get(result.acceptance);
                        machine.returnToken(t);
                    }
                }
            }
        }
        else{
            lexeme.setLength(0);
            lexeme.append(result.lexeme);
        }
        return result;
    }

    public ClassSource.MethodSource generateDfaTransFunc(int stateId){
        return dfa.generateNextStateMethod(stateId);
    }

    public ClassSource.MethodSource generateDfaAcceptFunc(int stateId){
        return dfa.generateAcceptMethod(stateId);
    }

    public ClassSource.MethodSource generateFunctionSource(int stateId){
        ClassSource.MethodSource method = new ClassSource.MethodSource(String.format("private Dfa.DfaMatch process_%d", stateId))
                .withParam("CodePointStream", "input");
        StringBuilder source = new StringBuilder();
        String tFunc = String.format("this::transitionFor_%d", stateId);
        String aFunc = String.format("this::acceptFor_%d", stateId);
        source.append(
                "Dfa.DfaMatch match = Dfa.hardExecute(input, " + tFunc + ", " + aFunc + ", " + dfa.getInit() + ");\n" +
                "switch(match.acceptance){\n" +
                "   case -1 -> {\n" +
                "      lexeme.setLength(0);\n" +
                "      lexeme.append(match.lexeme);\n" +
                "   }\n");
        StringBuilder subBuilder = new StringBuilder();
        Map<String, Set<Integer>> switchCases = new HashMap<>();
        for(Map.Entry<Integer, LexerTokenRule> option : rules.entrySet()){
            for(LexerTokenRule.Action action : option.getValue().getActions()){
                switch(action.action){
                    case RETURN -> {
                        int tokenId = action.args[0];
                        if(tokenId == -1)
                            tokenId = tokenIds.get(option.getKey());
                        subBuilder.append(
                "       nextTokenId = " + tokenId + ";\n");
                    }
                    case STATE -> subBuilder.append(
                "       state = " + action.args[0] + ";\n");
                    case APPEND -> subBuilder.append(
                "       lexeme.append(match.lexeme);\n");
                    case CONST -> subBuilder.append(
                "       lexeme.append(\"" + Arrays.stream(action.args)
                        .boxed()
                        .map(i -> {
                            if(i >= 0x10000) {
                                i -= 0x10000;
                                return String.format("\\u%04x\\u%04x", (i >> 10) + 0xD800, (i & 0x3ff) + 0xDC00);
                            }
                            else if(i == (int)'\n')
                                return "\\n";
                            else if(i == (int)'\r')
                                return "\\r";
                            else if(i == (int)'"')
                                return "\\\"";
                            else if(i == (int)'\\')
                                return "\\\\";
                            else return String.format("\\u%04x", i);
                        })
                        .collect(Collectors.joining()) + "\");\n");
                }
            }
            switchCases.computeIfAbsent(subBuilder.toString(), key -> new HashSet<>()).add(option.getKey());
            subBuilder.setLength(0);
        }
        for(Map.Entry<Integer, Integer> defPair : tokenIds.entrySet()){
            subBuilder.append(
                "       lexeme.append(match.lexeme);\n" +
                "       nextTokenId = " + defPair.getValue() + ";\n");
            switchCases.computeIfAbsent(subBuilder.toString(), key -> new HashSet<>()).add(defPair.getKey());
            subBuilder.setLength(0);
        }
        for(Map.Entry<String, Set<Integer>> switchCase : switchCases.entrySet()){
            source.append(
                "   case " + switchCase.getValue().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + " -> {\n")
                    .append(switchCase.getKey()).append(
                "   }\n");
        }
        source.append(
                "}\n" +
                "return match;\n");
        return method.withSource(source.toString());
    }

}
