package com.funguscow.gible.lexer.provider;

import com.funguscow.gible.lexer.Token;
import com.funguscow.gible.lexer.TokenTypeStore;

/**
 * Provides lexed tokens
 */
public abstract class TokenProvider {

    protected final TokenTypeStore store;
    private Token cache = null;
    private boolean pushedBack = false;

    /**
     *
     * @param store Store of token types, used to associated tokens with names and IDs
     */
    public TokenProvider(TokenTypeStore store){
        this.store = store;
    }

    /**
     *
     * @return This type store
     */
    public TokenTypeStore getStore(){
        return store;
    }

    /**
     *
     * @return The next token
     */
    public Token next(){
        Token ret;
        if(pushedBack) {
            ret = cache;
            pushedBack = false;
        }
        else{
            ret = cache = getToken();
        }
        return ret;
    }

    /**
     * Revert to previous token
     */
    public void pushBack(){
        if(pushedBack && cache != null)
            throw new IllegalStateException(String.format("TokenProvider already has pushed-back token %s", cache));
        if(cache == null)
            throw new IllegalStateException("Attempt to push back token before any tokens generated");
        pushedBack = true;
    }

    /**
     * Method to read next token
     * @return Read token
     */
    protected abstract Token getToken();

}
