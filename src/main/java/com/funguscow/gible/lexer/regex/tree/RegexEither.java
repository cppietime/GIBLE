package com.funguscow.gible.lexer.regex.tree;

import com.funguscow.gible.lexer.regex.RegexParseTermStore;
import com.funguscow.gible.parser.SyntaxTree;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class RegexEither extends RegexTree {

    public RegexEither(SyntaxTree tree){
        super();
        if(tree.getTerm().getId() != RegexParseTermStore.regex)
            throw new IllegalArgumentException("Argument to RegexEither must be of type regex");
        for(int i = 0; i < tree.numChildren(); i++){
            children.add(new RegexConcat(tree.getChild(i)));
        }
    }

    public RegexEither(List<RegexTree> regexes){
        super();
        children.addAll(regexes);
    }

    @Override
    protected boolean isNullable() {
        return children.stream().anyMatch(RegexTree::isNullable);
    }

    @Override
    public Set<Integer> getFirst() {
        TreeSet<Integer> first = new TreeSet<>();
        for(RegexTree child : children)
            first.addAll(child.getFirst());
        return first;
    }

    @Override
    protected Set<Integer> getLast() {
        TreeSet<Integer> last = new TreeSet<>();
        for(RegexTree child : children)
            last.addAll(child.getLast());
        return last;
    }
}
