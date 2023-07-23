package org.chrwhy.parser;


import org.chrwhy.util.Dict;
import org.chrwhy.util.Util;

import java.util.*;

public class Parser {

    public class PinyinNode {
        String Pinyin = "";
        String Leftover = "";
        PinyinNode[] DirectedNodes;
    }

    public class ParsedCandidate {
        public ParsedCandidate(String pinyin, String leftover) {
            this.candidate = pinyin;
            this.leftover = leftover;
        }

        String candidate;
        String leftover;
    }

    List<String> ParseInitial(String input) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < input.length(); i++) {
            if (Dict.IsIuv("" + input.charAt(i))) {
                return null;
            }
            result.add("" + input.charAt(i));
        }

        return result;
    }

    public String[][] Parse(String text) {
        PinyinNode root = new PinyinNode();
        root.DirectedNodes = new PinyinNode[]{};
        root.Leftover = text;
        //t1:=time.Now()
        Map<String, PinyinNode[]> temp = new HashMap<String, PinyinNode[]>();
        parsePinyinDAG(root, temp);

        String[][] pinyinGroups = Traverse(root);
        //log.Println("DAG way cost:",time.Since(t1))
        //pinyinGroups = append(pinyinGroups, ParseInitial(text))
        return pinyinGroups;
    }

    String[][] Traverse(PinyinNode root) {
        String[][] rawPinyinGroups = TraverseDAG(new String[]{}, root);
        String[][] pinyinGroups = new String[][]{};

        for (int j = 0; j < rawPinyinGroups.length; j++) {
            String[] nodePath = rawPinyinGroups[j];
            boolean legal = true;
            for (int i = 0; i < nodePath.length; i++) {
                String step = nodePath[i];
                if (i == (nodePath.length) - 1) {
                    if (!Dict.IsPinyinPrefix(step)) {
                        legal = false;
                        break;
                    }
                } else {
                    if (!Dict.IsLegalPinyin(step)) {
                        legal = false;
                        break;
                    }
                }
            }

            if (legal) {
                //log.Println("legal: ", nodePath)
                pinyinGroups = Util.append(pinyinGroups, nodePath);
            } else {
                //log.Println("illegal: ", nodePath)
                pinyinGroups = Util.append(pinyinGroups, nodePath);
            }
        }
        return pinyinGroups;
    }

    String[][] TraverseDAG(String[] prefix, PinyinNode root) {
        String[][] result = new String[][]{};
        if (root == null || root.DirectedNodes == null || root.DirectedNodes.length < 1) {
            String[] stopResult = Util.append(prefix, root.Pinyin);
            return new String[][]{stopResult};
        } else {
            if (!Util.IsEmpty(root.Pinyin)) {
                prefix = Util.append(prefix, root.Pinyin);
            }

            for (int i = 0; i < root.DirectedNodes.length; i++) {
                PinyinNode child = root.DirectedNodes[i];
                String[][] childResult = TraverseDAG(prefix, child);
                result = Util.append(result, childResult);
            }
            return result;
        }
    }


    void parsePinyinDAG(PinyinNode node, Map<String, PinyinNode[]> parseCache) {
        String raw = node.Leftover;
        if (parseCache.get(raw) != null && parseCache.get(raw).length > 0) {
            node.DirectedNodes = parseCache.get(raw);
            return;
        }

        String head = GreedyFirst(node.Leftover);
        if (Util.IsEmpty(head) || Util.IsEmpty(node.Leftover)) {
            return;
        }
        //log.Println("Greedy head:", head)
        node.DirectedNodes = new PinyinNode[]{};
        //candidates := ElectCandidates(head)
        String[] candidates = new String[]{};

        if (node.Leftover.substring(head.length()).length() > 0) {
            String nextChar = node.Leftover.substring(head.length()).substring(0, 1);
            if (Splittable(head)) {
                candidates = ElectCandidatesV2(head, nextChar);
            }
            if (head.endsWith(Dict.N) || head.endsWith(Dict.G)) {
                //jianing case
                if (Dict.IsPinyinPrefix(nextChar)) {
                    candidates = Util.append(candidates, head);
                }
            } else {
                candidates = Util.append(candidates, head);
            }
        } else {
            candidates = ElectCandidatesV2(head, "");
            candidates = Util.append(candidates, head);
        }

        for (int i = 0; i < candidates.length; i++) {
            String candidate = candidates[i];
            String leftover = raw.substring(candidate.length());
            PinyinNode child = new PinyinNode();
            child.Pinyin = candidate;
            child.Leftover = leftover;

            PinyinNode[] temp = parseCache.get(node.Leftover);
            parseCache.put(node.Leftover, Util.append(temp, child));
            node.DirectedNodes = Util.append(node.DirectedNodes, child);
            parsePinyinDAG(child, parseCache);
        }
    }

    boolean Splittable(String text) {
        return !Dict.NOT_SPLIT.containsKey(text);
    }

    String[] ElectCandidates(String text) {
        String[] result = new String[]{};
        String candidate = "";
        for (int i = 0; i < text.length(); i++) {
            String t = text.charAt(i) + "";
            if (i == text.length() - 1) {
                continue;
            }
            //leftover := text[i:]
            candidate += t;
            if (Dict.IsLegalPinyin(candidate)) {
                result = Util.append(result, candidate);
            }
        }

        //result = append(result, text)
        //log.Println(result)
        return result;
    }

    String[] ElectCandidatesV2(String text, String nextChar) {
        String[] result = new String[]{};
        String candidate = "";

        for (int i = 0; i < text.length(); i++) {
            String t = text.charAt(i) + "";

            if (i == (text.length()) - 1) {
                continue;
            }

            boolean isNgSuffixAndPrefix = true;
            String lastChar = text.substring(text.length() - 1);

            if ((i == text.length() - 2) && lastChar.equals(Dict.G)) {
                if (!Dict.IsPinyinPrefix(Dict.G + nextChar)) {
                    isNgSuffixAndPrefix = false;
                }
            }

            if ((i == text.length() - 2) && lastChar.equals(Dict.N)) {
                if (!Dict.IsPinyinPrefix(Dict.N + nextChar)) {
                    isNgSuffixAndPrefix = false;
                }
            }

            candidate += t;
            String leftover = text.substring(i + 1);
            if (leftover.equals(Dict.NG)) {
                continue;
            }

            if (leftover.length() == 1 && !Dict.IsPinyinPrefix(leftover)) {
                continue;
            }

            if (Dict.IsLegalPinyin(candidate) && isNgSuffixAndPrefix) {
                result = Util.append(result, candidate);
            }
        }

        return result;
    }

    String GreedyFirst(String text) {
        String candidate = "";
        String leftover = "";

        if (text.length() < 1) {
            return "";
        }

        if (text.length() < 6) {
            candidate = text;
            leftover = "";
        } else {
            candidate = text.substring(0, 6);
            leftover = text.substring(6);
        }

        if (Dict.IsPinyin(candidate) || (Util.IsEmpty(leftover) && Dict.IsPinyinPrefix(candidate))) {
            return candidate;
        } else {
            ParsedCandidate parsedCandidate = maxCut(candidate);
            String pinyin = parsedCandidate.candidate;
            String cutLeftover = parsedCandidate.leftover;
            if (Util.IsEmpty(pinyin) || Util.IsEmpty(cutLeftover)) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
            }
            return pinyin;
        }
    }

    String[] GreedyParse(String text) {
        String[] finalResult = new String[]{};
        String candidate = "";
        String leftover = "";

        if (text.length() < 1) {
            return finalResult;
        }

        if (text.length() < 6) {
            candidate = text;
            leftover = "";
        } else {
            candidate = text.substring(0, 6);
            leftover = text.substring(6);
        }

        if (Dict.IsPinyin(candidate) || (Util.IsEmpty(leftover) && Dict.IsPinyinPrefix(candidate))) {
            finalResult = Util.append(finalResult, candidate);
            finalResult = Util.append(finalResult, GreedyParse(leftover));
        } else {
            ParsedCandidate parsedCandidate = maxCut(candidate);
            String pinyin = parsedCandidate.candidate;
            String cutLeftover = parsedCandidate.leftover;

            if (Util.IsEmpty(pinyin) || Util.IsEmpty(cutLeftover)) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
            }

            finalResult = Util.append(finalResult, pinyin);
            finalResult = Util.append(finalResult, GreedyParse(cutLeftover + leftover));
        }

        return finalResult;
    }

    ParsedCandidate minCut(String text) {
        for (int i = 0; i < text.length(); i++) {
            String candidate = text.substring(0, i);
            if (Dict.IsLegalPinyin(candidate)) {
                String leftover = text.substring(i);
                return new ParsedCandidate(candidate, leftover);
            }
        }

        if (Dict.IsLegalPinyin(text)) {
            return new ParsedCandidate(text, "");
        } else {
            return new ParsedCandidate(text.substring(0, 1), text.substring(1));
        }
    }


    ParsedCandidate maxCut(String text) {
        for (int i = text.length() - 1; i > 0; i--) {
            String candidate = text.substring(0, i);
            if (Dict.IsPinyin(candidate)) {
                String leftover = text.substring(i);
                return new ParsedCandidate(candidate, leftover);
            }
        }
        return new ParsedCandidate(text, "");
    }

}

