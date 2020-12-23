package com.funguscow.gible.parser.lr;

import com.funguscow.gible.IndexNameStore;
import com.funguscow.gible.parser.ParseTerm;
import com.funguscow.gible.parser.ParseTermStore;
import com.funguscow.gible.parser.ProductionRuleStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Store of LR(1) item sets representing parse actions
 */
public class LrOneSetStore extends IndexNameStore<Set<LrOneItem>> {

    private final Map<LrOneItem, Set<Integer>> containers = new HashMap<>();
    private final Map<Set<LrOneItem>, Integer> containerStore = new HashMap<>();

    /**
     * Constructs a set of LR(1) items from a set of kernels of LR(0) items
     * @param kernels LR(0) items kernels set
     * @param ruleStore Store of production rules
     * @param termStore Store of parse terms
     */
    public LrOneSetStore(Set<Set<LrZeroItem>> kernels, ProductionRuleStore ruleStore, ParseTermStore termStore){
        super();
        LalrTable table = new LalrTable();
        table.getLookaheads(kernels, ruleStore, termStore);
        table.propagate();
        for(Set<LrZeroItem> kernel : kernels){
            Set<LrOneItem> transformed = table.kernelOf(kernel);
            if(transformed.isEmpty())
                continue;
            LrOneItem.closure(transformed, ruleStore, termStore);
            addWithName(transformed, String.format("State_%d", size()));
        }
    }

    /**
     * Construct a store from an already made set of LR(1) item sets
     * @param itemSets Sets of LR(1) items
     */
    public LrOneSetStore(Set<Set<LrOneItem>> itemSets){
        super();
        for(Set<LrOneItem> itemSet : itemSets){
            addWithName(itemSet, String.format("State_%d", size()));
        }
    }

    @Override
    public int addWithName(Set<LrOneItem> set, String name){
        int ret = super.addWithName(set, name);
        for(LrOneItem item : set){
            containers.computeIfAbsent(item, key -> new HashSet<>()).add(size() - 1);
        }
        return ret;
    }

    /**
     * Find the state that contains a certain subset of LR(1) items
     * @param items Set of items to check
     * @return State id of matching state
     */
    private int getContaining(Set<LrOneItem> items){
        Integer present = containerStore.get(items);
        if(present != null)
            return present;
        Set<Integer> retained = new HashSet<>(ids.values());
        for(LrOneItem item : items) {
            if(!containers.containsKey(item))
                throw new InvalidStoreRequestException("Item " + item + " not in container!");
            retained.retainAll(containers.get(item));
        }
        if(retained.size() == 0)
            throw new InvalidStoreRequestException("No matching state found for items subset");
        if(retained.size() > 1)
            throw new InvalidStoreRequestException("Conflicting states found for items subset");
        int id = retained.stream().findFirst().get();
        containerStore.put(items, id);
        return id;
    }

    /**
     * Generate an LR parse table from these item sets
     * @param ruleStore Production rules
     * @param termStore Parse terms
     * @return Generated parse table
     */
    public LrParseTable generateParseTable(ProductionRuleStore ruleStore, ParseTermStore termStore){
        LrParseTable table = new LrParseTable();
        LrOneItem initialItem = ruleStore.getStartRule().itemAt(0).lookahead(termStore.getEndTerm());
        for(IndexNameStoreEntry<Set<LrOneItem>> state : this){
            for(LrOneItem item : state.entry){
                if(item.equals(initialItem))
                    table.setInitialState(state.id);
                ParseTerm nextTerm = item.getItem().getNextTerm();
                if(nextTerm == null){
                    if(item.getItem().getRule().equals(ruleStore.getStartRule())){
                        table.setAction(state.id,
                                termStore.getEndTerm(),
                                LrParseTable.ParsingAction.Action.ACCEPT,
                                size());
                    }
                    else if(!item.getLookahead().equals(termStore.getEpsilonTerm())){
                        table.setAction(state.id,
                                item.getLookahead(),
                                LrParseTable.ParsingAction.Action.REDUCE,
                                ruleStore.getId(item.getItem().getRule()));
                    }
                }
                else if(nextTerm.isTerminal() && !nextTerm.equals(termStore.getEpsilonTerm())){
                    Set<LrOneItem> goTo = LrOneItem.gotos(state.entry, nextTerm, ruleStore, termStore);
                    try {
                        int id = getContaining(goTo);
                        table.setAction(state.id, nextTerm, LrParseTable.ParsingAction.Action.SHIFT, id);
                    }catch(Exception e){
                        System.err.println("Starting error state on term " + nextTerm + " is...\n=========\n" + state.entry + "\n=========");
                        throw e;
                    }
                }
            }
            for(int i = termStore.getStartTerm().getId(); i < termStore.size(); i++){
                ParseTerm term = termStore.get(i);
                Set<LrOneItem> gotos = LrOneItem.gotos(state.entry, term, ruleStore, termStore);
                if(!gotos.isEmpty())
                    table.setAction(state.id, term, LrParseTable.ParsingAction.Action.GOTO, getContaining(gotos));
            }
        }
        return table;
    }

}
