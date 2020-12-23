package com.funguscow.gible.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Non-terminal parse term
 */
public class NonTerminalParseTerm extends ParseTerm {

    public static final String START = "_START";

    private final int id;
    private final String name;
    private final Set<ParseTerm> first, follow;

    /**
     *
     * @param id ID of parse term
     * @param name Name of parse term
     */
    public NonTerminalParseTerm(int id, String name){
        this.id = id;
        this.name = name;
        first = new HashSet<>();
        follow = new HashSet<>();
    }

    /**
     *
     * @return The set of FOLLOW(this)
     */
    public Set<ParseTerm> getFollow() {
        return follow;
    }

    @Override
    public boolean isTerminal(){
        return false;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public int hashCode(){
        return id * 17 + 1;
    }

    @Override
    public boolean equals(Object other){
        if(other == this)
            return true;
        if(other == null)
            return false;
        if(!(other instanceof NonTerminalParseTerm))
            return false;
        return id == ((NonTerminalParseTerm)other).id;
    }

    @Override
    public Set<ParseTerm> getFirst() {
        return first;
    }

    @Override
    public int getId(){
        return id;
    }
}
