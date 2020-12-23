package com.funguscow.gible.parser;

import java.util.Set;

/**
 * Parse term for syntactical analysis
 */
public abstract class ParseTerm {

    /**
     *
     * @return true if this is a terminal item
     */
    public abstract boolean isTerminal();

    public abstract int hashCode();

    public abstract boolean equals(Object other);

    /**
     *
     * @return This term's name
     */
    public abstract String getName();

    /**
     *
     * @return The set of FIRST(this). {this} if terminal
     */
    public abstract Set<ParseTerm> getFirst();

    /**
     *
     * @return ID of parse term. ID of token if terminal
     */
    public abstract int getId();

    public String toString(){
        return getName();
    }

}
