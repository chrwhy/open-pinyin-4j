package org.chrwhy.util;

import org.chrwhy.parser.Parser;

public class Util {

    public static String Concat(String[] input, String separator) {
        String concat = "";
        for (int i = 0; i < input.length; i++) {
            if (IsEmpty(concat)) {
                concat += input[i];
            } else {
                concat += separator + input[i];
            }
        }

        return concat;
    }

    public static boolean IsEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static Parser.PinyinNode[] append(Parser.PinyinNode[] src, Parser.PinyinNode temp) {
        int targetLength = 1;
        Parser.PinyinNode[] result = new Parser.PinyinNode[targetLength];
        if (src == null || src.length == 0) {
            result[0] = temp;
            return result;
        }

        targetLength = src.length + 1;
        result = new Parser.PinyinNode[targetLength];

        System.arraycopy(src,0, result, 0, src.length);
        result[targetLength - 1] = temp;
        return result;
    }

    public static String[] append(String[] src, String temp) {
        int targetLength = src.length + 1;
        String[] result = new String[targetLength];
        System.arraycopy(src,0, result, 0, src.length);
        result[targetLength - 1] = temp;
        return result;
    }

    public static String[] append(String[] src, String[] temp) {
        int targetLength = src.length + temp.length;
        String[] result = new String[targetLength];
        System.arraycopy(src,0, result, 0, src.length);
        System.arraycopy(temp, 0, result, src.length, temp.length);
        return result;
    }

    public static String[][] append(String[][] src, String[] temp) {
        int targetLength = src.length + 1;
        String[][] result = new String[targetLength][];
        System.arraycopy(src,0, result, 0, src.length);
        result[targetLength - 1] = temp;
        return result;
    }

    public static String[][] append(String[][] src, String[][] temp) {
        int targetLength = src.length + temp.length;
        String[][] result = new String[targetLength][];
        System.arraycopy(src,0, result, 0, src.length);
        System.arraycopy(temp, 0, result, src.length, temp.length);
        return result;
    }
}