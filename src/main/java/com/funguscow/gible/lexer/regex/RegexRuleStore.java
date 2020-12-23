package com.funguscow.gible.lexer.regex;

import com.funguscow.gible.lexer.TokenType;
import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.lexer.provider.RegexTokenProvider;
import com.funguscow.gible.parser.NonTerminalParseTerm;
import com.funguscow.gible.parser.ParseTermStore;
import com.funguscow.gible.parser.ProductionRule;
import com.funguscow.gible.parser.ProductionRuleStore;

/**
 * Constant store for rules for Regex productions
 */
public class RegexRuleStore extends ProductionRuleStore {
    
    public RegexRuleStore(){
        TokenTypeStore tokenStore = RegexTokenProvider.getDefaultRegexTokenStore();
        TokenType vbar = tokenStore.get(("VBAR"));
        TokenType lparen = tokenStore.get(("LPAREN"));
        TokenType rparen = tokenStore.get(("RPAREN"));
        TokenType lbrack = tokenStore.get(("LBRACK"));
        TokenType rbrack = tokenStore.get(("RBRACK"));
        TokenType mod = tokenStore.get(("MODIFIER"));
        TokenType carat = tokenStore.get(("CARAT"));
        TokenType charac = tokenStore.get(("CHAR"));
        TokenType minus = tokenStore.get(("MINUS"));
        TokenType dot = tokenStore.get(("DOT"));

        ParseTermStore termStore = RegexParseTermStore.getInstance();
        NonTerminalParseTerm regex = (NonTerminalParseTerm)termStore.get(("regex"));
        NonTerminalParseTerm regex_0 = (NonTerminalParseTerm)termStore.get(("regex_0"));
        NonTerminalParseTerm or_piece = (NonTerminalParseTerm)termStore.get(("or_piece"));
        NonTerminalParseTerm or_piece_0 = (NonTerminalParseTerm)termStore.get(("or_piece_0"));
        NonTerminalParseTerm cat_piece = (NonTerminalParseTerm)termStore.get(("cat_piece"));
        NonTerminalParseTerm piece = (NonTerminalParseTerm)termStore.get(("piece"));
        NonTerminalParseTerm char_class = (NonTerminalParseTerm)termStore.get(("char_class"));
        NonTerminalParseTerm char_class_0 = (NonTerminalParseTerm)termStore.get(("char_class_0"));
        NonTerminalParseTerm char_range = (NonTerminalParseTerm)termStore.get(("char_range"));

        addAutoName(new ProductionRule(regex, or_piece, regex_0).
                setModifiers(ProductionRule.Modifier.NONE, ProductionRule.Modifier.EXPAND));
        addAutoName(new ProductionRule(regex_0));
        addAutoName(new ProductionRule(regex_0, termStore.get(vbar.getId()), or_piece, regex_0).
                setModifiers(ProductionRule.Modifier.OMIT, ProductionRule.Modifier.NONE, ProductionRule.Modifier.EXPAND));
        addAutoName(new ProductionRule(or_piece, cat_piece, or_piece_0).
                setModifiers(ProductionRule.Modifier.NONE, ProductionRule.Modifier.EXPAND));
        addAutoName(new ProductionRule(or_piece_0));
        addAutoName(new ProductionRule(or_piece_0, cat_piece, or_piece_0).
                setModifiers(ProductionRule.Modifier.NONE, ProductionRule.Modifier.EXPAND));
        addAutoName(new ProductionRule(cat_piece, piece));
        addAutoName(new ProductionRule(cat_piece, piece, termStore.get(mod.getId())));
        addAutoName(new ProductionRule(piece, termStore.get(charac.getId())));
        addAutoName(new ProductionRule(piece, termStore.get(dot.getId())));
        addAutoName(new ProductionRule(piece, termStore.get(lparen.getId()), regex, termStore.get(rparen.getId())).
                setModifiers(ProductionRule.Modifier.OMIT, ProductionRule.Modifier.NONE, ProductionRule.Modifier.OMIT));
        addAutoName(new ProductionRule(piece, termStore.get(lbrack.getId()), char_class, termStore.get(rbrack.getId())).
                setModifiers(ProductionRule.Modifier.OMIT, ProductionRule.Modifier.NONE, ProductionRule.Modifier.OMIT));
        addAutoName(new ProductionRule(char_class, termStore.get(carat.getId()), char_range, char_class_0).
                setModifiers(ProductionRule.Modifier.NONE, ProductionRule.Modifier.NONE, ProductionRule.Modifier.EXPAND));
        addAutoName(new ProductionRule(char_class, char_range, char_class_0).
                setModifiers(ProductionRule.Modifier.NONE, ProductionRule.Modifier.EXPAND));
        addAutoName(new ProductionRule(char_class_0));
        addAutoName(new ProductionRule(char_class_0, char_range, char_class_0).
                setModifiers(ProductionRule.Modifier.NONE, ProductionRule.Modifier.EXPAND));
        addAutoName(new ProductionRule(char_range, termStore.get(charac.getId())));
        addAutoName(new ProductionRule(char_range, termStore.get(charac.getId()), termStore.get(minus.getId()), termStore.get(charac.getId())).
                setModifiers(ProductionRule.Modifier.NONE, ProductionRule.Modifier.OMIT, ProductionRule.Modifier.NONE));
        createStartRuleFor(termStore, regex);
        calcFirsts(termStore);
        calcFollows(termStore);
    }
    
}
