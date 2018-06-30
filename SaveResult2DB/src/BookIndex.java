import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BookIndex implements Comparable {
    private ClassNo classNo;
    private String authorNo = "";
    private String volumeNo = "";
    private String lines[];
    private int lineNum;

    /**
     * 标记符号
     */
    private enum MarkSymbol {
        COLON(':'), HYPHEN('-'), EQUALITYSIGN('='), EMPTY('e');

        private char symbol;
        private int index;

        public char getSymbol() {
            return symbol;
        }

        public int getIndex() {
            return index;
        }

        MarkSymbol(char symbol) {
            this.symbol = symbol;
            switch (symbol) {
                case ':':
                    this.index = 0;
                case '-':
                    this.index = 1;
                case '=':
                    this.index = 2;
                case 'e':
                    this.index = -1;
            }
        }
    }

    /**
     * @param bookindex
     */
    BookIndex(String bookindex) {
        lines = bookindex.split("/");
        lineNum = lines.length;
        classNo = new ClassNo(lines[0]);
        if (lineNum >= 2) {
            if (lines[1].matches("([0-9-])")) {
                //卷次号
                volumeNo = lines[1];
                authorNo = "";
            }
            authorNo = lines[1];
            if (lineNum >= 3) {
                volumeNo = lines[2];
            }
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
        if (tmp != 0)
            return tmp;
        tmp = authorNo.compareTo(b2.authorNo);
        if (tmp != 0)
            return tmp;
        tmp = volumeNo.compareTo(b2.volumeNo);
        if (tmp != 0)
            return tmp;
        int count = 3;
        while (lineNum >= count && b2.lineNum >= count) {
            if (lineNum == b2.lineNum && lineNum == count)
                return 0;
            if (lineNum == count && b2.lineNum > lineNum)
                return -1;
            if (b2.lineNum == count && b2.lineNum < lineNum)
                return -1;
            tmp = lines[count].compareTo(b2.lines[count]);
            if (tmp != 0)
                return tmp;
            count++;
        }

        return 0;
    }

    private class ClassNo implements Comparable {

        private String preClassNo = "";
        private MarkSymbol markSymbol = MarkSymbol.EMPTY;
        private String laClassNo = "";

        ClassNo(String classNo) {
            Pattern p = Pattern.compile("[:-=]");
            Matcher m = p.matcher(classNo);
            if (m.find()) {
                switch (m.group().charAt(0)) {
                    case ':':
                        markSymbol = MarkSymbol.COLON;
                    case '-':
                        markSymbol = MarkSymbol.HYPHEN;
                    case '=':
                        markSymbol = MarkSymbol.EQUALITYSIGN;
                }
                preClassNo = classNo.substring(0, m.start());
                laClassNo = classNo.substring(m.end());
            } else
                preClassNo = classNo;
        }

        @Override
        public int compareTo(Object o) {
            ClassNo c2 = (ClassNo) o;
            int tmp = preClassNo.compareTo(c2.preClassNo);
            if (tmp != 0) {
                return tmp;
            }
            tmp = markSymbol.getIndex() - c2.markSymbol.getIndex();
            if (tmp != 0) {
                return tmp;
            }
            tmp = laClassNo.compareTo(c2.laClassNo);
            return tmp;
        }
    }

    public static void main(String[] args) {
        BookIndex[] bookIndices = {new BookIndex("D095-53/G294 "),
                new BookIndex("D095.12/X178 "),
                new BookIndex("D095.12/M132 "),
                new BookIndex("D095.05-53/H335/(1) "),
                new BookIndex("D095.12/S862 "),
                new BookIndex("D095.12/P367/(2) "),
                new BookIndex("D095.12/P367/(1) "),
                new BookIndex("D095.125/L329 "),
                new BookIndex("D095.125/C148 "),
                new BookIndex("D095.125/P365 "),
                new BookIndex("D095.124/M361 "),
                new BookIndex("D095.124/G589 "),
                new BookIndex("D095.125/Y315 "),
                new BookIndex("D095.125/W224 "),
                new BookIndex("D095.125/Z179/(1) "),
                new BookIndex("D095.125/Z179/(2) "),
                new BookIndex("D095.16/Z338 "),
                new BookIndex("D095.16/S166 "),
                new BookIndex("D095.16/S711 "),
                new BookIndex("D095.16/H341 "),
                new BookIndex("D095.16/M166 "),
                new BookIndex("D095.16/G261 "),
                new BookIndex("D095.16/C155 "),
                new BookIndex("D095.16/F297 "),
                new BookIndex("D095/S1982/(1) "),
                new BookIndex("D095/M141 "),
                new BookIndex("D095/M134 "),
                new BookIndex("D095.05/Q127 "),
                new BookIndex("D095/Y236a "),
                new BookIndex("D095/X444 "),
                new BookIndex("D095/Z272 "),
                new BookIndex("D095.05-53/H335/(2) "),
                new BookIndex("D095.04/L322 "),
                new BookIndex("D095.12/L746 "),
                new BookIndex("D095.124/Z272 "),
                new BookIndex("D095.16/S576 "),
                new BookIndex("D095.124/D432 "),
                new BookIndex("D095.16/M1662 "),
                new BookIndex("D095.4/M124 "),
                new BookIndex("D095.16/H341 "),
                new BookIndex("D095.125/Z282 "),
                new BookIndex("D095.165/S482 "),
                new BookIndex("D095.165/S482c "),
                new BookIndex("D095.165/H111c "),
                new BookIndex("D095.165/H111a "),
                new BookIndex("D095.165.34/A127 "),
                new BookIndex("D095.164.3/N249 "),
                new BookIndex("D095/X444d/(4) "),
                new BookIndex("D095/X444c "),
                new BookIndex("D095.05/C354 "),
                new BookIndex("D095-53/C151 "),
                new BookIndex("D095/Y5263 "),
                new BookIndex("D095.165/C155 "),
                new BookIndex("D095.04/B539 "),
                new BookIndex("D095-532/J542/(2003) "),
                new BookIndex("D095.165/S482b "),
                new BookIndex("D095.165/S482a "),
                new BookIndex("D095.165/H111 "),
                new BookIndex("D095.165/H111b "),
                new BookIndex("D095.164.1/H341 "),
                new BookIndex("D095.164.1/K169 "),
                new BookIndex("D095.164.1/H341a "),
                new BookIndex("D095.463/M129b "),
                new BookIndex("D095.463/M129b "),
                new BookIndex("D095.463/M129a "),
                new BookIndex("D095.463/M129 "),
                new BookIndex("D095.463/D167 "),
                new BookIndex("D095.463/D167 "),
                new BookIndex("D095.463/D167 "),
                new BookIndex("D095.463/B537 "),
                new BookIndex("D095/R232 "),
                new BookIndex("D095/M137 "),
                new BookIndex("D095/M3242 "),
                new BookIndex("D095/M132 "),
                new BookIndex("D095/M1342 "),
                new BookIndex("D095/S198 "),
                new BookIndex("D095/W396 "),
                new BookIndex("D095/T175 "),
                new BookIndex("D095/S126 "),
                new BookIndex("D095/Y236b "),
                new BookIndex("D095/X444d/(5) "),
                new BookIndex("D095/Y236 "),
                new BookIndex("D095/X444d/(1) "),
                new BookIndex("D095/X444d/(2) "),
                new BookIndex("D095/X444a "),
                new BookIndex("D095-53/M214 "),
                new BookIndex("D095-53/L849 "),
                new BookIndex("D095/Z658 "),
                new BookIndex("D095/Z269a "),
                new BookIndex("D095/Z212 "),
                new BookIndex("D095/Y526 "),
                new BookIndex("D095.04/W159 "),
                new BookIndex("D095.04/M516 "),
                new BookIndex("D095/M1372 "),
                new BookIndex("D095/P359 "),
                new BookIndex("D095/M289 "),
                new BookIndex("D095/S198 "),
                new BookIndex("D095/W4113 "),
                new BookIndex("D095/W411 "),
                new BookIndex("D095/T123 "),
                new BookIndex("D095/S1982/(2) "),
                new BookIndex("D095.03/B539/(2) "),
                new BookIndex("D095/X485 "),
                new BookIndex("D095/X444d/(3) "),
                new BookIndex("D095.03/B539/(1) "),
                new BookIndex("D095.053/M137 "),
                new BookIndex("D095-53/W455 "),
                new BookIndex("D095.12/P367/(1) "),
                new BookIndex("D095.12/P367/(3) "),
                new BookIndex("D095.12/P367/(2) "),
                new BookIndex("D095.12/P367/(1) "),
                new BookIndex("D095.12/L331 "),
                new BookIndex("D095.053/M1372 "),
                new BookIndex("D095.12-53/Z529 "),
                new BookIndex("D095.12/Z271 "),
                new BookIndex("D095.12/P367/(3) "),
                new BookIndex("A745/B5172 "),
                new BookIndex("A745/M132 "),
                new BookIndex("D668/C387 "),
                new BookIndex("A744/R178 "),
                new BookIndex("A743/S3723 "),
                new BookIndex("D668/C155 "),
                new BookIndex("A751/J5142/(6) "),
                new BookIndex("A751/J5142/(3) "),
                new BookIndex("A751/J5142/(2) "),
                new BookIndex("A751/J514/(2-1) "),
                new BookIndex("A751/H686a/(2) "),
                new BookIndex("D668/B287/(2011) "),
                new BookIndex("A751/T2642 "),
                new BookIndex("A751/M23 "),
                new BookIndex("A751/X2572 "),
                new BookIndex("D668/D176/(29) "),
                new BookIndex("A751/M2 "),
                new BookIndex("D668/D176/(28) "),
                new BookIndex("A751/L7122/(1) "),
                new BookIndex("D668/D176/(27) "),
                new BookIndex("A751/L7122/(2) "),
                new BookIndex("D668/D176/(26) "),
                new BookIndex("D668/D176/(25) "),
                new BookIndex("A751/L712/(1) "),
                new BookIndex("D668/D176/(24) "),
                new BookIndex("D668/D176/(23) "),
                new BookIndex("D668/D176/(22) "),
                new BookIndex("D095.46/M143 "),
                new BookIndex("D091.2/W396a "),
                new BookIndex("D095.45/B128 "),
                new BookIndex("D095.41/M1372 "),
                new BookIndex("A752/C323b/(1) "),
                new BookIndex("A752/C157 "),
                new BookIndex("D668/D176/(20) "),
                new BookIndex("D668/D176/(19) "),
                new BookIndex("D668/D176/(17) "),
                new BookIndex("D668/D176/(16) "),
                new BookIndex("A752/B435a "),
                new BookIndex("A752/B435 "),
                new BookIndex("D668/D176/(11) "),
                new BookIndex("D668/D176/(13) "),
                new BookIndex("D668/D176/(12) "),
                new BookIndex("D668/D176/(9) "),
                new BookIndex("A752/D251g "),
                new BookIndex("D668/D176/(8) "),
                new BookIndex("A752/D251b2 "),
                new BookIndex("D668/D176/(7) "),
                new BookIndex("D668/D176/(6) "),
                new BookIndex("A752/D251f "),
                new BookIndex("A752/C348 "),
                new BookIndex("A752/C354a "),
                new BookIndex("D668/D176/(53) "),
                new BookIndex("A741/D144 "),
                new BookIndex("A735/X485 "),
                new BookIndex("A735/T471 "),
                new BookIndex("A735/G312 "),
                new BookIndex("A741/L372a "),
                new BookIndex("A734/S245 "),
                new BookIndex("A741/S762 "),
                new BookIndex("A741/H151 "),
                new BookIndex("A741/L156 "),
                new BookIndex("A742/M1482/(2) "),
                new BookIndex("A742/M148 "),
                new BookIndex("A742/F346 "),
                new BookIndex("A742/F112 "),
                new BookIndex("A742/A118 "),
                new BookIndex("D668/D176/(30) "),
                new BookIndex("A742/Q324 "),
                new BookIndex("A742/W3963/(1) "),
                new BookIndex("A742/W396/(1) "),
                new BookIndex("A742/N129 ")
        };

        Arrays.sort(bookIndices);
        for (BookIndex b : bookIndices) {

            System.out.println(b.toString());
        }
    }
}
