package com.funguscow.gible.parser;

import com.funguscow.gible.lexer.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A parse tree
 */
public class SyntaxTree {

    protected final boolean terminal;
    protected final String lexeme;
    protected final ParseTerm term;
    protected final List<SyntaxTree> children;
    protected int lineNo;
    protected int colNo;
    protected int production;

    /**
     * Terminal parse tree of a token
     * @param token Containing token
     * @param store Term store
     */
    public SyntaxTree(Token token, ParseTermStore store){
        terminal = true;
        term = store.get(token.getType().getId());
        if(token.getType().doesHaveLexeme())
            lexeme = token.getLexeme();
        else
            lexeme = null;
        children = null;
        lineNo = token.getLineNo();
        colNo = token.getColNo();
        production = -1;
    }

    /**
     * Syntax tree with children
     * @param term Tree term
     * @param children Tree children
     */
    public SyntaxTree(ParseTerm term, int production, List<SyntaxTree> children){
        this.term = term;
        this.terminal = false;
        this.lexeme = null;
        this.children = new ArrayList<>(children);
        lineNo = -1;
        if(!children.isEmpty())
            lineNo = children.get(0).lineNo;
        this.production = production;
    }

    /**
     * Vararg non-terminal syntax tree construction
     * @param term This term
     * @param children Children
     */
    public SyntaxTree(ParseTerm term, int production, SyntaxTree... children){
        this(term, production, Arrays.asList(children));
    }

    /**
     *
     * @return This term type
     */
    public ParseTerm getTerm(){
        return term;
    }

    /**
     *
     * @return Number of children. Terminal trees have itself as its only child
     */
    public int numChildren(){
        if(terminal)
            return 1;
        return children.size();
    }

    /**
     * Get the child at position i. Terminal trees have itself as only child
     * @param i Position of child
     * @return Child at matching position
     */
    public SyntaxTree getChild(int i){
        if(terminal)
            return this;
        return children.get(i);
    }

    /**
     *
     * @return The line number of this tree
     */
    public int getLineNo(){
        return lineNo;
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
     * @return This lexeme
     */
    public String getLexeme(){
        if(lexeme == null)
            return "";
        return lexeme;
    }

    /**
     *
     * @return The production that produced this, or -1 for a terminal
     */
    public int getProduction(){
        return production;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(term.getName()).append("{").append(production).append("}");
        if(terminal && lexeme != null){
            builder.append(String.format("(%s)", lexeme));
        }
        else if(!terminal){
            builder.append("[ ");
            for(SyntaxTree child : children)
                builder.append(child).append(" ");
            builder.append("]");
        }
//        if(lineNo != -1)
//            builder.append(String.format("{%s}", lineNo));
        return builder.toString();
    }
}
