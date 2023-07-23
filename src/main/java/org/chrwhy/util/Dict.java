package org.chrwhy.util;

import java.io.*;
import java.util.*;

import static java.io.FileDescriptor.err;

public class Dict {

    public final static Map<String, String> PINYIN = new HashMap<String, String>();
    public final static Map<String, List<String>> CN_PINYIN = new HashMap<String, List<String>>();
    public final static Map<String, String> PINYIN_PREFIX = new HashMap<String, String>();
    public final static Map<String, String> NOT_SPLIT = new HashMap<String, String>();

    public final static String PINYIN_DICT = "pinyin.dict";
    public final static String CN_PINYIN_DICT = "cn_pinyin.dict";
    public final static String N = "n";
    public final static String G = "g";
    public final static String NG = "ng";
    public final static String ER = "er";

    void init() throws IOException {
        loadPinyin();
        loadCnPinyin();
    }

    public static void loadPinyin() {
        File pinyinDict = new File(PINYIN_DICT);
        try {
            BufferedReader br = new BufferedReader(new FileReader(PINYIN_DICT));
            String line = "";
            while ((line = br.readLine()) != null) {
                PINYIN.put(line, line);
                if (!IsIuv(line)) {
                    if (line.length() > 1) {
                        for (int i = 1; i <= line.length(); i++) {
                            PINYIN_PREFIX.put(line.substring(0, i), line.substring(0, i));
                        }
                    } else {
                        PINYIN_PREFIX.put(line, line);
                    }
                }
            }

            NOT_SPLIT.put(ER, ER);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    void loadCnPinyin() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(CN_PINYIN_DICT));
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] temp = line.split("=");
            CN_PINYIN.put(temp[0], Arrays.asList(temp[1].split(",")));
        }
    }

    public static boolean IsPinyinPrefix(String pinyin) {
        return PINYIN_PREFIX.containsKey(pinyin);
    }

    public static boolean IsPinyin(String pinyin) {
        return PINYIN.containsKey(pinyin);
    }

    public static boolean IsLegalPinyin(String pinyin) {
        if (pinyin != null && pinyin.length() == 1) {
            if ("a".equals(pinyin) || "o".equals(pinyin) || "e".equals(pinyin)) {
                return true;
            }
            return false;
        }

        if (pinyin != null) {
            if (pinyin.length() == 2 && pinyin.equals("zh") || pinyin.equals("ch") || pinyin.equals("sh") || pinyin.equals("ng")) {
                return false;
            }
        }

        return PINYIN.containsKey(pinyin);
    }

    public static boolean IsIuv(String pinyin) {
        return "i".equals(pinyin) || "u".equals(pinyin) || "v".equals(pinyin);
    }

    boolean HasIuv(String pinyin) {
        return pinyin.contains("i") || pinyin.contains("u") || pinyin.contains("v");
    }

    public static List<String> GetCnPinyin(String cn) {
        if (CN_PINYIN.containsKey(cn)) {
            return CN_PINYIN.get(cn);
        } else {
            System.out.println(cn + "has no pinyin");
            return new ArrayList<String>();
        }
    }
}
