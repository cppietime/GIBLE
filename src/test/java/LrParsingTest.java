import com.funguscow.gible.codegen.ClassSource;
import com.funguscow.gible.lexer.Token;
import com.funguscow.gible.lexer.TokenType;
import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.lexer.provider.ConstTokenProvider;
import com.funguscow.gible.lexer.provider.TokenProvider;
import com.funguscow.gible.parser.*;
import com.funguscow.gible.parser.lr.LrParseTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;

public class LrParsingTest {

    public static void main(String... args){

        TokenTypeStore tokenStore = new TokenTypeStore();
        TokenType num = tokenStore.get(tokenStore.registerToken("NUM"));
        TokenType plus = tokenStore.get(tokenStore.registerToken("PLUS"));
        TokenType minus = tokenStore.get(tokenStore.registerToken("MINUS"));
        TokenType star = tokenStore.get(tokenStore.registerToken("STAR"));
        TokenType slash = tokenStore.get(tokenStore.registerToken("SLASH"));

        ParseTermStore termStore = new ParseTermStore();
        termStore.registerTerminalsFromTokenTypes(tokenStore);
        NonTerminalParseTerm expr = (NonTerminalParseTerm)termStore.get(termStore.registerNonTerminal("expr"));
        NonTerminalParseTerm exprs = (NonTerminalParseTerm)termStore.get(termStore.registerNonTerminal("exprs"));

        ProductionRuleStore ruleStore = new ProductionRuleStore();
        ruleStore.addAutoName(new ProductionRule(exprs));
        ruleStore.addAutoName(new ProductionRule(exprs, expr, exprs));
        ruleStore.addAutoName(new ProductionRule(expr, termStore.get(num.getId())));
        ruleStore.createStartRuleFor(termStore, exprs);
        ruleStore.calcFirsts(termStore);
        ruleStore.calcFollows(termStore);

        LrParseTable table = LrParseTable.naiveLr1Table(ruleStore, termStore, false);
        System.out.println(table.toString());
        System.out.println("\n\n==================================\n\n");


        table = LrParseTable.naiveLr1Table(ruleStore, termStore, true);
        System.out.println("Generated table");
        System.out.println(table.toString());

        Token[] tokenArray = {
                new Token(num),
                new Token(num)
        };
        TokenProvider provider = new ConstTokenProvider(Arrays.stream(tokenArray).iterator(), tokenStore);
        SyntaxTree tree = null;
        try {
            tree = table.parse(provider, ruleStore, termStore);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(tree);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("TestParser.java"));
            ClassSource classSource = table.generateSourceCode("TestParser", ruleStore, termStore);
            writer.write(classSource.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TestParser parser = new TestParser(tokenStore);
        provider = new ConstTokenProvider(Arrays.stream(tokenArray).iterator(), tokenStore);
        tree = parser.parse(provider);
        System.out.println("Hardcoded tree:");
        System.out.println(tree);
    }

    private static long usedMemory(){
        gc(); gc();
        long total = Runtime.getRuntime().totalMemory();
        gc(); gc();
        long free = Runtime.getRuntime().freeMemory();
        return total - free;
    }

    private static void gc(){
        try{
            System.gc();
            Thread.sleep(100);
            System.runFinalization();
            Thread.sleep(100);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
