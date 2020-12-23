package com.funguscow.gible.lexer.regex.tree;

import com.funguscow.gible.lexer.regex.RegexParseTermStore;
import com.funguscow.gible.parser.SyntaxTree;

import java.util.*;

public class RegexConcat extends RegexTree {

    public RegexConcat(SyntaxTree tree){
        super();
        if(tree.getTerm().getId() != RegexParseTermStore.or_piece)
            throw new IllegalArgumentException("Argument to RegexConcat must be of type or_piece");
        for(int i = 0; i < tree.numChildren(); i++){
            SyntaxTree concat = tree.getChild(i);
            children.add(new RegexModified(concat));
        }
    }

    public RegexConcat(RegexTree... trees){
        super();
        children.addAll(Arrays.asList(trees));
    }

    @Override
    protected boolean isNullable() {
        return children.stream().allMatch(RegexTree::isNullable);
    }

    @Override
    public Set<Integer> getFirst() {
        TreeSet<Integer> first = new TreeSet<>();
        for(RegexTree child : children){
            first.addAll(child.getFirst());
            if(!child.isNullable())
                break;
        }
        return first;
    }

    @Override
    protected Set<Integer> getLast() {
        TreeSet<Integer> last = new TreeSet<>();
        for(int i = children.size() - 1; i >= 0; i--){
            RegexTree child = children.get(i);
            last.addAll(child.getLast());
            if(!child.isNullable())
                break;
        }
        return last;
    }

    @Override
    public void calcFollow(Map<Integer, RegexLeaf> positions) {
        super.calcFollow(positions);
        for(int i = children.size() - 1; i > 0; i--){
            RegexTree donor = children.get(i);
            for(int j = i - 1; j >= 0; j--){
                RegexTree receiver = children.get(j);
                receiver.getLast().forEach(pos -> positions.get(pos).follow.addAll(donor.getFirst()));
                if(!receiver.isNullable())
                    j = -1;
            }
        }
    }
}
