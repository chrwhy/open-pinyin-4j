package org.example;

import org.chrwhy.util.Dict;
import org.chrwhy.parser.Parser;
import org.chrwhy.util.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Dict.loadPinyin();
        try {
            Parser parser = new Parser();
            String[][] result = parser.Parse("luxian");
            for (int i = 0; i < result.length; i++) {
                System.out.println(Util.Concat(result[i], "-"));
            }

            testCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void testCase() {
        List<String> testCases = new ArrayList<String>();
        testCases.add("chenhairong");
        testCases.add("xianrenmin");
        testCases.add("xianr");
        testCases.add("aqiang");
        testCases.add("ana");
        testCases.add("abc");
        testCases.add("aaijifeji");
        testCases.add("yiqungaoguiqizhidechairenzaichufaweizhangdongwu");
        testCases.add("lianggehuanglimingcuiliu");
        testCases.add("renshengdeyixunjinhuanmoshijinzunkongduiyue");
        testCases.add("ziranyuyanchulishiyigelishinantideyuyanzhedetianxia");
        testCases.add("bairiyishanj");
        testCases.add("luxian");
        testCases.add("pdfzhuanhuachengword");
        testCases.add("pdfzenmezhuanchengexcel");
        testCases.add("chuangqianmingyueguang");
        testCases.add("angui");
        testCases.add("ning");
        testCases.add("xiongge");
        testCases.add("liaojie");
        testCases.add("liangting");
        testCases.add("jianing");
        testCases.add("libai");
        testCases.add("luxian");
        testCases.add("lixiaoguang");
        testCases.add("chuangqianqianmingyueguang");
        testCases.add("xiangbiyudaduoshurenshuxideshujukudesuoyinxiaolvshangshiwanbaochuantongshujukudexingneng");

        try {
            FileWriter fw = new FileWriter("./output.txt", false);
            try {
                Parser parser = new Parser();
                for (int i = 0; i < testCases.size(); i++) {
                    String[][] result = parser.Parse(testCases.get(i));
                    fw.write(testCases.get(i) + "\n");
                    for (int j = 0; j < result.length; j++) {
                        fw.write(Util.Concat(result[j], " ") + "\n");
                    }
                    fw.write("=========================\n\n");
                }
            } finally {
                fw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}