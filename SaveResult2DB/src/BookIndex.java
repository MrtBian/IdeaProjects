import java.util.Arrays;

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
        return Arrays.toString(lines);
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
            String s1 = anStr.toLowerCase(), s2 = anStr.toLowerCase();
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
        BookIndex[] bookIndices = {new BookIndex("TP312C++/D295/1996/Y"),
                new BookIndex("D81/I-61a/1995/Y"),
                new BookIndex("D971.221.6/I-32/1995/Y"),
                new BookIndex("F74/I-61j/1996/Y"),
                new BookIndex("P74-532/P115/1991,19th/Y"),
                new BookIndex("D971.2/A512r(-)/2nd/Judgments/1982-/V.1-/Y"),
                new BookIndex("D910.9-61/I-61//Intergov./1998/V.1/Suppl.3/Y"),
                new BookIndex("S715-091.61/F718/1988/Y"),
                new BookIndex("D0/S558(5)/1997/Y"),
                new BookIndex("B516.41/M191(-)/1997/Y"),
                new BookIndex("D956.1/P123(+)/[1989]/Y"),
                new BookIndex("H319/W192s/1980/Tutor's bk./Y"),
                new BookIndex("H319/B853/1989/teacher's guide/Y"),
                new BookIndex("H334.3/Z-66/*"),
                new BookIndex("O4-44/O-66(2-)/1975"),
                new BookIndex("O4/Y72a(6)/1982/Guide"),
                new BookIndex("TP15-532/P692/1984,15th/V.15, Pt.3"),
                new BookIndex("TP15-532/P692/1981,12th/V.12,Pt.2"),
                new BookIndex("TP15-532/P692/1979,10th/V.10,Pt.3"),
                new BookIndex("TP15-532/P692/1984,15th/V.15, Pt.4"),
                new BookIndex("TP15-532/P692/1981,12th/V.12,Pt.1"),
                new BookIndex("TP15-532/P692/1981,12th/V.12,Pt.4"),
                new BookIndex("TP15-532/P692/1981,12th/V.12,Pt.3"),
                new BookIndex("TP15-532/P692/1980,11th/V.11,Pt.4"),
                new BookIndex("TP15-532/P692/1984,15th/V.15,Pt.2"),
                new BookIndex("TP15-532/P692/1979,10th/V.10,Pt.2"),
                new BookIndex("TP15-532/P692/1984,15th/V.15,Pt.5"),
                new BookIndex("TP15-532/P692/1979,10th/V.10,Pt.1"),
                new BookIndex("TP15-532/P692/1980,11th/V.11,Pt.2"),
                new BookIndex("TP15-532/P692/1979,10th/V.10,Pt.4"),
                new BookIndex("TP15-532/P692/1984,15th/V.15,Pt.1"),
                new BookIndex("O32-53/S559/1983/V.53,Pt.3"),
                new BookIndex("O32-53/S559/1973/V.43,Pt.3-4"),
                new BookIndex("O32-53/S559/1970/V.41,Pt.5"),
                new BookIndex("O32-53/S559/1970/V.41,Pt.2"),
                new BookIndex("O32-53/S559/1970/V.41,Pt.7"),
                new BookIndex("O32-53/S559/1970/V.41,Pt.3"),
                new BookIndex("O32-53/S559/1973/V.43,Pt.1-2"),
                new BookIndex("O32-53/S559/1983/V.53,Pt.2"),
                new BookIndex("O32-53/S559/1982/V.52,Pt.4"),
                new BookIndex("H339/B664/1979/V.1B-Si/Y"),
                new BookIndex("H339/B664/V.1A-D/1970/Y"),
                new BookIndex("H339/B664/1978/V.1A-G/Y"),
                new BookIndex("H339/B664/1977/V.1B-L/Y"),
                new BookIndex("H339/B664/V.2/1971/Y"),
                new BookIndex("TP399:TH126/R725(2)/2002/Y"),
                new BookIndex("Z88:N/C614/2005/Y"),
                new BookIndex("51.621/C262/[1964-]"),
                new BookIndex("TP312C#/X141a"),
                new BookIndex("TP312C++/L785"),
                new BookIndex("TP312C++-33/C1962"),
                new BookIndex("K825.3=74/Z259"),
                new BookIndex("H319.4:I/L147"),
                new BookIndex("B2/Z561a/(1-3)"),
                new BookIndex("A221.1/1894.04a2"),
                new BookIndex("A1/M1/(26-2)"),
                new BookIndex("K827=73/Z6983"),
                new BookIndex("B2/X337/(1-1)"),
                new BookIndex("B2/X337/(1-3-1)"),
                new BookIndex("B2/X337/(1-2)"),
                new BookIndex("B516.47/Z666a2"),
                new BookIndex("K204.2/S641zx"),
                new BookIndex("K204.2/S641zx2"),
                new BookIndex("K827=6/H488d/(14)5"),
                new BookIndex("D923.04/Z571a/(1~2)"),
                new BookIndex("D923.05-55/M387a/(2009-1&2)"),
                new BookIndex("K825.6/D342/Y.ZH."),
                new BookIndex("K254.410.7/J333b"),
                new BookIndex("D091.6-52/O-1242/(1)"),
                new BookIndex("K250.6/C325/(8)-(10)"),
                new BookIndex("I216.1/Z325/(1911-1927)/(6) "),
                new BookIndex("I216.1/Z325/(1911-1927)/(8) "),
                new BookIndex("I216.1/Z325/(1911-1927)/(3) "),
                new BookIndex("I216.1/Z325/(1911-1927)/(1) "),
                new BookIndex("I216.1/Z325/(1911-1927)/(4) "),
                new BookIndex("I216.1/Z325/(1911-1927)/(5) "),
                new BookIndex("I216.1/Z325/(1911-1927)/(9) "),
                new BookIndex("I216.1/Z325/(1911-1927)/(7) "),
                new BookIndex("I216.1/Z325/(1911-1927)/(2) "),
                new BookIndex("R/91.8563/P214/1976-/base vol.1977-//Y"),
                new BookIndex("SW/E712.53-09/R311/1996/Ser.8//R.25/Y"),
                new BookIndex("R/54.5073/B422(4e)/1999/Suppl.5/V.27/F:Index/C1-C204")};

        Arrays.sort(bookIndices);
        for (BookIndex b : bookIndices) {

            System.out.println(b.toString());
        }
    }
}
