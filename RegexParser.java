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
public class RegexParser extends AbstractHardcodedParser{
    /**
      * @param provider Provides tokens
     */
    public SyntaxTree parse(TokenProvider provider){
        boolean accept = false;
        boolean erred = false;
        Stack<SyntaxTree> symbols = new Stack<>();
        Stack<Integer> states = new Stack<>();
        int state = 30;
        SyntaxTree lookingAt = null;
        while(!accept){
           if(lookingAt == null)
               lookingAt = new SyntaxTree(provider.next(), termStore);
           switch(state){
               case 0 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 5 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(15), 1, children);
                       }
                       case 3 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 1;
                       }
                       case 15 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 29;
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
                       case 16 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 0;
                       }
                       case 4 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 11;
                       }
                       case 18 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 20;
                       }
                       case 12 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 4;
                       }
                       case 19 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 15;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 27;
                       }
                       case 6 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 14;
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
                       case 7 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           for(int i = temp.numChildren() - 1; i >= 0; i--)
                               children.add(temp.getChild(i));
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(21), 15, children);
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
                       case 7 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(21), 14, children);
                       }
                       case 21 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 10;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 8;
                       }
                       case 22 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 17;
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
                       case 0, 3, 4, 5, 6, 8, 10, 12 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(19), 9, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 5 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 13;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 6 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 3, 4, 5, 6, 10, 12 -> {
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
                           lookingAt = new SyntaxTree(termStore.get(18), 7, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 7 -> {
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
               case 8 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 11 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 5;
                       }
                       case 7, 10 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(22), 16, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 9 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 3, 4, 5, 6, 8, 10, 12 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           symbols.pop();
                           states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           state = states.pop();
                           symbols.pop();
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(19), 10, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 10 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 7 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           for(int i = temp.numChildren() - 1; i >= 0; i--)
                               children.add(temp.getChild(i));
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(20), 13, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 11 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 4 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 11;
                       }
                       case 18 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 20;
                       }
                       case 14 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 21;
                       }
                       case 12 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 4;
                       }
                       case 19 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 15;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 27;
                       }
                       case 6 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 14;
                       }
                       case 16 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 28;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 12 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 5 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           for(int i = temp.numChildren() - 1; i >= 0; i--)
                               children.add(temp.getChild(i));
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(14), 0, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 13 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 7, 10 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           states.pop();
                           symbols.pop();
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(22), 17, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 14 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 22 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 3;
                       }
                       case 20 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 26;
                       }
                       case 9 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 16;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 8;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 15 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 8 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 6;
                       }
                       case 0, 3, 4, 5, 6, 10, 12 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(18), 6, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 16 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 22 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 24;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 8;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 17 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 21 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 2;
                       }
                       case 7 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(21), 14, children);
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 8;
                       }
                       case 22 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 17;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 18 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 4 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 11;
                       }
                       case 0, 3, 5 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(17), 4, children);
                       }
                       case 12 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 4;
                       }
                       case 19 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 15;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 27;
                       }
                       case 17 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 25;
                       }
                       case 6 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 14;
                       }
                       case 18 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 18;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 19 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 3, 5 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           for(int i = temp.numChildren() - 1; i >= 0; i--)
                               children.add(temp.getChild(i));
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(16), 3, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 20 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 4 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 11;
                       }
                       case 0, 3, 5 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(17), 4, children);
                       }
                       case 12 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 4;
                       }
                       case 19 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 15;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 27;
                       }
                       case 6 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 14;
                       }
                       case 17 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 19;
                       }
                       case 18 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 18;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 21 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 5 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 9;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 22 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 3, 4, 5, 6, 8, 10, 12 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           symbols.pop();
                           states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           state = states.pop();
                           symbols.pop();
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(19), 11, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 23 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 7 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           for(int i = temp.numChildren() - 1; i >= 0; i--)
                               children.add(temp.getChild(i));
                           states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(20), 12, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 24 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 21 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 23;
                       }
                       case 7 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(21), 14, children);
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 8;
                       }
                       case 22 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 17;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 25 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 3, 5 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           for(int i = temp.numChildren() - 1; i >= 0; i--)
                               children.add(temp.getChild(i));
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(17), 5, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 26 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 7 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 22;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 27 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 3, 4, 5, 6, 8, 10, 12 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           state = states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(19), 8, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 28 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 5 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(15), 1, children);
                       }
                       case 3 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 1;
                       }
                       case 15 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 12;
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 29 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 0, 5 -> {
                           List<SyntaxTree> children = new ArrayList<>();
                           SyntaxTree temp;
                           states.pop();
                           temp = symbols.pop();
                           for(int i = temp.numChildren() - 1; i >= 0; i--)
                               children.add(temp.getChild(i));
                           states.pop();
                           temp = symbols.pop();
                           children.add(temp);
                           state = states.pop();
                           symbols.pop();
                           provider.pushBack();
                           Collections.reverse(children);
                           lookingAt = new SyntaxTree(termStore.get(15), 2, children);
                       }
                       default -> {
                           state = panic(state, lookingAt, states, symbols, provider);
                           lookingAt = null;
                           erred = true;
                       }
                   }
               }
               case 30 -> {
                   switch(lookingAt.getTerm().getId()){
                       case 14 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 7;
                       }
                       case 4 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 11;
                       }
                       case 18 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 20;
                       }
                       case 12 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 4;
                       }
                       case 19 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 15;
                       }
                       case 10 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 27;
                       }
                       case 6 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 14;
                       }
                       case 16 -> {
                           symbols.push(lookingAt);
                           lookingAt = null;
                           states.push(state);
                           state = 28;
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
    public RegexParser(TokenTypeStore tokenStore){
        super(tokenStore);
        termStore.registerNonTerminal("regex");
        termStore.registerNonTerminal("regex_0");
        termStore.registerNonTerminal("or_piece");
        termStore.registerNonTerminal("or_piece_0");
        termStore.registerNonTerminal("cat_piece");
        termStore.registerNonTerminal("piece");
        termStore.registerNonTerminal("char_class");
        termStore.registerNonTerminal("char_class_0");
        termStore.registerNonTerminal("char_range");
    }

    /**
     */
    public int panic(int state, SyntaxTree symbol, Stack<Integer> states, Stack<SyntaxTree> symbols, TokenProvider provider){
        StringBuilder expectedBuilder = new StringBuilder();
        switch(state){
           case 17, 3, 24 -> {
               expectedBuilder.append("CHAR, RBRACK");
           }
           case 0, 19, 25, 28 -> {
               expectedBuilder.append("VBAR, RPAREN");
           }
           case 6 -> {
               expectedBuilder.append("DOT, LBRACK, RPAREN, CHAR, LPAREN, VBAR");
           }
           case 30 -> {
               expectedBuilder.append("LPAREN, DOT, LBRACK, CHAR");
           }
           case 14 -> {
               expectedBuilder.append("CHAR, CARAT");
           }
           case 1, 11 -> {
               expectedBuilder.append("CHAR, LPAREN, DOT, LBRACK");
           }
           case 7 -> {
               expectedBuilder.append("");
           }
           case 27 -> {
               expectedBuilder.append("DOT, RPAREN, LBRACK, LPAREN, CHAR, MODIFIER, VBAR");
           }
           case 16, 5 -> {
               expectedBuilder.append("CHAR");
           }
           case 4 -> {
               expectedBuilder.append("CHAR, RPAREN, VBAR, LPAREN, MODIFIER, LBRACK, DOT");
           }
           case 18, 20 -> {
               expectedBuilder.append("CHAR, LPAREN, DOT, LBRACK, VBAR, RPAREN");
           }
           case 13 -> {
               expectedBuilder.append("RBRACK, CHAR");
           }
           case 2, 23, 10, 26 -> {
               expectedBuilder.append("RBRACK");
           }
           case 21, 12, 29 -> {
               expectedBuilder.append("RPAREN");
           }
           case 22 -> {
               expectedBuilder.append("DOT, LBRACK, LPAREN, CHAR, RPAREN, MODIFIER, VBAR");
           }
           case 9 -> {
               expectedBuilder.append("MODIFIER, VBAR, DOT, LBRACK, CHAR, RPAREN, LPAREN");
           }
           case 8 -> {
               expectedBuilder.append("MINUS, CHAR, RBRACK");
           }
           case 15 -> {
               expectedBuilder.append("MODIFIER, LPAREN, DOT, CHAR, RPAREN, VBAR, LBRACK");
           }
        }
        System.err.printf("No matching action for symbol %s on state %d at %d:%d\nExpected one of {%s}\n",
        symbol, state, symbol.getLineNo(), symbol.getColNo(), expectedBuilder.toString());
        while(true){
           int targetNonTerminal = -1;
           int targetState = -1;
           switch(state){
               case 2, 4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 19, 21, 22, 23, 25, 26, 27, 29 -> {
               }
               case 0 -> {
                   targetNonTerminal = 15;
                   targetState = 29;
               }
               case 20 -> {
                   targetNonTerminal = 17;
                   targetState = 19;
               }
               case 3 -> {
                   targetNonTerminal = 21;
                   targetState = 10;
               }
               case 24 -> {
                   targetNonTerminal = 21;
                   targetState = 23;
               }
               case 18 -> {
                   targetNonTerminal = 17;
                   targetState = 25;
               }
               case 28 -> {
                   targetNonTerminal = 15;
                   targetState = 12;
               }
               case 30 -> {
                   targetNonTerminal = 14;
                   targetState = 7;
               }
               case 17 -> {
                   targetNonTerminal = 21;
                   targetState = 2;
               }
               case 16 -> {
                   targetNonTerminal = 22;
                   targetState = 24;
               }
               case 1 -> {
                   targetNonTerminal = 16;
                   targetState = 0;
               }
               case 11 -> {
                   targetNonTerminal = 14;
                   targetState = 21;
               }
               case 14 -> {
                   targetNonTerminal = 20;
                   targetState = 26;
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
                       case 18 ->{
                           switch(ahead){
                               case 0, 5, 10, 4, 3, 12, 6 -> inFollow = true;
                           }
                       }
                       case 20, 21 ->{
                           switch(ahead){
                               case 7 -> inFollow = true;
                           }
                       }
                       case 14, 15 ->{
                           switch(ahead){
                               case 0, 5 -> inFollow = true;
                           }
                       }
                       case 19 ->{
                           switch(ahead){
                               case 0, 5, 10, 4, 3, 8, 12, 6 -> inFollow = true;
                           }
                       }
                       case 16, 17 ->{
                           switch(ahead){
                               case 0, 5, 3 -> inFollow = true;
                           }
                       }
                       case 22 ->{
                           switch(ahead){
                               case 10, 7 -> inFollow = true;
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
