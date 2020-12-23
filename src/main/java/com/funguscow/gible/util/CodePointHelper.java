package com.funguscow.gible.util;

import java.util.Collection;

public class CodePointHelper {

    public static int MAX_CODE_POINT = 0x110000;

    public static String asSingleString(int point){
        if(point < 0x10000)
            return "" + (char)point;
        point -= 0x10000;
        return (char)(0xD800 + (point >> 10)) + "" + (char)(0xDC00 + (point & 0x3ff));
    }

    public static int charAtAsCodepoint(String str, int pos){
        int c = str.charAt(pos);
        if(c < 0xD800 || c >= 0xE000)
            return c;
        c -= 0xD800;
        int d = str.charAt(pos + 1) - 0xDC00;
        return 0x10000 | (c << 10) | d;
    }

    public static String codePointsToString(Collection<Integer> points){
        StringBuilder builder = new StringBuilder();
        for(int i : points){
            if(i < 0x10000)
                builder.append((char)i);
            else{
                i -= 0x10000;
                int first = (i >> 10) | 0xD800;
                int second = (i & 0x3ff) | 0xDC00;
                builder.append(first).append(second);
            }
        }
        return builder.toString();
    }

}
