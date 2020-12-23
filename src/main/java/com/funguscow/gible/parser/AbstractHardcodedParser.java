package com.funguscow.gible.parser;

import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.lexer.provider.TokenProvider;
import com.funguscow.gible.parser.lr.LrParseTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Base class that handles some common logic for hardcoded parsers
 */
public abstract class AbstractHardcodedParser {

    protected ParseTermStore termStore;
    protected Map<Integer, Set<Integer>> followSets, expectSets;
    protected Map<Integer, Map<Integer, Integer>> gotoSets;

    public AbstractHardcodedParser(TokenTypeStore store){
        termStore = new ParseTermStore();
        followSets = new HashMap<>();
        expectSets = new HashMap<>();
        gotoSets = new HashMap<>();
        termStore.registerTerminalsFromTokenTypes(store);
    }

    /**
     * Called when an error is encountered
     * @param state Current state ID
     * @param lookingAt Current looking-at symbol
     * @param states Stack of states
     * @param symbols Stack of processed symbols
     * @param provider Input tokens
     * @return Next state
     */
    protected int panic(int state,
                              SyntaxTree lookingAt,
                              Stack<Integer> states,
                              Stack<SyntaxTree> symbols,
                              TokenProvider provider){
        if(lookingAt.getTerm().isTerminal())
            new LrParseTable.LrParseException(String.format("No matching action on state %d for symbol %s at %d:%d\n" +
                    "Expected one of {%s}", state, lookingAt, lookingAt.getLineNo(), lookingAt.getColNo(),
                    expectSets.getOrDefault(state, Set.of()).stream().map(i -> termStore.get(i).getName()).collect(Collectors.joining(", "))))
                    .printStackTrace();
        else {
            System.err.println(states);
            new LrParseTable.LrParseException(String.format("Found invalid non-terminal %s for state %d", lookingAt.getTerm(), state)).printStackTrace();
        }
        return LrParseTable.panic(state, states, symbols, provider, followSets, gotoSets, expectSets, termStore);
    }

    /**
     * Parses a stream of input tokens and returns the result, if successful
     * @param provider Provides lexed input tokens
     * @return Derived syntax tree
     */
    public abstract SyntaxTree parse(TokenProvider provider);

}
