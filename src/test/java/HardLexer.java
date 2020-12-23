import com.funguscow.gible.lexer.Token;
import com.funguscow.gible.lexer.TokenType;
import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.lexer.provider.TokenProvider;
import com.funguscow.gible.lexer.regex.Dfa;
import com.funguscow.gible.lexer.Lexer;
import com.funguscow.gible.lexer.regex.CRange;
import com.funguscow.gible.util.CodePointStream;
import java.lang.StringBuilder;

/**
  * Generated hardcoded lexer
 */
public class HardLexer extends TokenProvider{
    private StringBuilder lexeme;
    private int state;
    private int nextTokenId;
    private CodePointStream input;
    /**
     */
    private static TokenTypeStore generateStore(){
        TokenTypeStore tokenTypeStore = new TokenTypeStore();
        tokenTypeStore.registerToken(new TokenType(3, "STRING").withLexeme());
        tokenTypeStore.registerToken(new TokenType(4, "ID").withLexeme());
        tokenTypeStore.registerToken(new TokenType(5, "WS"));
        return tokenTypeStore;
    }

    /**
     */
    public HardLexer(CodePointStream input){
        super(generateStore());
        this.input = input;
        lexeme = new StringBuilder();
    }

    /**
     */
    public Token getToken(){
        int line = input.getLineno();
        int col = input.getColno();
        state = 0;
        nextTokenId = -1;
        lexeme.setLength(0);
        Dfa.DfaMatch match;
        while(nextTokenId == -1){
           switch(state){
               default -> {
                   match = new Dfa.DfaMatch(-1, 0);
               }
               case 0 -> {
                   match = process_0(input);
               }
               case 1 -> {
                   match = process_1(input);
               }
           }
           if(match.acceptance == -1){
               if(input.eof())
                   return new Token(TokenType.EOF);
               throw new Lexer.LexicalError(String.format("Unidentified token \"%s\" at %d:%d", lexeme.toString(), line, col));
           }
           switch(nextTokenId){
               case 5 -> {
                   nextTokenId = -1;
                   lexeme.setLength(0);
               }
           }
        }
        TokenType tokenType = store.get(nextTokenId);
        if(tokenType.doesHaveLexeme())
           return new Token(tokenType, lexeme.toString());
        return new Token(tokenType);
    }

    /**
     */
    private Dfa.DfaMatch process_0(CodePointStream input){
        Dfa.DfaMatch match = Dfa.hardExecute(input, this::transitionFor_0, this::acceptFor_0, 2);
        switch(match.acceptance){
           case -1 -> {
              lexeme.setLength(0);
              lexeme.append(match.lexeme);
           }
           case 1 -> {
               lexeme.append(match.lexeme);
               nextTokenId = 4;
           }
           case 2 -> {
               lexeme.append(match.lexeme);
               nextTokenId = 5;
           }
           case 0 -> {
               state = 1;
           }
        }
        return match;
    }

    /**
     */
    private CRange transitionFor_0(int currentState, int symbol){
        switch(currentState){
           case 0 -> {
           }
           case 1 -> {
               if(symbol >= 61 && symbol < 62){
                   return new CRange(62, 62, 3);
               }
           }
           case 2 -> {
               if(symbol >= 9 && symbol < 11){
                   return new CRange(11, 11, 4).mark(true);
               }
               if(symbol >= 13 && symbol < 14){
                   return new CRange(14, 14, 4).mark(true);
               }
               if(symbol >= 32 && symbol < 33){
                   return new CRange(33, 33, 4).mark(true);
               }
               if(symbol >= 34 && symbol < 35){
                   return new CRange(35, 35, 7).mark(true);
               }
               if(symbol >= 58 && symbol < 59){
                   return new CRange(59, 59, 1);
               }
           }
           case 3 -> {
               if(symbol >= 0 && symbol < 59){
                   return new CRange(59, 59, 3);
               }
               if(symbol >= 59 && symbol < 60){
                   return new CRange(60, 60, 0).mark(true);
               }
               if(symbol >= 60 && symbol < 92){
                   return new CRange(92, 92, 3);
               }
               if(symbol >= 92 && symbol < 93){
                   return new CRange(93, 93, 6);
               }
               if(symbol >= 93 && symbol < 1114112){
                   return new CRange(1114112, 1114112, 3);
               }
           }
           case 4 -> {
               if(symbol >= 9 && symbol < 11){
                   return new CRange(11, 11, 4).mark(true);
               }
               if(symbol >= 13 && symbol < 14){
                   return new CRange(14, 14, 4).mark(true);
               }
               if(symbol >= 32 && symbol < 33){
                   return new CRange(33, 33, 4).mark(true);
               }
           }
           case 5 -> {
               if(symbol >= 0 && symbol < 59){
                   return new CRange(59, 59, 3);
               }
               if(symbol >= 59 && symbol < 60){
                   return new CRange(60, 60, 0).mark(true);
               }
               if(symbol >= 60 && symbol < 92){
                   return new CRange(92, 92, 3);
               }
               if(symbol >= 92 && symbol < 93){
                   return new CRange(93, 93, 6);
               }
               if(symbol >= 93 && symbol < 1114112){
                   return new CRange(1114112, 1114112, 3);
               }
           }
           case 6 -> {
               if(symbol >= 0 && symbol < 59){
                   return new CRange(59, 59, 3);
               }
               if(symbol >= 59 && symbol < 60){
                   return new CRange(60, 60, 5).mark(true);
               }
               if(symbol >= 60 && symbol < 92){
                   return new CRange(92, 92, 3);
               }
               if(symbol >= 92 && symbol < 93){
                   return new CRange(93, 93, 6);
               }
               if(symbol >= 93 && symbol < 1114112){
                   return new CRange(1114112, 1114112, 3);
               }
           }
           case 7 -> {
           }
        }
        return null;
    }

