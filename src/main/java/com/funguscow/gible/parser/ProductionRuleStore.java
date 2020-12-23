package com.funguscow.gible.parser;

import com.funguscow.gible.IndexNameStore;
import com.funguscow.gible.parser.lr.LrOneItem;
import com.funguscow.gible.parser.lr.LrZeroItem;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores all production rules
 */
public class ProductionRuleStore extends IndexNameStore<ProductionRule> {

    private final Map<NonTerminalParseTerm, List<ProductionRule>> ruleMap;
    private ProductionRule startRule;
    private boolean hasFirsts, hasFollows;

    public ProductionRuleStore(){
        ruleMap = new HashMap<>();
        hasFirsts = hasFollows = false;
    }

    @Override
    public int addWithName(ProductionRule item, String name) {
        int id = super.addWithName(item, name);
        List<ProductionRule> rules = ruleMap.computeIfAbsent(item.getResult(), key -> new ArrayList<>());
        rules.add(item);
        return id;
    }

    /**
     * Test if a term is nullable
     * @param parseTerm Term to test
     * @param store Parse terms
     * @return true if parseTerm is nullable
     */
    public boolean isNullable(ParseTerm parseTerm, ParseTermStore store){
        if(!hasFirsts)
            calcFirsts(store);
        return parseTerm.getFirst().contains(store.getEpsilonTerm());
    }

    /**
     * Test if a rule is nullable from a position onward
     * @param rule Rule to test
     * @param position Starting position
     * @param store Parse terms
     * @return true if nullable
     */
    public boolean isNullableFrom(ProductionRule rule, int position, ParseTermStore store){
        List<ParseTerm> terms = rule.getTerms();
        for(int i = position; i < terms.size(); i++)
            if(!isNullable(terms.get(i), store))
                return false;
        return true;
    }

    /**
     *
     * @return The generated starting rule S' -> S
     */
    public ProductionRule getStartRule(){
        return startRule;
    }

    /**
     *
     * @param result A desired result term
     * @return A list of all rules that produce result
     */
    public List<ProductionRule> getRulesFor(NonTerminalParseTerm result){
        return ruleMap.get(result);
    }

    /**
     * Create the start rule for a desired target term
     * @param parseTermStore Parse terms
     * @param target Starting term
     */
    public void createStartRuleFor(ParseTermStore parseTermStore, ParseTerm target){
        NonTerminalParseTerm startTerm = parseTermStore.getStartTerm();
        startRule = new ProductionRule(startTerm, target);
        addWithName(startRule, startRule.toString());
    }

    /**
     * Calculate the sets FIRST for each term in store
     * @param store Parse terms
     */
    public void calcFirsts(ParseTermStore store){
        boolean didChange;
        do {
            didChange = false;
            for (NonTerminalParseTerm nonTerminal : ruleMap.keySet()) {
                Set<ParseTerm> first = nonTerminal.getFirst();
                int oldCount = first.size();
                for (ProductionRule rule : ruleMap.get(nonTerminal)) {
                    if (rule.isNull())
                        first.add(store.getEpsilonTerm());
                    else{
                        List<ParseTerm> terms = rule.getTerms();
                        boolean nullable = false;
                        for(ParseTerm term : terms){
                            nullable = false;
                            for(ParseTerm previous : term.getFirst()){
                                if(!previous.equals(store.getEpsilonTerm())){
                                    first.add(previous);
                                }
                                else{
                                    nullable = true;
                                }
                            }
                            if(!nullable)
                                break;
                        }
                        if(nullable)
                            first.add(store.getEpsilonTerm());
                    }
                }
                int newCount = first.size();
                if(newCount > oldCount)
                    didChange = true;
            }
        }while(didChange);
        hasFirsts = true;
    }

