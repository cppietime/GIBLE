package com.funguscow.gible.lexer.regex;

import com.funguscow.gible.lexer.provider.RegexTokenProvider;
import com.funguscow.gible.parser.ParseTermStore;

public class RegexParseTermStore extends ParseTermStore {

    public static int regex, regex_0, or_piece, or_piece_0, cat_piece, piece, char_class, char_class_0, char_range;

    private final static RegexParseTermStore instance;

    static {
        instance = new RegexParseTermStore();
    }

    public static RegexParseTermStore getInstance(){
        return instance;
    }

    private RegexParseTermStore(){
        super();
        registerTerminalsFromTokenTypes(RegexTokenProvider.getDefaultRegexTokenStore());
        regex = registerNonTerminal("regex");
        regex_0 = registerNonTerminal("regex_0");
        or_piece = registerNonTerminal("or_piece");
        or_piece_0 = registerNonTerminal("or_piece_0");
        cat_piece = registerNonTerminal("cat_piece");
        piece = registerNonTerminal("piece");
        char_class = registerNonTerminal("char_class");
        char_class_0 = registerNonTerminal("char_class_0");
        char_range = registerNonTerminal("char_range");
    }

}
