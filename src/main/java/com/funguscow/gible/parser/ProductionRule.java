package com.funguscow.gible.parser;

import com.funguscow.gible.parser.lr.LrZeroItem;

import java.util.Arrays;
import java.util.List;

/**
 * Rule to produce a non-terminal term
 */
public class ProductionRule {

    /**
     * Determine behavior when adding to a reduction.
     * NONE - Add as child as-is
     * OMIT - Leave out of production (e.g. use for symbols that are always in the same position)
     * OMIT_EMPTY - Add only if the symbol is terminal or has children, otherwise omit
     * EXPAND - Add each child of the symbol in-order on the same level as its siblings
     */
    public enum Modifier{
        NONE,
        EXPAND,
        OMIT,
        OMIT_EMPTY
    }

    private final NonTerminalParseTerm left;
    private final List<ParseTerm> right;
    private List<Modifier> modifiers;
    private final String asString;

    /**
     * Construct a new rule to produce left from right, in-order
     * @param left Result of this rule
     * @param terms Ingredients of production, in-order
     */
    public ProductionRule(NonTerminalParseTerm left, ParseTerm... terms){
        this.left = left;
        right = Arrays.asList(terms);
        StringBuilder builder = new StringBuilder();
        builder.append(left.toString()).append(" -> ");
        for(ParseTerm term : terms){
            builder.append(term.toString());
        }
        asString = builder.toString();
    }

    /**
     * Set the modifiers for each term, in order
     * @param modifiers Modifiers, in order, of right-hand-side
     * @return this
     */
    public ProductionRule setModifiers(List<Modifier> modifiers){
        this.modifiers = modifiers;
        return this;
    }

    /**
     * Set the modifiers for each term, in order
     * @param modifiers Modifiers, in order, of right-hand-side
     * @return this
     */
    public ProductionRule setModifiers(Modifier... modifiers){
        return setModifiers(Arrays.asList(modifiers));
    }

    /**
     *
     * @return true if the right-hand-side of this rule is empty
     */
    public boolean isNull(){
        return right.isEmpty();
    }

    /**
     *
     * @return The left-hand-side result of this rule
     */
    public NonTerminalParseTerm getResult(){
        return left;
    }

    /**
     *
     * @return The list of terms making up the right-hand-side
     */
    public List<ParseTerm> getTerms(){
        return right;
    }

    /**
     *
     * @param position Position to fetch
     * @return The item at position in the right-hand-side
     */
    public LrZeroItem itemAt(int position){
        return new LrZeroItem(this, position);
    }

    /**
     *
     * @param position Index of term to check
     * @return Modifier associated with term
     */
    public Modifier modifierAt(int position){
        if(modifiers == null || modifiers.isEmpty())
            return Modifier.NONE;
        return modifiers.get(position);
    }

    @Override
    public String toString(){
        return asString;
    }

    @Override
    public int hashCode(){
        return left.hashCode() * 11 + right.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof ProductionRule))
            return false;
        ProductionRule other = (ProductionRule)obj;
        if(!left.equals(other.left))
            return false;
        if(right.size() != other.right.size())
            return false;
        for(int i = 0; i < right.size(); i++){
            if(!right.get(i).equals(other.right.get(i)))
                return false;
        }
        return true;
    }
}
