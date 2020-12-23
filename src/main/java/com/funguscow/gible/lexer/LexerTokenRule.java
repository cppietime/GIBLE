package com.funguscow.gible.lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LexerTokenRule {

    /**
     * The rule executed when no other rule is specified
     */
    public static final LexerTokenRule DEFAULT_RULE = new LexerTokenRule(
            new Action(Action.ActionType.APPEND),
            new Action(Action.ActionType.RETURN, -1)
    );

    /**
     * Includes the ability to:
     * Transition to another state
     * Append the lexed string
     * Append a replacement string
     * Return the token
     */
    public static class Action{
        public enum ActionType{
            STATE,
            APPEND,
            CONST,
            RETURN
        }
        public ActionType action;
        public int[] args;
        public Action(ActionType action, int... args){
            this.action = action;
            this.args = args;
        }
    }

    private List<Action> actions;

    public LexerTokenRule(Action... actions){
        this.actions = new ArrayList<>(Arrays.asList(actions));
    }

    public LexerTokenRule(List<Action> actions){
        actions = new ArrayList<>(actions);
    }

    public List<Action> getActions(){
        return actions;
    }

}
