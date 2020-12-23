package com.funguscow.gible.lexer.regex.tree;

import com.funguscow.gible.lexer.regex.RegexParseTermStore;
import com.funguscow.gible.parser.SyntaxTree;

import java.util.*;

/**
 * A syntax tree for a parsed Regex
 */
public abstract class RegexTree implements Cloneable {

    protected List<RegexTree> children;
    protected TreeSet<Integer> follow;
    protected int id;

    protected RegexTree(){
        children = new ArrayList<>();
        follow = new TreeSet<>();
    }

    /**
     * Assign indices to each node
     * @param i Index for this node
     * @param positions Map of positions
     * @return index to pick up on
     */
    public int indexDfs(int i, Map<Integer, RegexLeaf> positions){
        id = i++;
        for(RegexTree child : children){
            i = child.indexDfs(i, positions);
        }
        if(this instanceof RegexLeaf)
            positions.put(id, (RegexLeaf)this);
        return i;
    }

    public int getId(){
        return id;
    }

    protected static RegexTree of(SyntaxTree tree){
        int id = tree.getTerm().getId();
        if(id == RegexParseTermStore.regex)
            return new RegexEither(tree);
        else if(id == RegexParseTermStore.or_piece)
            return new RegexConcat(tree);
        else if(id == RegexParseTermStore.cat_piece)
            return new RegexModified(tree);
        else if(id == RegexParseTermStore.piece){
            SyntaxTree pChild = tree.getChild(0);
            if(pChild.getTerm().getId() == RegexParseTermStore.regex)
                return new RegexEither(pChild);
            return new RegexLeaf(pChild);
        }
        throw new IllegalArgumentException("Invalid type " + tree.getTerm() + " from which to derive regex");
    }

    /**
     * Construct a regex from a Regex-syntax-tree
     * @param trees Parsed regex syntax trees
     * @return New Regex Tree
     */
    public static RegexTree regexOf(SyntaxTree... trees){
        List<RegexTree> eithers = new ArrayList<>();
        for (int i = 0; i < trees.length; i++) {
            SyntaxTree tree = trees[i];
            RegexTree regex = of(tree);
            regex.indexDfs(0, new HashMap<>());
            regex.ensureLookahead();
            RegexTree accept = new RegexLeaf(i);
            RegexTree concat = new RegexConcat(regex, accept);
            eithers.add(concat);
        }
        return new RegexEither(eithers);
    }

    protected abstract boolean isNullable();

    public abstract Set<Integer> getFirst();

    protected abstract Set<Integer> getLast();

    public void calcFollow(Map<Integer, RegexLeaf> positions){
        for(RegexTree child : children)
            child.calcFollow(positions);
    }

    protected boolean hasMarker(){
        boolean marker = false;
        for(RegexTree child : children){
            if(child.hasMarker())
                marker = true;
        }
        return marker;
    }

    protected void markFinal(Set<Integer> lastSet){
        for(RegexTree child : children)
            child.markFinal(lastSet);
    }

    /**
     * Ensure this regex tree has a lookahead marker at the end if it has none
     */
    public void ensureLookahead(){
        if(hasMarker())
            return;
        markFinal(getLast());
    }

    public Set<Integer> getFollow(){
        return follow;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
        builder.append("[");
        for(RegexTree child : children){
            builder.append(child.toString()).append(",");
        }
        builder.append("]");
        return builder.toString();
    }

}
