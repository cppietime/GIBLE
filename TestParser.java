import com.funguscow.gible.lexer.TokenTypeStore;
import com.funguscow.gible.parser.SyntaxTree;
import com.funguscow.gible.parser.lr.LrParseTable;
import com.funguscow.gible.lexer.provider.TokenProvider;
import com.funguscow.gible.parser.AbstractHardcodedParser;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
/**
  * Generated from an LR parse table for hardcoded parsing
 */
public class TestParser extends AbstractHardcodedParser{
    /**
      * @param provider Provides tokens
     */
    public SyntaxTree parse(TokenProvider provider){
        boolean accept = false;
        boolean erred = false;
        Stack<SyntaxTree> symbols = new Stack<>();
        Stack<Integer> states = new Stack<>();
        int state = 3;
        SyntaxTree lookingAt = null;
        while(!accept){
           if(lookingAt == null)
               lookingAt = new SyntaxTree(provider.next(), termStore);
           switch(state){
               case 0 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 3 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(9), 2, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 1 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 2;
                       }
                       case 9 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 1;
                       }
                       case 3 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 0;
                       }
                       case 0 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(10), 0, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 2 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(10), 1, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 3 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 9 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 1;
                       }
                       case 3 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 0;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 4;
                       }
                       case 0 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(10), 0, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 4 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0 -> {
                           accept = true;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               default -> {
                   state = panic(state, lookingAt, states, symbols, provider);
                   lookingAt = null;
                   erred = true;
               }
           }
        }
        if(erred){
           throw new LrParseTable.LrParseException("Error(s) encountered while parsing");
        }
        return symbols.pop();
    }

    /**
      * @param tokenStore Provides token types
     */
    public TestParser(TokenTypeStore tokenStore){
        super(tokenStore);
        termStore.registerNonTerminal("expr");
        termStore.registerNonTerminal("exprs");
    }

    /**
     */
    public int panic(int state, SyntaxTree symbol, Stack<Integer> states, Stack<SyntaxTree> symbols, TokenProvider provider){
        StringBuilder expectedBuilder = new StringBuilder();
        switch(state){
           case 0, 1, 3 -> {
               expectedBuilder.append("NUM");
           }
           case 2, 4 -> {
               expectedBuilder.append("");
           }
        }
        System.err.printf("No matching action for symbol %s on state %d at %d:%d\nExpected one of {%s}\n",
        symbol, state, symbol.getLineNo(), symbol.getColNo(), expectedBuilder.toString());
        while(true){
           int targetNonTerminal = -1;
           int targetState = -1;
           switch(state){
               case 0, 2, 4 -> {
               }
               case 1, 3 -> {
                   targetNonTerminal = 9;
                   targetState = 1;
               }
           }
           if(targetNonTerminal != -1){
               while(true){
                   int ahead = provider.next().getType().getId();
                   if(ahead == 0){
                       throw new LrParseTable.LrParseException("EOF reached during panic mode");
                   }
                   boolean inFollow = false;
                   switch(targetNonTerminal){
                       case 10 ->{
                           switch(ahead){
                               case 0 -> inFollow = true;
                           }
                       }
                       case 9 ->{
                           switch(ahead){
                               case 0, 3 -> inFollow = true;
                           }
                       }
                   }
                   if(inFollow){
                       symbols.push(new SyntaxTree(termStore.get(targetNonTerminal), 0));
                       states.push(state);
                       provider.pushBack();
                       return targetState;
                   }
               }
           }
           if(states.isEmpty())
               throw new LrParseTable.LrParseException("Parse stack emptied during error recovery");
           state = states.pop();
           symbols.pop();
        }
    }

}
