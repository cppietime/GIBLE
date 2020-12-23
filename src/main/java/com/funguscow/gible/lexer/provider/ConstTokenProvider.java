package com.funguscow.gible.lexer.provider;

import com.funguscow.gible.lexer.Token;
import com.funguscow.gible.lexer.TokenType;
import com.funguscow.gible.lexer.TokenTypeStore;

import java.util.Iterator;

/**
 * Provides a stream of tokens from a constant iterator given
 */
public class ConstTokenProvider extends TokenProvider {

    private final Iterator<Token> tokens;

    /**
     *
     * @param tokens Any iterator with defined tokens in-order
     * @param store Store of token types
     */
    public ConstTokenProvider(Iterator<Token> tokens, TokenTypeStore store){
        super(store);
        this.tokens = tokens;
    }

    @Override
    protected Token getToken() {
        if(!tokens.hasNext())
            return new Token(TokenType.EOF);
        return tokens.next();
    }
}
