import com.funguscow.gible.codegen.ClassSource;
import com.funguscow.gible.lexer.provider.RegexTokenProvider;
import com.funguscow.gible.lexer.provider.TokenProvider;
import com.funguscow.gible.lexer.regex.RegexParseTermStore;
import com.funguscow.gible.lexer.regex.RegexParser;
import com.funguscow.gible.lexer.regex.RegexRuleStore;
import com.funguscow.gible.lexer.regex.Dfa;
import com.funguscow.gible.lexer.regex.tree.RegexTree;
import com.funguscow.gible.parser.*;
import com.funguscow.gible.parser.lr.LrParseTable;
import com.funguscow.gible.util.CodePointStream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class RegexTest {

    public static void main(String... args){

        ParseTermStore termStore = RegexParseTermStore.getInstance();
        ProductionRuleStore ruleStore = new RegexRuleStore();

        LrParseTable table = LrParseTable.naiveLr1Table(ruleStore, termStore, false);
        System.out.println(table.toString());
        System.out.println("\n\n==================================\n\n");


        table = LrParseTable.naiveLr1Table(ruleStore, termStore, true);
        System.out.println("Generated table");
        System.out.println(table.toString());

        RegexParser parser = new RegexParser(RegexTokenProvider.getDefaultRegexTokenStore());

        String source = "[_A-Za-z]\\w*:\\d+:\\s+:\\w+=/\\w+:\\::(\\w+)/\\:";
        CodePointStream regexStream = new CodePointStream(source);
        TokenProvider provider = new RegexTokenProvider(regexStream).addDelims((int)':');
        SyntaxTree tree = null;
        try {
            List<SyntaxTree> regexTrees = new ArrayList<>();
            while(!regexStream.eof()){
                tree = parser.parse(provider);
                regexTrees.add(tree);
            }
            RegexTree rt = RegexTree.regexOf(regexTrees.toArray(new SyntaxTree[0]));
            Dfa dfa = new Dfa(rt);
            System.out.println(dfa);
            dfa.minimize();
            System.out.println(dfa);
            long i0 = usedMemory();
            long t0 = System.currentTimeMillis();
            Dfa[] dfas = new Dfa[100];
            for(int i = 0; i < 100; i++){
                dfas[i] = new Dfa(RegexTree.regexOf(tree));
            }
            long i1 = usedMemory();
            long t1 = System.currentTimeMillis();
            System.out.println("DFA takes " + (i1 - i0) / 100.0 + " bytes on average over " + (t0 - t1) / 1000.0
                    + " seconds without minimization");
            dfas = null;
            i0 = usedMemory();
            t0 = System.currentTimeMillis();
            dfas = new Dfa[100];
            for(int i = 0; i < 100; i++){
                dfas[i] = new Dfa(RegexTree.regexOf(tree));
                dfas[i].minimize();
            }
            i1 = usedMemory();
            t1 = System.currentTimeMillis();
            System.out.println("DFA takes " + (i1 - i0) / 100.0 + " bytes on average over " + (t0 - t1) / 1000.0
                    + " seconds with minimization");
            CodePointStream stream = new CodePointStream("abc_2 345 a=bc:de===");
            Dfa.DfaMatch match;
            while((match = dfa.execute(stream)).acceptance != -1){
                System.out.println(match);
            }
            System.out.println("Final lexeme = " + match.lexeme);
            if(!stream.eof())
                System.err.println("Error lexing encountered");
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(tree);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("RegexParser.java"));
            ClassSource classSource = table.generateSourceCode("RegexParser", ruleStore, termStore);
            writer.write(classSource.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
