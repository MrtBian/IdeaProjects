package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utils.FileUtil.readFileByLine;
import static utils.FileUtil.writeFile;

/**
 * 图书索引号类
 *
 * @author Wing
 * @date 2018.07.19
 */
public class BookIndex implements Comparable {

    /**
     * 除去类别号和著者号的其他信息起始值，其值为{@value}
     */
    private final int OTHERNO = 2;

    /**
     * 类别号
     */
    private ClassNo classNo;

    /**
     * 著者号
     */
    private AuthorNo authorNo;

    /**
     * 以/分割索书号的字符数组
     */
    private String[] lines;

    /**
     * lines数组的长度
     */
    private int lineNum;

    /**
     * @param bookIndex 索引号字符串
     */
    BookIndex(String bookIndex) {
        lines   = bookIndex.split("/");
        lineNum = lines.length;
        classNo = new ClassNo(lines[0]);

        if (lineNum >= OTHERNO) {
            authorNo = new AuthorNo(lines[1]);
        }
    }

    /**
     * {@inheritDoc}
     * 比较两个索引号的大小
     *
     * @param o 索引号
     * @return 0为相等，正数为前者大，否则后者大
     */
    @Override
    public int compareTo(Object o) {
        BookIndex b2  = (BookIndex) o;
        int       tmp = classNo.compareTo(b2.classNo);

        if (tmp != 0) {
            return tmp;
        }

        tmp = authorNo.compareTo(b2.authorNo);

        if (tmp != 0) {
            return tmp;
        }

        int count = 2;

        while ((lineNum >= count) && (b2.lineNum >= count)) {
            if ((lineNum == b2.lineNum) && (lineNum == count)) {
                return 0;
            }

            if ((lineNum == count) && (b2.lineNum > lineNum)) {
                return -1;
            }

            if ((b2.lineNum == count) && (b2.lineNum < lineNum)) {
                return -1;
            }

            VolumeOrYear vy1 = new VolumeOrYear(lines[count]);
            VolumeOrYear vy2 = new VolumeOrYear(b2.lines[count]);

            tmp = vy1.compareTo(vy2);

            if (tmp != 0) {
                return tmp;
            }

            count++;
        }

        return 0;
    }

    /**
     * Test Method
     *
     * @param args Param description
     */
    public static void main(String[] args) {
        List<String> bookIndexs = readFileByLine("data\\test_0711.txt");

        Collections.shuffle(bookIndexs);

        List<BookIndex> bis = new ArrayList<>();

        for (String bi : bookIndexs) {
            bis.add(new BookIndex(bi));
        }

        Collections.sort(bis);

        List<String> newStrs = new ArrayList<>();

        for (BookIndex bi : bis) {
            newStrs.add(bi.toString());
        }

        writeFile("data\\old_0711.txt", false, bookIndexs);
        writeFile("data\\new_0711.txt", false, newStrs);
    }

    /**
     * {@inheritDoc}
     * 将索书号对象输出为字符串
     *
     * @return 索书号字符串
     */
    @Override
    public String toString() {
        StringBuilder str     = new StringBuilder();
        boolean       isFirst = true;

        for (String line : lines) {
            if (isFirst) {
                str.append(line);
                isFirst = false;
            } else {
                str.append("/").append(line);
            }
        }

        return str.toString();
    }

    /**
     * 著者号类
     */
    private class AuthorNo implements Comparable {

        /**
         * 著者号字符串
         */
        String anStr = "";

        /**
         * 著者号字符串长度
         */
        int len = 0;

        /**
         * Constructs ...
         *
         * @param authorNo Param description
         */
        AuthorNo(String authorNo) {
            anStr = authorNo;
            len   = authorNo.length();
        }

        /**
         * 按照著者号规则来比较字符大小
         *
         * @param c1 字符1
         * @param c2 字符2
         * @return c1 &lt; c2 -1 <br> c1 == c2 0 <br> c1 &gt; c2 1
         */
        private int compareChar(char c1, char c2) {
            String str = "()-+=0123456789abcdefghijklmnopqrstuvwxyz.";

            return str.indexOf(c1) - str.indexOf(c2);
        }

        @Override
        public int compareTo(Object o) {
            AuthorNo authorNo1 = (AuthorNo) o;
            String   s1        = anStr.toLowerCase(),
                     s2        = authorNo1.anStr.toLowerCase();

            s1 = s1.replaceAll("[^()-+=\\w\\d]", "");
            s2 = s2.replaceAll("[^()-+=\\w\\d]", "");

            int l1 = s1.length(),
                l2 = s2.length();

            if (s1.compareTo(s2) == 0) {
                return 0;
            }

            int index1 = 0,
                index2 = 0, tmp;

            // 是否在括号内
            boolean isIn = false;

            while ((l1 > index1) && (l2 > index2)) {
                char c1 = s1.charAt(index1);
                char c2 = s2.charAt(index2);

                if (isIn && Character.isDigit(c1) && Character.isDigit(c2)) {

                    // 括号内数值按大小排序
                    String tmpStr1 = s1.substring(index1).split("[^0-9]")[0];
                    String tmpStr2 = s2.substring(index1).split("[^0-9]")[0];

                    tmp = Integer.parseInt(tmpStr1) - Integer.parseInt(tmpStr2);

                    if (tmp != 0) {
                        return tmp;
                    }

                    index1 += tmpStr1.length();
                    index2 += tmpStr2.length();
                } else {
                    if (!isIn) {

                        // 忽略括号外的'-'
                        if (c1 == '-') {
                            index1++;
                            c1 = s1.charAt(index1);
                        }

                        if (c2 == '-') {
                            index2++;
                            c2 = s2.charAt(index2);
                        }
                    }

                    tmp = compareChar(c1, c2);

                    if (tmp != 0) {
                        return tmp;
                    }

                    if (c1 == '(') {
                        isIn = true;
                    }

                    index1++;
                    index2++;
                }
            }

            if ((l1 == index1) && (l2 == index2)) {
                return 0;
            }

            if ((l1 == index1) && (l2 > index2)) {
                return -1;
            }

            if ((l1 > index1) && (l2 == index2)) {
                return 1;
            }

            return 0;
        }
    }


