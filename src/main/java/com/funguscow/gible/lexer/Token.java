package com.funguscow.gible.lexer;

/**
 * An instance of a lexed token
 */
public class Token {

    private final TokenType type;
    private String lexeme;
    private int lineNo, colNo;

    /**
     *
     * @param type Token type
     * @param lexeme Lexeme if any
     */
    public Token(TokenType type, String lexeme){
        this.type = type;
        this.lexeme = lexeme;
    }

    /**
     *
     * @param type Lexeme-less token type
     */
    public Token(TokenType type){
        this(type, null);
    }

    /**
     *
     * @param lineNo Line number
     * @return this
     */
    public Token setLineNo(int lineNo){
        this.lineNo = lineNo;
        return this;
    }

    /**
     *
     * @return The line on which this token appears
     */
    public int getLineNo(){
        return lineNo;
    }

    /**
     *
     * @param colNo Column number
     * @return this
     */
    public Token setColNo(int colNo){
        this.colNo = colNo;
        return this;
    }

    /**
     *
     * @return Column number
     */
    public int getColNo(){
        return colNo;
    }

    /**
     *
     * @return Type of this
     */
    public TokenType getType(){
        return type;
    }

    /**
     *
     * @return This lexeme
     */
    public String getLexeme(){
        return lexeme;
    }

    /**
     *
     * @param lexeme Assign this lexeme
     * @return this
     */
    public Token setLexeme(String lexeme){
        this.lexeme = lexeme;
        return this;
    }

    @Override
    public String toString() {
        if(type.doesHaveLexeme())
            return String.format("%s[%s]", type.getName(), lexeme);
        return type.getName();
    }
}
