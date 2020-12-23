package com.funguscow.gible.lexer.regex.tree;

import com.funguscow.gible.lexer.provider.RegexTokenProvider;
import com.funguscow.gible.lexer.regex.CRange;
import com.funguscow.gible.lexer.regex.RegexParseTermStore;
import com.funguscow.gible.parser.SyntaxTree;
import com.funguscow.gible.util.CodePointHelper;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RegexLeaf extends RegexTree {

    private TreeSet<CRange> ranges;
    private int accepts;
    private boolean marks;

    public RegexLeaf(SyntaxTree tree){
        super();
        accepts = -1;
        ranges = new TreeSet<>();
        boolean negative = false;
        if(tree.getTerm().getId() == RegexParseTermStore.char_class) {
            negative = tree.getChild(0).getTerm().getId() == RegexTokenProvider.CARAT;
            int start = negative ? 1 : 0;
            for (int i = start; i < tree.numChildren(); i++) {
                SyntaxTree range = tree.getChild(i);
                int begin = CodePointHelper.charAtAsCodepoint(range.getChild(0).getLexeme(), 0);
                int end;
                if (range.numChildren() == 1) {
                    end = begin;
                }
                else{
                    end = CodePointHelper.charAtAsCodepoint(range.getChild(1).getLexeme(), 0);
                }
                ranges.add(new CRange(begin, end + 1));
            }
        }
        else if(tree.getTerm().getId() == RegexTokenProvider.CHAR){
            int c = CodePointHelper.charAtAsCodepoint(tree.getLexeme(), 0);
            ranges.add(new CRange(c, c + 1));
        }
        else if(tree.getTerm().getId() == RegexTokenProvider.DOT){
            ranges.add(new CRange(0, CodePointHelper.MAX_CODE_POINT));
        }
        else
            throw new IllegalArgumentException("Argument to RegexLeaf must be of type CHAR, DOT, or char_class");
        ranges = CRange.union(ranges);
        if(negative)
            ranges = CRange.complement(ranges);
    }

    public RegexLeaf(int i){
        super();
        accepts = i;
        ranges = new TreeSet<>();
    }

    public boolean doesMark() {
        return marks;
    }

    public void setMarks(boolean marks) {
        this.marks = marks;
        for(CRange range : ranges)
            range.mark(marks);
    }

    public int indexDfs(int i, Map<Integer, RegexLeaf> positions){
        int ret = super.indexDfs(i, positions);
        ranges.forEach(r -> r.addValue(id));
        return ret;
    }

    public Set<CRange> getRanges(){
        return ranges;
    }

    public int getAccepts() {
        return accepts;
    }

    @Override
    protected boolean isNullable() {
        return false;
    }

    @Override
    public Set<Integer> getFirst() {
        return Set.of(id);
    }

    @Override
    protected Set<Integer> getLast() {
        return Set.of(id);
    }

    @Override
    protected void markFinal(Set<Integer> lastSet) {
        if(lastSet.contains(id)){
            for(CRange range : ranges)
                range.mark(true);
        }
    }
}
