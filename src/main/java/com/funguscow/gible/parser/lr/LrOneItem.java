package com.funguscow.gible.parser.lr;

import com.funguscow.gible.parser.*;

import java.util.HashSet;
import java.util.Set;

/**
 * An LR(1) parse item
 */
public class LrOneItem {

    private final LrZeroItem item;
    private final ParseTerm lookahead;

    /**
     * Construct an LR(1) item from an LR(0) item
     * @param item Corresponding LR(0) item
     * @param lookahead Lookahead symbol
     */
    public LrOneItem(LrZeroItem item, ParseTerm lookahead){
        this.item = item;
        this.lookahead = lookahead;
    }

    /**
     *
     * @return Matching LR(0) item
     */
    public LrZeroItem getItem(){
        return item;
    }

    /**
     *
     * @return Lookahead symbol
     */
    public ParseTerm getLookahead(){
        return lookahead;
    }

    /**
     * Move ahead by i symbols
     * @param i Amount to progress
     * @return New progressed item
     */
    public LrOneItem progress(int i){
        return new LrOneItem(item.progress(i), lookahead);
    }

    /**
     * Test if this is a kernel item
     * @param store Parse terms
     * @return true if this is a kernel item in store
     */
    public boolean isKernel(ParseTermStore store){
        return item.isKernel(store);
    }

    /**
     * Add the closure of this item to a set of items
     * @param closure Set to which to add
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return closure, for convenience
     */
    public int addClosureTo(Set<LrOneItem> closure, ProductionRuleStore ruleStore, ParseTermStore termStore){
        int initialSize = closure.size();
        closure.add(this);
        ParseTerm nextTerm = item.getNextTerm();
        if(nextTerm != null){
            if(!nextTerm.isTerminal()){
                NonTerminalParseTerm nonTerminal = (NonTerminalParseTerm)nextTerm;
                int startPos = item.getPosition() + 1;
                Set<ParseTerm> first = new HashSet<>();
                boolean nullable = true;
                for(int i = startPos; i < item.getRule().getTerms().size() && nullable; i++){
                    ParseTerm term = item.getRule().getTerms().get(i);
                    first.addAll(term.getFirst());
                    if(!ruleStore.isNullable(term, termStore))
                        nullable = false;
                }
                if(nullable)
                    first.addAll(lookahead.getFirst());
                for(ProductionRule rule : ruleStore.getRulesFor(nonTerminal)){
                    for(ParseTerm term : first){
                        closure.add(rule.itemAt(0).lookahead(term));
                    }
                }
            }
        }
        return closure.size() - initialSize;
    }

    @Override
    public int hashCode() {
        return item.hashCode() * 41 + lookahead.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof LrOneItem))
            return false;
        LrOneItem other = (LrOneItem)obj;
        return item.equals(other.item) && lookahead.equals(other.lookahead);
    }

    /**
     * Adds closure of a set of items to that set and returns it for convenience
     * @param start Set of LR(1) items to close, is modified by method
     * @param ruleStore Store of production rules
     * @param termStore Store of terms
     * @return start
     */
    public static Set<LrOneItem> closure(Set<LrOneItem> start, ProductionRuleStore ruleStore, ParseTermStore termStore){
        boolean didChange;
        Set<LrOneItem> temp = new HashSet<>();
        do {
            didChange = false;
            for (LrOneItem item : start)
                item.addClosureTo(temp, ruleStore, termStore);
            if(start.addAll(temp))
                didChange = true;
            temp.clear();
        }while(didChange);
        return start;
    }

    /**
     * Calculate goto from an item set on a symbol
     * @param start Starting item set
     * @param next Transition term
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return Set of GOTO(start, next)
     */
    public static Set<LrOneItem> gotos(Set<LrOneItem> start,
                                       ParseTerm next,
                                       ProductionRuleStore ruleStore,
                                       ParseTermStore termStore){
        Set<LrOneItem> gotos = new HashSet<>();
        for(LrOneItem item : start){
            ParseTerm match = item.item.getNextTerm();
            if(next.equals(match)){
                gotos.add(item.progress(1));
            }
        }
        return closure(gotos, ruleStore, termStore);
    }

    @Override
    public String toString(){
        return item.toString() + "," + lookahead;
    }
}
