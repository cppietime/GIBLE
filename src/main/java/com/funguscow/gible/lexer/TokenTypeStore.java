package com.funguscow.gible.lexer;

import com.funguscow.gible.IndexNameStore;

/**
 * Store of token types
 */
public class TokenTypeStore extends IndexNameStore<TokenType> {

    public TokenTypeStore(){
        super();
        registerToken(TokenType.EOF);
        registerToken(TokenType.EPSILON);
        registerToken(TokenType.PROPAGATION);
    }

    /**
     *
     * @param name Name of new token
     * @return ID of newly registered token
     */
    public int registerToken(String name){
        TokenType tokenType = new TokenType(size(), name);
        return addWithName(tokenType, name);
    }

    /**
     *
     * @param tokenType Name of new token
     * @return ID of newly registered token
     */
    public int registerToken(TokenType tokenType){
        return addWithName(tokenType, tokenType.getName());
    }

}
