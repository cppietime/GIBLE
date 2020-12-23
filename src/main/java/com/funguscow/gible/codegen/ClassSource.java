package com.funguscow.gible.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassSource {

    public static class MethodSource{
        private String name;
        private List<String> params;
        private String source;
        private List<String> javadoc;
        private int tabs;

        public MethodSource(String name){
            this.name = name;
            params = new ArrayList<>();
            javadoc = new ArrayList<>();
            tabs = 1;
        }

        public MethodSource withTabs(int t){
            tabs = t;
            return this;
        }

        public MethodSource withParam(String paramType, String paramName, String doc){
            params.add(String.format("%s %s", paramType, paramName));
            if(doc != null)
                javadoc.add(String.format("@param %s %s", paramName, doc));
            return this;
        }

        public MethodSource withParam(String paramType, String paramName){
            return withParam(paramType, paramName, null);
        }

        public MethodSource withSource(String source){
            this.source = source;
            return this;
        }

        public MethodSource annotate(String doc){
            javadoc.add(doc);
            return this;
        }

        public MethodSource returns(String doc){
            javadoc.add(String.format("@return %s", doc));
            return this;
        }

        private String tabbed(String string){
            return String.format("%" + (tabs * 4) + "s%s", "", string);
        }

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();
            builder.append(tabbed("/**\n"));
            for(String line : javadoc){
                builder.append(tabbed(String.format("  * %s\n", line)));
            }
            builder.append(tabbed(" */\n"));
            builder.append(tabbed(String.format("%s(", name)));
            for(int i = 0; i < params.size(); i++){
                String param = params.get(i);
                if(i > 0)
                    builder.append(", ");
                builder.append(param);
            }
            builder.append("){\n");
            tabs ++;
            builder.append(source.lines().map(this::tabbed).collect(Collectors.joining("\n")));
            tabs --;
            builder.append("\n").append(tabbed("}\n"));
            return builder.toString();
        }
    }

    private final String name;
    private String baseClass;
    private final List<String> interfaces;
    private final List<String> members;
    private final List<MethodSource> methods;
    private final List<String> imports;
    private final List<String> javadoc;

    public ClassSource(String name){
        this.name = name;
        interfaces = new ArrayList<>();
        members = new ArrayList<>();
        methods = new ArrayList<>();
        imports = new ArrayList<>();
        javadoc = new ArrayList<>();
    }

    public ClassSource extend(String base){
        baseClass = base;
        return this;
    }

    public ClassSource implement(String inter){
        interfaces.add(inter);
        return this;
    }

    public ClassSource withMember(String member){
        members.add(member);
        return this;
    }

    public ClassSource withMethod(MethodSource method){
        methods.add(method);
        return this;
    }

    public ClassSource imports(String imp){
        imports.add(imp);
        return this;
    }

    public ClassSource annotate(String doc){
        javadoc.add(doc);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(String imp : imports)
            builder.append(String.format("import %s;\n", imp));
        builder.append("/**\n");
        for(String doc : javadoc)
            builder.append(String.format("  * %s\n", doc));
        builder.append(" */\n");
        builder.append(name);
        if(baseClass != null)
            builder.append(String.format(" extends %s", baseClass));
        if(!interfaces.isEmpty()){
            builder.append(" implements ");
            for(int i = 0; i < interfaces.size(); i++){
                String inter = interfaces.get(i);
                if(i > 0)
                    builder.append(", ");
                builder.append(inter);
            }
        }
        builder.append("{\n");
        for(String member : members)
            builder.append(String.format("%4s%s;\n", "", member));
        for(MethodSource method : methods){
            builder.append(method.toString()).append("\n");
        }
        builder.append("}\n");
        return builder.toString();
    }
}