    /**
     * 分类号类
     */
    private class ClassNo implements Comparable {

        /**
         * Field description
         */
        String cnStr = "";

        /**
         * Field description
         */
        int len = 0;

        /**
         * Constructs ...
         *
         * @param classNo Param description
         */
        ClassNo(String classNo) {
            cnStr = classNo;
            len   = classNo.length();
        }

        /**
         * 按照分类号规则来比较字符大小
         *
         * @param c1 字符1
         * @param c2 字符2
         * @return c1 &lt; c2 -1 <br> c1 == c2 0 <br> c1 &gt; c2 1
         */
        private int compareChar(char c1, char c2) {
            String str = "-()\"\"=#+:0123456789abcdefghijklmnopqrstuvwxyz.";

            return str.indexOf(c1) - str.indexOf(c2);
        }

        @Override
        public int compareTo(Object o) {
            ClassNo classNo1 = (ClassNo) o;
            String  s1       = cnStr.toLowerCase(),
                    s2       = classNo1.cnStr.toLowerCase();

            s1 = s1.replaceAll("[^-()\"\'=#+:\\w\\d]", "");
            s2 = s2.replaceAll("[^-()\"\'=#+:\\w\\d]", "");

            int l1 = s1.length(),
                l2 = s2.length();

            if (s1.compareTo(s2) == 0) {
                return 0;
            }

            int index = 0, tmp;

            while ((l1 > index) && (l2 > index)) {
                char c1 = s1.charAt(index);
                char c2 = s2.charAt(index);

                tmp = compareChar(c1, c2);

                if (tmp != 0) {
                    return tmp;
                }

                index++;
            }

            if ((l1 == index) && (l2 == index)) {
                return 0;
            }

            if ((l1 == index) && (l2 > index)) {
                return -1;
            }

            if ((l1 > index) && (l2 == index)) {
                return 1;
            }

            return 0;
        }
    }


    /**
     * 卷册年类
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    private class VolumeOrYear implements Comparable {

        /**
         * Field description
         */
        String vyStr = "";

        /**
         * Field description
         */
        int len = 0;

        /**
         * Constructs ...
         *
         * @param authorNo Param description
         */
        VolumeOrYear(String authorNo) {
            vyStr = authorNo;
            len   = authorNo.length();
        }

        /**
         * 按照卷册年规则来比较字符大小
         *
         * @param c1 字符1
         * @param c2 字符2
         * @return c1 &lt; c2 -1 <br> c1 == c2 0 <br> c1 &gt; c2 1
         */
        private int compareChar(char c1, char c2) {
            String str = "-.,?0123456789abcdefghijklmnopqrstuvwxyz.";

            return str.indexOf(c1) - str.indexOf(c2);
        }

        @Override
        public int compareTo(Object o) {
            VolumeOrYear vy1 = (VolumeOrYear) o;
            String       s1  = vyStr.toLowerCase(),
                         s2  = vy1.vyStr.toLowerCase();

            s1 = s1.replaceAll("[()\\[\\]' ]", "");
            s2 = s2.replaceAll("[()\\[\\]' ]", "");
            s1 = s1.replaceAll("[^-.,?\\d\\w()\\[\\]']", " ");
            s2 = s2.replaceAll("[^-.,?\\d\\w()\\[\\]']", " ");

            int l1 = s1.length(),
                l2 = s2.length();

            if (s1.compareTo(s2) == 0) {
                return 0;
            }

            String strY = "y";

            // noinspection AlibabaUndefineMagicConstant
            if (strY.equals(s1)) {
                return -1;
            }

            // noinspection AlibabaUndefineMagicConstant
            if (strY.equals(s2)) {
                return 1;
            }

            int index1 = 0,
                index2 = 0, tmp;

            while ((l1 > index1) && (l2 > index2)) {
                char c1 = s1.charAt(index1);
                char c2 = s2.charAt(index2);

                if (Character.isDigit(c1) && Character.isDigit(c2)) {

                    // 数值按大小排序
                    String tmpStr1 = s1.substring(index1).split("[^0-9]")[0];
                    String tmpStr2 = s2.substring(index1).split("[^0-9]")[0];

                    tmp = Integer.parseInt(tmpStr1) - Integer.parseInt(tmpStr2);

                    if (tmp != 0) {
                        return tmp;
                    }

                    index1 += tmpStr1.length();
                    index2 += tmpStr2.length();
                } else {
                    tmp = compareChar(c1, c2);

                    if (tmp != 0) {
                        return tmp;
                    }

                    index1++;
                    index2++;
                }
            }

            if ((l1 == index1) && (l2 == index2)) {
                return 0;
            }

            if ((l1 == index1) && (l2 > index2)) {
                return -1;
            }

            if ((l1 > index1) && (l2 == index2)) {
                return 1;
            }

            return 0;
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