    /**
     */
    private int acceptFor_0(int state){
        switch(state){
           case 7 -> {return 0;}
           case 1, 2, 3, 6 -> {return -1;}
           case 0, 5 -> {return 1;}
           case 4 -> {return 2;}
        }
        return -1;
    }

    /**
     */
    private Dfa.DfaMatch process_1(CodePointStream input){
        Dfa.DfaMatch match = Dfa.hardExecute(input, this::transitionFor_1, this::acceptFor_1, 5);
        switch(match.acceptance){
           case -1 -> {
              lexeme.setLength(0);
              lexeme.append(match.lexeme);
           }
           case 5, 6 -> {
           }
           case 4 -> {
               lexeme.append("\\");
           }
           case 2 -> {
               nextTokenId = 3;
               state = 0;
           }
           case 0 -> {
               lexeme.append("\"");
           }
           case 3 -> {
               lexeme.append(match.lexeme);
           }
           case 1 -> {
               lexeme.append("\n");
           }
        }
        return match;
    }

    /**
     */
    private CRange transitionFor_1(int currentState, int symbol){
        switch(currentState){
           case 0 -> {
           }
           case 1 -> {
           }
           case 2 -> {
           }
           case 3 -> {
               if(symbol >= 0 && symbol < 1114112){
                   return new CRange(1114112, 1114112, 2).mark(true);
               }
           }
           case 4 -> {
               if(symbol >= 0 && symbol < 34){
                   return new CRange(34, 34, 8);
               }
               if(symbol >= 34 && symbol < 35){
                   return new CRange(35, 35, 6).mark(true);
               }
               if(symbol >= 35 && symbol < 92){
                   return new CRange(92, 92, 8);
               }
               if(symbol >= 92 && symbol < 93){
                   return new CRange(93, 93, 1).mark(true);
               }
               if(symbol >= 93 && symbol < 110){
                   return new CRange(110, 110, 8);
               }
               if(symbol >= 110 && symbol < 111){
                   return new CRange(111, 111, 0).mark(true);
               }
               if(symbol >= 111 && symbol < 1114112){
                   return new CRange(1114112, 1114112, 8);
               }
           }
           case 5 -> {
               if(symbol >= 0 && symbol < 34){
                   return new CRange(34, 34, 7).mark(true);
               }
               if(symbol >= 34 && symbol < 35){
                   return new CRange(35, 35, 9).mark(true);
               }
               if(symbol >= 35 && symbol < 36){
                   return new CRange(36, 36, 3);
               }
               if(symbol >= 36 && symbol < 92){
                   return new CRange(92, 92, 7).mark(true);
               }
               if(symbol >= 92 && symbol < 93){
                   return new CRange(93, 93, 4).mark(true);
               }
               if(symbol >= 93 && symbol < 1114112){
                   return new CRange(1114112, 1114112, 7).mark(true);
               }
           }
           case 6 -> {
           }
           case 7 -> {
               if(symbol >= 0 && symbol < 34){
                   return new CRange(34, 34, 7).mark(true);
               }
               if(symbol >= 36 && symbol < 92){
                   return new CRange(92, 92, 7).mark(true);
               }
               if(symbol >= 93 && symbol < 1114112){
                   return new CRange(1114112, 1114112, 7).mark(true);
               }
           }
           case 8 -> {
           }
           case 9 -> {
           }
        }
        return null;
    }

    /**
     */
    private int acceptFor_1(int state){
        switch(state){
           case 6 -> {return 0;}
           case 3, 4, 5 -> {return -1;}
           case 0 -> {return 1;}
           case 9 -> {return 2;}
           case 7 -> {return 3;}
           case 1 -> {return 4;}
           case 8 -> {return 5;}
           case 2 -> {return 6;}
        }
        return -1;
    }

}
