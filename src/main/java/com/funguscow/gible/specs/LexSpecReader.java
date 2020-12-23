package com.funguscow.gible.specs;

import com.funguscow.gible.lexer.TokenType;
import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.parser.*;

/**
 * To be used for reading specifications of tokens from a (text) file
 * Format might be something like:
 *
 * %lexeme TOKEN_NAME
 * %state init
 * TOKEN_NAME(ws):=(ws)regex(ws):=(ws)ACTION(ws),(ws)ACTION\n
 *
 * definitions to use
 *
 * %state base
 * WHITESPACE := \\s+ ->
 * COMMENT := #[^\\n]* ->
 * DIRECTIVE := %\\w+
 * ID := \\w+
 * LBRACE := {
 * RBRACE := }
 * COMMA := ,
 * NUM := \\d+
 * ASN := :=
 * VBAR := |
 * RULE := ^ | - | &
 * :\\s+ -> in_regex
 *
 * %state in_regex
 * REGEX := (([^;\\s]|\\\\;)*)/;
 *
 * lines := EPSILON | line ^lines
 * line := directive | tokdef | ruledef
 * directive := DIRECTIVE ID
 * tokdef := lhs REGEX
 * lhs := ID | -LBRACE rules -RBRACE
 * rules := rule | ^rules -COMMA rule
 * rule := ID | ID ID
 * ruledef := ID -ASN &rhs
 * rhs := def | ^rhs -VBAR def | EPSILON
 * def := term | ^def term
 * term := ID | RULE ID
 *
 */
public class LexSpecReader {

    private final TokenTypeStore tokens;
    private final ParseTermStore terms;
    private final ProductionRuleStore rules;

    private final TokenType WS, ID, NUMBER, REGEX, ASSIGN, COMMENT, DIRECTIVE, LBRACE, RBRACE, COMMA, VBAR, ACTION;
    private final NonTerminalParseTerm lines, line, instruction, tokenDef, tokenLhs, ruleList, rule, production, termRhs, termDef, term;

    public LexSpecReader(){
        tokens = new TokenTypeStore();
        WS = tokens.get(tokens.registerToken("WS"));
        ID = tokens.get(tokens.registerToken("ID"));
        NUMBER = tokens.get(tokens.registerToken("NUMBER"));
        REGEX = tokens.get(tokens.registerToken("REGEX"));
        ASSIGN = tokens.get(tokens.registerToken("ASSIGN"));
        VBAR = tokens.get(tokens.registerToken("VBAR"));
        COMMENT = tokens.get(tokens.registerToken("COMMENT"));
        DIRECTIVE = tokens.get(tokens.registerToken("DIRECTIVE"));
        LBRACE = tokens.get(tokens.registerToken("LBRACE"));
        RBRACE = tokens.get(tokens.registerToken("RBRACE"));
        COMMA = tokens.get(tokens.registerToken("COMMA"));
        ACTION = tokens.get(tokens.registerToken("ACTION"));

        terms = new ParseTermStore();
        terms.registerTerminalsFromTokenTypes(tokens);
        lines       = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("lines"));
        line        = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("line"));
        instruction = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("instruction"));
        tokenDef    = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("token_def"));
        tokenLhs    = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("token_lhs"));
        ruleList    = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("rules"));
        rule        = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("rule"));
        production  = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("production"));
        termRhs     = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("term_rhs"));
        termDef     = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("term_def"));
        term        = (NonTerminalParseTerm)terms.get(terms.registerNonTerminal("term"));

        rules = new ProductionRuleStore();
        rules.addAutoName(new ProductionRule(lines));
        rules.addAutoName(new ProductionRule(lines, line, lines)
                .setModifiers(ProductionRule.Modifier.NONE, ProductionRule.Modifier.EXPAND));
        rules.addAutoName(new ProductionRule(line, instruction).setModifiers(ProductionRule.Modifier.EXPAND));
        rules.addAutoName(new ProductionRule(line, tokenDef).setModifiers(ProductionRule.Modifier.EXPAND));
        rules.addAutoName(new ProductionRule(line, production).setModifiers(ProductionRule.Modifier.EXPAND));
        /* TODO finish defining production rules for file definitions */
    }

}
