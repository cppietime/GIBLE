package com.funguscow.gible.parser;

import com.funguscow.gible.IndexNameStore;
import com.funguscow.gible.lexer.TokenType;
import com.funguscow.gible.lexer.TokenTypeStore;

/**
 * Stores parse terms
 */
public class ParseTermStore extends IndexNameStore<ParseTerm> {

    public static class InvalidParseTermRegistrationException extends RuntimeException{
        public InvalidParseTermRegistrationException(String message){
            super(message);
        }
    }

    /**
     * Register all terminal tokens from a TokenTypeStore and starting non-terminal
     * @param tokenTypeStore Store of tokens
     */
    public void registerTerminalsFromTokenTypes(TokenTypeStore tokenTypeStore){
        if(!list.isEmpty())
            throw new InvalidParseTermRegistrationException("Attempt to register terminals after already registering parse terms");
        for(IndexNameStoreEntry<TokenType> entry : tokenTypeStore){
            ParseTerm terminal = new TerminalParseTerm(entry.entry);
            addWithName(terminal, entry.name);
        }
        addWithName(new NonTerminalParseTerm(size(), NonTerminalParseTerm.START), NonTerminalParseTerm.START);
    }

    /**
     * Register a non-terminal parse term by a name
     * @param name Name of term
     * @return ID of new term
     */
    public int registerNonTerminal(String name){
        ParseTerm nonTerminal = new NonTerminalParseTerm(size(), name);
        return addWithName(nonTerminal, name);
    }

    /**
     *
     * @return The starting non-terminal term S'
     */
    public NonTerminalParseTerm getStartTerm(){
        return (NonTerminalParseTerm)get(NonTerminalParseTerm.START);
    }

    /**
     *
     * @return The terminal term for the empty string epsilon
     */
    public TerminalParseTerm getEpsilonTerm(){
        return (TerminalParseTerm)get(TokenType.EPSILON.getName());
    }

    /**
     *
     * @return The terminal term for EOF $
     */
    public TerminalParseTerm getEndTerm(){
        return (TerminalParseTerm)get(TokenType.EOF.getName());
    }

    /**
     *
     * @return The placeholder terminal term for propagation testing #
     */
    public TerminalParseTerm getPropTerm(){
        return (TerminalParseTerm)get(TokenType.PROPAGATION.getName());
    }

}
