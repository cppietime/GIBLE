import com.funguscow.gible.codegen.ClassSource;
import com.funguscow.gible.lexer.*;
import com.funguscow.gible.lexer.provider.RegexTokenProvider;
import com.funguscow.gible.lexer.provider.TokenProvider;
import com.funguscow.gible.lexer.regex.RegexParser;
import com.funguscow.gible.parser.SyntaxTree;
import com.funguscow.gible.util.CodePointStream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class LexerTest {

    public static void main(String[] args){
        TokenTypeStore tokenTypeStore = new TokenTypeStore();
        int str = tokenTypeStore.registerToken("STRING"),
                id = tokenTypeStore.registerToken("ID"),
                ws = tokenTypeStore.registerToken("WS");
        tokenTypeStore.get(str).withLexeme();
        tokenTypeStore.get(id).withLexeme();

        // Set up state 0
        RegexParser regexParser = new RegexParser(RegexTokenProvider.getDefaultRegexTokenStore());
        String tokenExprs = "\":\\:=([^;]|\\\\;)*;:\\s+";
        CodePointStream regexStream = new CodePointStream(tokenExprs);
        TokenProvider regexProvider = new RegexTokenProvider(regexStream).addDelims((int)':');
        List<SyntaxTree> regexTrees = new ArrayList<>();
        while(!regexStream.eof()){
            regexTrees.add(regexParser.parse(regexProvider));
        }
        LexerState state0 = new LexerState(regexTrees.toArray(new SyntaxTree[0]));
        state0.setRule(0, new LexerTokenRule(
                new LexerTokenRule.Action(LexerTokenRule.Action.ActionType.STATE, 1)
        ));
        state0.setToken(1, id);
        state0.setToken(2, ws);

        //Set up state1
        tokenExprs = "\\\\\":\\\\n:\":[^\"\\\\#]+:\\\\\\\\:\\\\/[^\\\\n\"]:#.";
        regexStream = new CodePointStream(tokenExprs);
        regexProvider = new RegexTokenProvider(regexStream).addDelims((int)':');
        regexTrees.clear();
        while(!regexStream.eof())
            regexTrees.add(regexParser.parse(regexProvider));
        LexerState state1 = new LexerState(regexTrees.toArray(new SyntaxTree[0]));
        state1.setRule(0, new LexerTokenRule(new LexerTokenRule.Action(LexerTokenRule.Action.ActionType.CONST, (int)'"')));
        state1.setRule(1, new LexerTokenRule(new LexerTokenRule.Action(LexerTokenRule.Action.ActionType.CONST, (int)'\n')));
        state1.setRule(2, new LexerTokenRule(
                new LexerTokenRule.Action(LexerTokenRule.Action.ActionType.RETURN, str),
                new LexerTokenRule.Action(LexerTokenRule.Action.ActionType.STATE, 0)
        ));
        state1.setRule(3, new LexerTokenRule(new LexerTokenRule.Action(LexerTokenRule.Action.ActionType.APPEND)));
        state1.setRule(4, new LexerTokenRule(new LexerTokenRule.Action(LexerTokenRule.Action.ActionType.CONST, (int)'\\')));
        state1.setRule(5, new LexerTokenRule());
        state1.setRule(6, new LexerTokenRule());

        //Set up lexer
        Lexer lexer = new Lexer(tokenTypeStore).ignore(ws);
        lexer.addState(state0);
        lexer.addState(state1);
        String parseSource = "\"String\":=stuf;:=morestuf\\;;";
        CodePointStream parseStream = new CodePointStream(parseSource);
        Token token;
        while((token = lexer.lex(parseStream)).getType() != TokenType.EOF){
            System.out.println(token);
        }

        ClassSource lexerSource = lexer.generateSourceCode("HardLexer");
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("HardLexer.java"));
            bw.write(lexerSource.toString());
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        CodePointStream tokensIn = new CodePointStream(parseSource);
        HardLexer hl = new HardLexer(tokensIn);
        while((token = hl.getToken()).getType() != TokenType.EOF){
            System.out.println("hardtok: " + token);
        }
    }

}
