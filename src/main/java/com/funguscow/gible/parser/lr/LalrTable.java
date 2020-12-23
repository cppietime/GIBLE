package com.funguscow.gible.parser.lr;

import com.funguscow.gible.parser.ParseTerm;
import com.funguscow.gible.parser.ParseTermStore;
import com.funguscow.gible.parser.ProductionRuleStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Calculates and stores lookaheads for LALR(1)
 */
public class LalrTable {

    private final Map<LrZeroItem, Set<ParseTerm>> lookaheads;
    private final Map<LrZeroItem, Set<LrZeroItem>> propagate;
    private int state;

    public LalrTable(){
        state = 0;
        lookaheads = new HashMap<>();
        propagate = new HashMap<>();
    }

    /**
     * Calculate the spontaneous and propagated lookaheads for a set of LR(0) kernels
     * @param kernels Set of kernels of LR(0) item sets
     * @param ruleStore Production rules
     * @param termStore Parse terms
     */
    public void getLookaheads(Set<Set<LrZeroItem>> kernels,
                              ProductionRuleStore ruleStore,
                              ParseTermStore termStore){
        ParseTerm prop = termStore.getPropTerm();
        lookaheads.computeIfAbsent(ruleStore.getStartRule().itemAt(0), key -> new HashSet<>()).add(termStore.getEndTerm());
        for(Set<LrZeroItem> kernel : kernels) {
            for (LrZeroItem item : kernel) {
                Set<LrZeroItem> propTo = propagate.computeIfAbsent(item, key -> new HashSet<>());
                Set<ParseTerm> starting = this.lookaheads.computeIfAbsent(item, key -> new HashSet<>());
                Set<LrOneItem> closure = new HashSet<>();
                closure.add(item.lookahead(prop));
                LrOneItem.closure(closure, ruleStore, termStore);
                for (LrOneItem closed : closure) {
                    if (closed.getLookahead().equals(prop)) {
                        if(!closed.getItem().atEnd())
                            propTo.add(closed.getItem().progress(1));
                    } else {
                        starting.add(closed.getLookahead());
                    }
                }
            }
        }
        state = 1;
    }

    /**
     * Propagate lookaheads until no further changes can be made
     */
    public void propagate(){
        if(state == 0)
            throw new IllegalStateException("Must call getLookaheads before calling propagate");
        boolean didChange;
        do{
            didChange = false;
            for(Map.Entry<LrZeroItem, Set<LrZeroItem>> entry : propagate.entrySet()){
                Set<ParseTerm> source = lookaheads.get(entry.getKey());
                Set<LrZeroItem> targets = entry.getValue();
                for(LrZeroItem target : targets){
                    if(lookaheads.computeIfAbsent(target, key -> new HashSet<>()).addAll(source))
                        didChange = true;
                }
            }
        }while(didChange);
        state = 2;
    }

    /**
     * Get the LR(1) kernel of an LR(0) item
     * @param kernel LR(0) kernel to match
     * @return The matching LR(1) kernel
     */
    public Set<LrOneItem> kernelOf(Set<LrZeroItem> kernel){
        if(state != 2)
            throw new IllegalStateException("Must call getLookaheads and propagate before calling kernelOf");
        Set<LrOneItem> ret = new HashSet<>();
        for(LrZeroItem item : kernel){
            if(lookaheads.containsKey(item)) {
                Set<ParseTerm> las = lookaheads.get(item);
                for(ParseTerm term : las){
                    ret.add(item.lookahead(term));
                }
            }
        }
        return ret;
    }

}
