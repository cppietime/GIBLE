package com.funguscow.gible.lexer;

import com.funguscow.gible.IndexNameStore;
import com.funguscow.gible.codegen.ClassSource;
import com.funguscow.gible.lexer.provider.TokenProvider;
import com.funguscow.gible.lexer.regex.CRange;
import com.funguscow.gible.lexer.regex.Dfa;
import com.funguscow.gible.util.CodePointStream;

import java.util.*;
import java.util.stream.Collectors;

public class Lexer {

    public static class LexicalError extends RuntimeException{
        public LexicalError(String msg){
            super(msg);
        }
    }

    private int state;
    private Token nextToken;
    private final List<LexerState> states;
    private final TokenTypeStore tokenStore;
    private final StringBuilder lexemeBuilder;
    private Set<Integer> ignore;

    public Lexer(TokenTypeStore tokenStore){
        this.tokenStore = tokenStore;
        state = 0;
        states = new ArrayList<>();
        nextToken = null;
        lexemeBuilder = new StringBuilder();
        ignore = new HashSet<>();
    }

    public Lexer ignore(int... igs){
        for(int i : igs)ignore.add(i);
        return this;
    }

    public Lexer addState(LexerState state){
        states.add(state);
        return this;
    }

    public Token lex(CodePointStream input){
        int line = input.getLineno();
        int col = input.getColno();
        state = 0;
        nextToken = null;
        while(nextToken == null || ignore.contains(nextToken.getType().getId())) {
            Dfa.DfaMatch match = states.get(state).process(input, lexemeBuilder, this);
            if (match.acceptance == -1) {
                if (input.eof()) {
                    return new Token(TokenType.EOF);
                }
                throw new LexicalError(String.format("Unidentified token %s at %d:%d",
                        lexemeBuilder.toString(),
                        line,
                        col));
            }
        }
        return nextToken;
    }

    public void setState(int state){
        this.state = state;
    }

    public void returnToken(int id){
        if(tokenStore.get(id).doesHaveLexeme())
            nextToken = new Token(tokenStore.get(id), lexemeBuilder.toString());
        else
            nextToken = new Token(tokenStore.get(id));
        lexemeBuilder.setLength(0);
    }

    /**
     * Generates source code for a hardcoded lexer
     * Simply call this with the desired class name and save it as a String to a .java file
     * Add a package declaration if needed
     * @param className Name of class produced
     * @return Source code of lexer class
     */
    public ClassSource generateSourceCode(String className){
        ClassSource source = new ClassSource("public class " +  className)
                .annotate("Generated hardcoded lexer")
                .extend(TokenProvider.class.getSimpleName())
                .withMember("private StringBuilder lexeme")
                .withMember("private int state")
                .withMember("private int nextTokenId")
                .withMember("private CodePointStream input")
                .withMethod(generateTermMaker())
                .withMethod(generateConstructor(className))
                .withMethod(generateLexFunc());
        for (int i = 0, statesSize = states.size(); i < statesSize; i++) {
            LexerState state = states.get(i);
            source.withMethod(state.generateFunctionSource(i))
                    .withMethod(state.generateDfaTransFunc(i))
                    .withMethod(state.generateDfaAcceptFunc(i));
        }
        return source
                .imports(Token.class.getCanonicalName())
                .imports(TokenType.class.getCanonicalName())
                .imports(TokenTypeStore.class.getCanonicalName())
                .imports(TokenProvider.class.getCanonicalName())
                .imports(Dfa.class.getCanonicalName())
                .imports(Lexer.class.getCanonicalName())
                .imports(CRange.class.getCanonicalName())
                .imports(CodePointStream.class.getCanonicalName())
                .imports(StringBuilder.class.getCanonicalName())
                .imports(Map.class.getCanonicalName())
                .imports(HashMap.class.getCanonicalName())
                .imports(List.class.getCanonicalName())
                .imports(ArrayList.class.getCanonicalName());
    }

    private ClassSource.MethodSource generateLexFunc(){
        ClassSource.MethodSource method = new ClassSource.MethodSource("public Token getToken");
        StringBuilder source = new StringBuilder();
        source.append(
                "int line = input.getLineno();\n" +
                "int col = input.getColno();\n" +
                "state = 0;\n" +
                "nextTokenId = -1;\n" +
                "lexeme.setLength(0);\n" +
                "Dfa.DfaMatch match;\n" +
                "while(nextTokenId == -1){\n" +
                "   switch(state){\n" +
                "       default -> {\n" +
                "           match = new Dfa.DfaMatch(-1, 0);\n" +
                "       }\n");
        for(int i = 0; i < states.size(); i++){
            String processFunc = String.format("process_%d", i);
            source.append(
                "       case " + i + " -> {\n" +
                "           match = " + processFunc + "(input);\n" +
                "       }\n");
        }
        source.append(
                "   }\n" +
                "   if(match.acceptance == -1){\n" +
                "       if(input.eof())\n" +
                "           return new Token(TokenType.EOF);\n" +
                "       throw new Lexer.LexicalError(String.format(\"Unidentified token \\\"%s\\\" at %d:%d\", " +
                        "lexeme.toString(), line, col));\n" +
                "   }\n");
        if(!ignore.isEmpty()) {
            source.append(
                "   switch(nextTokenId){\n" +
                "       case " + ignore.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + " -> {\n" +
                "           nextTokenId = -1;\n" +
                "           lexeme.setLength(0);\n" +
                "       }\n" +
                "   }\n");
        }
        source.append("}\n" +
                "TokenType tokenType = store.get(nextTokenId);\n" +
                "if(tokenType.doesHaveLexeme())\n" +
                "   return new Token(tokenType, lexeme.toString());\n" +
                "return new Token(tokenType);\n");
        return method.withSource(source.toString());
    }

    private ClassSource.MethodSource generateConstructor(String name){
        ClassSource.MethodSource init = new ClassSource.MethodSource(String.format("public %s", name))
                .withParam("CodePointStream", "input");
        String source =
                "super(generateStore());\n" +
                "this.input = input;\n" +
                "lexeme = new StringBuilder();\n";
        return init.withSource(source);
    }

    private ClassSource.MethodSource generateTermMaker(){
        ClassSource.MethodSource method = new ClassSource.MethodSource("private static TokenTypeStore generateStore");
        StringBuilder source = new StringBuilder(
                "TokenTypeStore tokenTypeStore = new TokenTypeStore();\n");
        for(IndexNameStore.IndexNameStoreEntry<TokenType> entry : tokenStore){
            TokenType type = entry.entry;
            if(type.equals(TokenType.EOF) || type.equals(TokenType.EPSILON) || type.equals(TokenType.PROPAGATION))
                continue;
            source.append(
                    "tokenTypeStore.registerToken(new TokenType(" + type.getId() + ", \"" + type.getName() + "\")"
                            + (type.doesHaveLexeme() ? ".withLexeme()" : "") + ");\n");
        }
        source.append("return tokenTypeStore;\n");
        return method.withSource(source.toString());
    }

}
