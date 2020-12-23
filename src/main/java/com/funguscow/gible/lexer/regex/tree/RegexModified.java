package com.funguscow.gible.lexer.regex.tree;

import com.funguscow.gible.lexer.regex.RegexParseTermStore;
import com.funguscow.gible.parser.SyntaxTree;

import java.util.Map;
import java.util.Set;

public class RegexModified extends RegexTree {

    public enum Modifier{
        ONE,
        MANY,
        ANY,
        OPTIONAL,
        LOOKAHEAD
    }

    private final Modifier modifier;

    public RegexModified(SyntaxTree tree){
        super();
        if(tree.getTerm().getId() != RegexParseTermStore.cat_piece)
            throw new IllegalArgumentException("Argument to RegexModified must be of type cat_piece");
        children.add(RegexTree.of(tree.getChild(0)));
        if(tree.numChildren() > 1){
            SyntaxTree modTree = tree.getChild(1);
            switch(modTree.getLexeme()){
                case "*" -> modifier = Modifier.ANY;
                case "+" -> modifier = Modifier.MANY;
                case "?" -> modifier = Modifier.OPTIONAL;
                case "/" -> modifier = Modifier.LOOKAHEAD;
                default -> modifier = Modifier.ONE;
            }
        }
        else
            modifier = Modifier.ONE;
    }

    public Modifier getModifier(){
        return modifier;
    }

    @Override
    protected boolean isNullable() {
        return modifier == Modifier.ANY || modifier == Modifier.OPTIONAL || children.get(0).isNullable();
    }

    @Override
    public Set<Integer> getFirst() {
        return children.get(0).getFirst();
    }

    @Override
    protected Set<Integer> getLast() {
        return children.get(0).getLast();
    }

    @Override
    public void calcFollow(Map<Integer, RegexLeaf> positions) {
        super.calcFollow(positions);
        RegexTree base = children.get(0);
        if(modifier == Modifier.ANY || modifier == Modifier.MANY) {
            base.getLast().forEach(i -> positions.get(i).follow.addAll(base.getFirst()));
        }
    }

    @Override
    public int indexDfs(int i, Map<Integer, RegexLeaf> positions) {
        int ret = super.indexDfs(i, positions);
        if(modifier == Modifier.LOOKAHEAD) {
            for (int l : getLast()) {
                positions.get(l).setMarks(true);
            }
        }
        return ret;
    }

    @Override
    protected boolean hasMarker() {
        return modifier == Modifier.LOOKAHEAD;
    }
}
