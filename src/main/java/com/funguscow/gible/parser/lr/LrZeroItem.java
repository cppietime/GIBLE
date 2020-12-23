package com.funguscow.gible.parser.lr;

import com.funguscow.gible.parser.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An LR(0) parse item
 */
public class LrZeroItem {

    private final ProductionRule rule;
    private final int position;

    /**
     * Construct an LR(0) item from a rule and position
     * @param rule Production rule of item
     * @param position Position in rule of item
     */
    public LrZeroItem(ProductionRule rule, int position){
        this.rule = rule;
        this.position = position;
    }

    /**
     *
     * @return True if this is at the final position
     */
    public boolean atEnd(){
        return position == rule.getTerms().size();
    }

    /**
     * Progress ahead by i
     * @param i Amount to move forward
     * @return New progressed item
     */
    public LrZeroItem progress(int i){
        if(position + i > rule.getTerms().size())
            throw new IndexOutOfBoundsException(position + i + " is greater than range" + rule.getTerms().size());
        return new LrZeroItem(rule, Math.min(position + i, rule.getTerms().size() + 1));
    }

    /**
     * Construct a new LR(1) item from this with a lookahead symbol
     * @param lookahead Lookahead symbol
     * @return New LR(1) item
     */
    public LrOneItem lookahead(ParseTerm lookahead){
        return new LrOneItem(this, lookahead);
    }

    /**
     *
     * @param store  Parse terms
     * @return true if this is a kernel item in store
     */
    public boolean isKernel(ParseTermStore store){
        return position != 0 || rule.getResult().equals(store.getStartTerm());
    }

    /**
     *
     * @return The next term in this item's rule, or null if at the end
     */
    public ParseTerm getNextTerm(){
        if(position == rule.getTerms().size())
            return null;
        return rule.getTerms().get(position);
    }

    /**
     *
     * @return This item's production rule
     */
    public ProductionRule getRule(){
        return rule;
    }

    /**
     *
     * @return This item's position
     */
    public int getPosition(){
        return position;
    }

    /**
     * Add the closure of this item to a set of items
     * @param closure Set to which to add closure set
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return closure, for convenience
     */
    public int addClosureTo(Set<LrZeroItem> closure, ProductionRuleStore ruleStore, ParseTermStore termStore){
        int initialSize = closure.size();
        closure.add(this);
        ParseTerm nextTerm = getNextTerm();
        if(nextTerm != null){
            if(!nextTerm.isTerminal()){
                NonTerminalParseTerm nonTerminal = (NonTerminalParseTerm)nextTerm;
                for(ProductionRule rule : ruleStore.getRulesFor(nonTerminal)){
                    closure.add(rule.itemAt(0));
                }
            }
        }
        return closure.size() - initialSize;
    }

    @Override
    public int hashCode() {
        return position * 31 + rule.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof LrZeroItem))
            return false;
        LrZeroItem other = (LrZeroItem)obj;
        return other.rule.equals(rule) && position == other.position;
    }

    /**
     * Set start to its closure
     * @param start Starting set, modified in-place and returned
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return start, for convenience
     */
    public static Set<LrZeroItem> closure(Set<LrZeroItem> start, ProductionRuleStore ruleStore, ParseTermStore termStore){
        boolean didChange;
        Set<LrZeroItem> temp = new HashSet<>();
        do {
            didChange = false;
            for (LrZeroItem item : start)
                item.addClosureTo(temp, ruleStore, termStore);
            if(start.addAll(temp))
                didChange = true;
            temp.clear();
        }while(didChange);
        return start;
    }

    /**
     * Calculate the goto set of a set of items on a symbol
     * @param start Starting set of items
     * @param next Transition symbol
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return Set of GOTO(start, next)
     */
    public static Set<LrZeroItem> gotos(Set<LrZeroItem> start,
                                       ParseTerm next,
                                       ProductionRuleStore ruleStore,
                                       ParseTermStore termStore){
        Set<LrZeroItem> gotos = new HashSet<>();
        for(LrZeroItem item : start){
            ParseTerm match = item.getNextTerm();
            if(next.equals(match)){
                gotos.add(item.progress(1));
            }
        }
        return closure(gotos, ruleStore, termStore);
    }

    /**
     * Prune a set of item sets to only kernels
     * @param sets Sets to prune
     * @param termStore Parse terms
     * @return Set of kernels of sets
     */
    public static Set<Set<LrZeroItem>> kernelsOnly(Set<Set<LrZeroItem>> sets, ParseTermStore termStore){
        return sets.stream().map(
                set -> set.stream().filter(i -> i.isKernel(termStore)).collect(Collectors.toSet())
        ).filter(set -> !set.isEmpty()).collect(Collectors.toSet());
    }

    @Override
    public String toString(){
        return rule.toString() + "@" + position;
    }
}
