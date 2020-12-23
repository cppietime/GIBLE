package com.funguscow.gible.lexer.provider;

import com.funguscow.gible.lexer.Token;
import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.util.CodePointStream;

/**
 * Abstract superclass of any token provider that reads from a CodePointStream
 */
public abstract class CodePointTokenProvider extends TokenProvider {

    protected CodePointStream source;

    /**
     * @param store Store of token types, used to associated tokens with names and IDs
     */
    public CodePointTokenProvider(TokenTypeStore store, CodePointStream source) {
        super(store);
        this.source = source;
    }

    public Token getToken(){
        return innerToken().setLineNo(source.getLineno()).setColNo(source.getColno());
    }

    protected abstract Token innerToken();
}
