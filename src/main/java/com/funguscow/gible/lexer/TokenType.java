package com.funguscow.gible.lexer;

/**
 * Type of token for lexical analysis
 */
public class TokenType {

    public static final TokenType EOF = new TokenType(0, "_EOF"),
            EPSILON = new TokenType(1, "_EPISLON"),
            PROPAGATION = new TokenType(2, "_PROPAGATION");

    private final int id;
    private final String name;
    private boolean hasLexeme = false;

    /**
     *
     * @param id Token numeric ID
     * @param name Token name
     */
    public TokenType(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Identify this token type to have a lexeme
     * @return this
     */
    public TokenType withLexeme(){
        hasLexeme = true;
        return this;
    }

    /**
     *
     * @return true if this token type has a lexeme
     */
    public boolean doesHaveLexeme(){
        return hasLexeme;
    }

    @Override
    public int hashCode(){
        return id;
    }

    /**
     *
     * @return The numeric ID of this token
     */
    public int getId(){
        return id;
    }

    /**
     *
     * @return This token's name
     */
    public String getName(){
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof TokenType))
            return false;
        TokenType other = (TokenType)obj;
        return name.equals(other.name) && id == other.id;
    }
}
