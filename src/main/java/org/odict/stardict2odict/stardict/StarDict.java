package org.odict.stardict2odict.stardict;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class StarDict {
    //装载读到的单词和位置、长度
    private Map<String, WordPosition> words = new HashMap<String, WordPosition>();

    //随机读取字典内容
    private RandomAccessFile randomAccessFile;

    //最大返回结果数
    public static final int MAX_RESULT = 40;

    public StarDict(Map<String, WordPosition> words, RandomAccessFile raf) {
        this.words = words;
        this.randomAccessFile = raf;
    }

    public List<Map.Entry<String, WordPosition>> searchWord(String term) {
        //直接开头的结果
        List<Map.Entry<String, WordPosition>> resa = new ArrayList<>();
        //间接开头的结果
        List<Map.Entry<String, WordPosition>> resb = new ArrayList<>();

        int i = -1;
        for (Map.Entry<String, WordPosition> en : words.entrySet()) {
            if (en.getKey() == null) {
                System.out.println("oh no null");
            }
            i = en.getKey().toLowerCase().indexOf(term);
            if (i == 0) {
                resa.add(en);
            } else if (i > 0 && resb.size() < MAX_RESULT) {
                resb.add(en);
            }
            if (resa.size() > MAX_RESULT) {
                break;
            }
        }

        Collections.sort(resa, WordComparator);
        Collections.sort(resb, WordComparator);

        if (resa.size() < MAX_RESULT) {
            int need = MAX_RESULT - resa.size();
            if (need > resb.size()) {
                need = resb.size();
            }
            resa.addAll(resb.subList(0, need));
        }
        return resa;
    }


    /**
     * Disposes of the dictionary and closes all open streams
     */
    public void dispose() {
        try {
            this.randomAccessFile.close();
        } catch (IOException e) {
            throw new Error("StarDict dictionary has already been disposed");
        }
    }

    /**
     * 得到字典内容中的内容片断，即为释义
     *
     * @param start offset point
     * @param len   length to get
     * @return
     */
    public String getWordExplanation(int start, int len) {
        String res = "";
        byte[] buf = new byte[len];
        try {
            System.out.println(start);
            System.out.println(len);
            System.out.println(this.randomAccessFile.length());
            this.randomAccessFile.seek(start);
            System.out.println(this.randomAccessFile.getFilePointer());
            int ir = this.randomAccessFile.read(buf);
            if (ir != len) {
                System.out.println("Error occurred, not enought bytes read, wanting:" + len + ",got:" + ir);
            }
            res = new String(buf, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Map<String, WordPosition> getWords() {
        return words;
    }

    public void setWords(Map<String, WordPosition> words) {
        this.words = words;
    }

    /**
     * 测试，显示这些单词
     */
    public void showWords() {
        int i = 0;
        for (Map.Entry<String, WordPosition> en : words.entrySet()) {
            System.out.println(en.getKey() + " :" + en.getValue().getStartPos() + " - " + en.getValue().getLength());
            if (i++ % 25 == 0) {
                System.out.println(this.getWordExplanation(en.getValue().getStartPos(), en.getValue().getLength()));
            }
        }
    }

    /**
     * customer comparator
     */
    private static Comparator<Map.Entry<String, WordPosition>> WordComparator = new Comparator<Map.Entry<String, WordPosition>>() {
        public int compare(Map.Entry<String, WordPosition> ea, Map.Entry<String, WordPosition> eb) {
            return ea.getKey().compareToIgnoreCase(eb.getKey());
        }
    };
}
