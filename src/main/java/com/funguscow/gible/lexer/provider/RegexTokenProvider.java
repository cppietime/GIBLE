package com.funguscow.gible.lexer.provider;

import com.funguscow.gible.lexer.Token;
import com.funguscow.gible.lexer.TokenType;
import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.util.CodePointHelper;
import com.funguscow.gible.util.CodePointStream;

import java.io.IOException;
import java.util.*;

/**
 * Hardcoded lexer for reading regex expressions
 */
public class RegexTokenProvider extends CodePointTokenProvider {

    public static int VBAR, LPAREN, RPAREN, LBRACK, RBRACK, MODIFIER, CARAT, CHAR, MINUS, DOT;

    private final static int START = 0, BRACK = 1;

    private static TokenTypeStore regexStore = null;

    static{
        regexStore = new TokenTypeStore();
        VBAR = regexStore.registerToken("VBAR");
        LPAREN = regexStore.registerToken("LPAREN");
        RPAREN = regexStore.registerToken("RPAREN");
        LBRACK = regexStore.registerToken("LBRACK");
        RBRACK = regexStore.registerToken("RBRACK");
        MODIFIER = regexStore.registerToken("MODIFIER");
        CARAT = regexStore.registerToken("CARAT");
        CHAR = regexStore.registerToken("CHAR");
        MINUS = regexStore.registerToken("MINUS");
        DOT = regexStore.registerToken("DOT");
        regexStore.get(CHAR).withLexeme();
        regexStore.get(MODIFIER).withLexeme();
    }

    public static TokenTypeStore getDefaultRegexTokenStore(){
        return regexStore;
    }

    private int macroState;
    private final Queue<Integer> special;
    private Set<Integer> delims;

    /**
     * @param source Source of code  points
     */
    public RegexTokenProvider(CodePointStream source) {
        super(getDefaultRegexTokenStore(), source);
        macroState = START;
        special = new ArrayDeque<>();
        delims = new HashSet<>();
    }

    /**
     * Add delimiter characters
     * @param points New delim chars
     * @return this
     */
    public RegexTokenProvider addDelims(Integer... points){
        delims.addAll(Arrays.asList(points));
        return this;
    }

    private void queueClass(String inner){
        if(macroState == START)
            special.add((int)'[');
        for(char c : inner.toCharArray())
            special.add((int)c);
        if(macroState == START)
            special.add((int)']');
    }

    private int read() throws IOException {
        int point;
        if(special.isEmpty())
            point = source.read();
        else {
            point = special.poll();
        }
        return point;
    }

    @Override
    protected Token innerToken() {
        try {
            int point = read();
            if(delims.contains(point))
                return new Token(TokenType.EOF);
            switch(point){
                case '|':
                    if(macroState == START)
                        return new Token(store.get(VBAR));
                    break;
                case '^':
                    if(macroState == BRACK)
                        return new Token(store.get(CARAT));
                    break;
                case '-':
                    if(macroState == BRACK)
                        return new Token(store.get(MINUS));
                    break;
                case '*':
                case '+':
                case '?':
                case '/':
                    if(macroState == START)
                        return new Token(store.get(MODIFIER), "" + (char)point);
                    break;
                case '(':
                    if(macroState == START)
                        return new Token(store.get(LPAREN));
                    break;
                case ')':
                    if(macroState == START)
                        return new Token(store.get(RPAREN));
                    break;
                case '[':
                    if(macroState == START){
                        macroState = BRACK;
                        return new Token(store.get(LBRACK));
                    }
                    break;
                case ']':
                    if(macroState == BRACK){
                        macroState = START;
                        return new Token(store.get(RBRACK));
                    }
                    break;
                case '.':
                    if(macroState == START)
                        return new Token(store.get(DOT));
                    break;
                case '\\':
                    int second = read();
                    int rto = 8;
                    switch (second){
                        case 'n':
                            return new Token(store.get(CHAR), "\n");
                        case 't':
                            return new Token(store.get(CHAR), "\t");
                        case 'r':
                            return new Token(store.get(CHAR), "\r");
                        case 'u':
                            rto = 4;
                        case 'U':
                            int escaped = 0;
                            for(int i = 0; i < rto; i++){
                                boolean doPush = special.isEmpty();
                                int digit = read();
                                if(digit >= '0' && digit <= '9')
                                    escaped = escaped * 16 + digit - '0';
                                else if(digit >= 'a' && digit <= 'f')
                                    escaped = escaped * 16 + digit - 'a' + 10;
                                else if(digit >= 'A' && digit <= 'F')
                                    escaped = escaped * 16 + digit - 'A' + 10;
                                else{
                                    if(doPush)
                                        source.pushBack();
                                    i = rto;
                                }
                            }
                            return new Token(store.get(CHAR), CodePointHelper.asSingleString(escaped));
                        case 's':
                            queueClass(" \\t\\n\\r");
                            return getToken();
                        case 'S':
                            queueClass("^ \\t\\n\\r");
                            return getToken();
                        case 'd':
                            queueClass("0-9");
                            return getToken();
                        case 'D':
                            queueClass("^0-9");
                            return getToken();
                        case 'w':
                            queueClass("A-Za-z0-9_");
                            return getToken();
                        case 'W':
                            queueClass("^A-Za-z0-9_");
                            return getToken();
                        default:
                            return new Token(store.get(CHAR), CodePointHelper.asSingleString(second));
                    }
                case '\r':
                    boolean doPush = special.isEmpty();
                    second = read();
                    if(second != '\n' && doPush)
                        source.pushBack();
                    return new Token(store.get(0));
                case -1:
                case '\n':
                    return new Token(store.get(0));
            }
            return new Token(store.get(CHAR), CodePointHelper.asSingleString(point));
        }catch(Exception e){
            e.printStackTrace();
            return new Token(store.get(0));
        }
    }
}
