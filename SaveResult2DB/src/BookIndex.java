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
            if (lines[1].matches("([0-9])")) {
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
        return "BookIndex{" +
                "lines=" + Arrays.toString(lines) +
                '}';
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
        BookIndex[] bookIndices = {new BookIndex("K825.6:442/L328"), new BookIndex("K825.6=442/L328"), new BookIndex("K825.6-442/L328"), new BookIndex("K825.6/L328"), new BookIndex("K825.6=442/L328/(22)")};
        Arrays.sort(bookIndices);
        for (BookIndex b : bookIndices) {

            System.out.println(b.toString());
        }
    }
}
