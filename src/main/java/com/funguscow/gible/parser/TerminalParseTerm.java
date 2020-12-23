package com.funguscow.gible.parser;

import com.funguscow.gible.lexer.TokenType;

import java.util.Set;

/**
 * Terminal parse term corresponding to a token type
 */
public class TerminalParseTerm extends ParseTerm {

    private final TokenType tokenType;

    /**
     * Construct a parse term from a token type
     * @param tokenType Token type for term
     */
    public TerminalParseTerm(TokenType tokenType){
        this.tokenType = tokenType;
    }

    @Override
    public boolean isTerminal(){
        return true;
    }

    @Override
    public int hashCode(){
        return tokenType.hashCode() * 13 + 1;
    }

    @Override
    public boolean equals(Object other){
        if(this == other)
            return true;
        if(other == null)
            return false;
        if(!(other instanceof TerminalParseTerm))
            return false;
        return tokenType == ((TerminalParseTerm)other).tokenType;
    }

    @Override
    public String getName(){
        return tokenType.getName();
    }

    @Override
    public Set<ParseTerm> getFirst() {
        return Set.of(this);
    }

    @Override
    public int getId(){
        return tokenType.getId();
    }
}
