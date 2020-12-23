package com.funguscow.gible.lexer.regex;

import com.funguscow.gible.lexer.regex.tree.RegexLeaf;

import java.util.*;

/**
 * One state in a DFA
 */
public class DfaState {

    private Set<Integer> positions;

    private TreeSet<CRange> transitions;
    private CRange placeHolder;

    private int accepts;

    /**
     * Construct  a DFA state from a set of leaf positions and regex leaves
     * @param state What positions are included
     * @param leaves Leaves of regex tree
     */
    public DfaState(Set<Integer> state, Map<Integer, RegexLeaf> leaves){
        positions = new HashSet<>(state);
        transitions = new TreeSet<>();
        placeHolder = new CRange(0, 0);
        accepts = -1;
        for(int i : positions){
            RegexLeaf leaf = leaves.get(i);
            if(leaf.getAccepts() > accepts)
                accepts = leaf.getAccepts();
            for(CRange range : leaf.getRanges()){
                transitions.add(new CRange(range.getMin(), range.getMax(), leaf.getFollow()).mark(range.doesMark()));
            }
        }
        transitions = CRange.breakApart(transitions);
    }

    /**
     * Construct a state matching other
     * @param other State to match
     */
    public DfaState(DfaState other){
        placeHolder = new CRange(0, 0);
        accepts = other.accepts;
        positions = new HashSet<>(other.positions);
        transitions = new TreeSet<>();
    }

    /**
     *
     * @return Positions in this state
     */
    public Set<Integer> getPositions(){
        return positions;
    }

    /**
     *
     * @return Transitions this state matches
     */
    public TreeSet<CRange> getTransitions() {
        return transitions;
    }

    /**
     *
     * @return Accepting value of this state, -1 if it does not accept */
    public int getAccepts() {
        return accepts;
    }

    /**
     * Get the ID of the next state for a code point
     * @param codePoint Codepoint triggering transition
     * @return ID of the next state, or -1 for error
     */
    public CRange getNextStateFor(int codePoint){
        placeHolder.makeInt(codePoint);
        CRange transition = transitions.floor(placeHolder);
        if(transition == null)
            return null;
        if(transition.contains(codePoint))
            return transition;
        return null;
    }

    public int hashCode(){
        return positions.hashCode() * 53 + transitions.hashCode();
    }

    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(!(obj instanceof DfaState))
            return false;
        DfaState other = (DfaState)obj;
        return positions.equals(other.positions) && transitions.equals(other.transitions);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(String.format("(%s):", Arrays.toString(positions.toArray())));
        for(CRange transition : transitions){
            if(transition.doesMark())
                builder.append(" @");
            builder.append(String.format(" [%d-%d -> %s]", transition.getMin(), transition.getMax(), transition.getValues()));
        }
        if(accepts > -1)
            builder.append(String.format(" !{%d}!", accepts));
        return builder.toString();
    }

}