    /**
     * Calculate the set FOLLOW for each term in store
     * @param store Parse terms
     */
    public void calcFollows(ParseTermStore store){
        store.getStartTerm().getFollow().add(store.getEndTerm());
        boolean didChange;
        do{
            didChange = false;
            for(NonTerminalParseTerm nonTerminal : ruleMap.keySet()){
                Set<ParseTerm> follow = nonTerminal.getFollow();
                for(ProductionRule rule : ruleMap.get(nonTerminal)){
                    boolean nullSoFar = true;
                    List<ParseTerm> terms = rule.getTerms();
                    for(int i = terms.size() - 1; i >= 0; i--){
                        ParseTerm term = terms.get(i);
                        if(i > 0){
                            ParseTerm previous = terms.get(i - 1);
                            if(!previous.isTerminal()){
                                NonTerminalParseTerm previousNT = (NonTerminalParseTerm)previous;
                                Set<ParseTerm> previousFollow = previousNT.getFollow();
                                int oldCount = previousFollow.size();
                                for(ParseTerm follows : term.getFirst()){
                                    if(!follows.equals(store.getEpsilonTerm()))
                                        previousFollow.add(follows);
                                }
                                if(previousFollow.size() > oldCount)
                                    didChange = true;
                            }
                        }
                        if(nullSoFar && !term.isTerminal()){
                            NonTerminalParseTerm termNT = (NonTerminalParseTerm)term;
                            Set<ParseTerm> termFollow = termNT.getFollow();
                            int oldCount = termFollow.size();
                            termFollow.addAll(follow);
                            if(termFollow.size() > oldCount)
                                didChange = true;
                        }
                        if(!term.getFirst().contains(store.getEpsilonTerm()))
                            nullSoFar = false;
                    }
                }
            }
        }while(didChange);
        hasFollows = true;
    }

    /**
     * Construct the set of canonical LR(0) item sets
     * @param termStore Parse terms
     * @return Set C or LR(0) items
     */
    public Set<Set<LrZeroItem>> canonicalLr0Sets(ParseTermStore termStore){
        Set<Set<LrZeroItem>> sets = new HashSet<>();
        Set<LrZeroItem> initial = new HashSet<>();
        initial.add(startRule.itemAt(0));
        LrZeroItem.closure(initial, this, termStore);
        sets.add(initial);
        Set<Set<LrZeroItem>> temp = new HashSet<>();
        boolean didChange;
        do{
            int initialSize = sets.size();
            for(Set<LrZeroItem> set : sets){
                for(IndexNameStoreEntry<ParseTerm> termEntry : termStore){
                    Set<LrZeroItem> gotos = LrZeroItem.gotos(set, termEntry.entry, this, termStore);
                    if(!gotos.isEmpty())
                        temp.add(gotos);
                }
            }
            sets.addAll(temp);
            temp.clear();
            didChange = sets.size() > initialSize;
        }while(didChange);
        return sets;
    }

    private void mergeOrAdd(Set<Set<LrOneItem>> existing, Set<LrOneItem> recent){
        Set<LrZeroItem> core = recent.stream().map(LrOneItem::getItem).collect(Collectors.toSet());
        Set<LrOneItem> dest = null;
        for(Set<LrOneItem> old : existing){
            Set<LrZeroItem> oldCore = old.stream().map(LrOneItem::getItem).collect(Collectors.toSet());
            if(core.equals(oldCore)){
                dest = old;
            }
        }
        if(dest == null)
            existing.add(recent);
        else
            dest.addAll(recent);
    }

    /**
     * Construct the set C of canonical LR(1) items
     * @param termStore Parse terms
     * @param progressiveLalr true to merge LALR(1) sets on-the-fly
     * @return Set of LR(1) or LALR(1) items
     */
    public Set<Set<LrOneItem>> canonicalLr1Sets(ParseTermStore termStore, boolean progressiveLalr){
        Set<Set<LrOneItem>> sets = new HashSet<>();
        Set<LrOneItem> initial = new HashSet<>();
        initial.add(startRule.itemAt(0).lookahead(termStore.getEndTerm()));
        LrOneItem.closure(initial, this, termStore);
        sets.add(initial);
        Set<Set<LrOneItem>> temp = new HashSet<>();
        boolean didChange;
        do{
            int initialSize = sets.size();
            temp.addAll(sets);
            for(Set<LrOneItem> set : sets){
                for(IndexNameStoreEntry<ParseTerm> termEntry : termStore){
                    Set<LrOneItem> gotos = LrOneItem.gotos(set, termEntry.entry, this, termStore);
                    if(!gotos.isEmpty()) {
                        if(progressiveLalr)
                            mergeOrAdd(temp, gotos);
                        else
                            temp.add(gotos);
                    }
                }
            }
            sets.clear();
            sets.addAll(temp);
            temp.clear();
            didChange = sets.size() > initialSize;
        }while(didChange);
        return sets;
    }
}
