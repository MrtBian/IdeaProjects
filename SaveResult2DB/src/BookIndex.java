import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import utils.FileUtil.*;

import static utils.FileUtil.readFileByLine;
import static utils.FileUtil.writeFile;

class BookIndex implements Comparable {
    private ClassNo classNo;
    private AuthorNo authorNo;
    private String lines[];
    private int lineNum;

    /**
     * @param bookindex
     */
    BookIndex(String bookindex) {
        lines = bookindex.split("/");
        lineNum = lines.length;
        classNo = new ClassNo(lines[0]);
        if (lineNum >= 2) {
            authorNo = new AuthorNo(lines[1]);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        boolean isFirst = true;
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

    @Override
    public int compareTo(Object o) {
        BookIndex b2 = (BookIndex) o;
        int tmp = classNo.compareTo(b2.classNo);
        if (tmp != 0) return tmp;
        tmp = authorNo.compareTo(b2.authorNo);
        if (tmp != 0) return tmp;
        int count = 2;
        while (lineNum >= count && b2.lineNum >= count) {
            if (lineNum == b2.lineNum && lineNum == count) return 0;
            if (lineNum == count && b2.lineNum > lineNum) return -1;
            if (b2.lineNum == count && b2.lineNum < lineNum) return -1;
            VolumeOrYear vy1 = new VolumeOrYear(lines[count]);
            VolumeOrYear vy2 = new VolumeOrYear(b2.lines[count]);
            tmp = vy1.compareTo(vy2);
            if (tmp != 0) return tmp;
            count++;
        }

        return 0;
    }

    /**
     * 分类号类
     */
    private class ClassNo implements Comparable {
        String cnStr = "";
        int len = 0;

        ClassNo(String classNo) {
            cnStr = classNo;
            len = classNo.length();
        }

        @Override
        public int compareTo(Object o) {
            ClassNo classNo1 = (ClassNo) o;
            String s1 = cnStr.toLowerCase(), s2 = classNo1.cnStr.toLowerCase();
            s1 = s1.replaceAll("[^-()\"\"=#+:\\w\\d]", "");
            s2 = s2.replaceAll("[^-()\"\"=#+:\\w\\d]", "");
            int l1 = s1.length(), l2 = s2.length();
            if (s1.compareTo(s2) == 0) {
                return 0;
            }
            int index = 0, tmp;
            while (l1 > index && l2 > index) {
                char c1 = s1.charAt(index);
                char c2 = s2.charAt(index);
                tmp = compareChar(c1, c2);
                if (tmp != 0) return tmp;
                index++;
            }
            if (l1 == index && l2 == index) return 0;
            if (l1 == index && l2 > index) return -1;
            if (l1 > index && l2 == index) return 1;
            return 0;
        }

        /**
         * 按照分类号规则来比较字符大小
         *
         * @param c1 字符1
         * @param c2 字符2
         * @return c1 < c2 -1 c1 == c2 0 c1 > c2 1
         */
        private int compareChar(char c1, char c2) {
            String str = "-()\"\"=#+:0123456789abcdefghijklmnopqrstuvwxyz.";
            return str.indexOf(c1) - str.indexOf(c2);
        }
    }

    /**
     * 著者号类
     */
    private class AuthorNo implements Comparable {
        String anStr = "";
        int len = 0;

        AuthorNo(String authorNo) {
            anStr = authorNo;
            len = authorNo.length();
        }

        @Override
        public int compareTo(Object o) {
            AuthorNo authorNo1 = (AuthorNo) o;
            String s1 = anStr.toLowerCase(), s2 = authorNo1.anStr.toLowerCase();
            s1 = s1.replaceAll("[^()-+=\\w\\d]", "");
            s2 = s2.replaceAll("[^()-+=\\w\\d]", "");
            int l1 = s1.length(), l2 = s2.length();
            if (s1.compareTo(s2) == 0) {
                return 0;
            }
            int index1 = 0, index2 = 0, tmp;
            boolean isIn = false;//是否在括号内
            while (l1 > index1 && l2 > index2) {
                char c1 = s1.charAt(index1);
                char c2 = s2.charAt(index2);
                if (isIn && Character.isDigit(c1) && Character.isDigit(c2)) {
                    //括号内数值按大小排序
                    String tmpStr1 = s1.substring(index1).split("[^0-9]")[0];
                    String tmpStr2 = s2.substring(index1).split("[^0-9]")[0];
                    tmp = Integer.parseInt(tmpStr1) - Integer.parseInt(tmpStr2);
                    if (tmp != 0) return tmp;
                    index1 += tmpStr1.length();
                    index2 += tmpStr2.length();
                } else {
                    if (!isIn) {
                        //忽略括号外的'-'
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
                    if (tmp != 0) return tmp;
                    if (c1 == '(') isIn = true;
                    index1++;
                    index2++;
                }
            }
            if (l1 == index1 && l2 == index2) return 0;
            if (l1 == index1 && l2 > index2) return -1;
            if (l1 > index1 && l2 == index2) return 1;
            return 0;
        }

        /**
         * 按照著者号规则来比较字符大小
         *
         * @param c1 字符1
         * @param c2 字符2
         * @return c1 < c2 -1 c1 == c2 0 c1 > c2 1
         */
        private int compareChar(char c1, char c2) {
            String str = "()-+=0123456789abcdefghijklmnopqrstuvwxyz.";
            return str.indexOf(c1) - str.indexOf(c2);
        }
    }

    /**
     * 卷册年类
     */
    private class VolumeOrYear implements Comparable {
        String vyStr = "";
        int len = 0;

        VolumeOrYear(String authorNo) {
            vyStr = authorNo;
            len = authorNo.length();
        }

        @Override
        public int compareTo(Object o) {
            VolumeOrYear vy1 = (VolumeOrYear) o;
            String s1 = vyStr.toLowerCase(), s2 = vy1.vyStr.toLowerCase();
            s1 = s1.replaceAll("[()\\[\\]' ]", "");
            s2 = s2.replaceAll("[()\\[\\]' ]", "");
            s1 = s1.replaceAll("[^-.,?\\d\\w()\\[\\]']", " ");
            s2 = s2.replaceAll("[^-.,?\\d\\w()\\[\\]']", " ");
            int l1 = s1.length(), l2 = s2.length();
            if (s1.compareTo(s2) == 0) {
                return 0;
            }
            if(s1.equals("y")){
                return -1;
            }

            if(s2.equals("y")){
                return 1;
            }
            int index1 = 0, index2 = 0, tmp;
            while (l1 > index1 && l2 > index2) {
                char c1 = s1.charAt(index1);
                char c2 = s2.charAt(index2);
                if (Character.isDigit(c1) && Character.isDigit(c2)) {
                    //数值按大小排序
                    String tmpStr1 = s1.substring(index1).split("[^0-9]")[0];
                    String tmpStr2 = s2.substring(index1).split("[^0-9]")[0];
                    tmp = Integer.parseInt(tmpStr1) - Integer.parseInt(tmpStr2);
                    if (tmp != 0) return tmp;
                    index1 += tmpStr1.length();
                    index2 += tmpStr2.length();
                } else {
                    tmp = compareChar(c1, c2);
                    if (tmp != 0) return tmp;
                    index1++;
                    index2++;
                }
            }
            if (l1 == index1 && l2 == index2) return 0;
            if (l1 == index1 && l2 > index2) return -1;
            if (l1 > index1 && l2 == index2) return 1;
            return 0;
        }

        /**
         * 按照卷册年规则来比较字符大小
         *
         * @param c1 字符1
         * @param c2 字符2
         * @return c1 < c2 -1 c1 == c2 0 c1 > c2 1
         */
        private int compareChar(char c1, char c2) {
            String str = "-.,?0123456789abcdefghijklmnopqrstuvwxyz.";
            return str.indexOf(c1) - str.indexOf(c2);
        }
    }

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
}
