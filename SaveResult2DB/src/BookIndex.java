import java.util.function.BinaryOperator;

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
        COLON(':'), HYPHEN('-'), EQUALITYSIGN('='),EMPTY('e');

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
    public int compareTo(Object o) {
        BookIndex b2 = (BookIndex) o;
        int tmp = classNo.compareTo(b2.classNo);
        if(tmp!=0)
            return tmp;
        tmp = authorNo.compareTo(b2.authorNo);
        if(tmp!=0)
            return tmp;
        tmp = volumeNo.compareTo(b2.volumeNo);
        if(tmp!=0)
            return tmp;

        return 0;
    }

    private class ClassNo implements Comparable {

        private String preClassNo = "";
        private MarkSymbol markSymbol = MarkSymbol.EMPTY;
        private String laClassNo = "";

        ClassNo(String classNo) {
            int index = classNo.indexOf("[:-=]");
            if (index >= 0) {
                switch (classNo.charAt(index)) {
                    case ':':
                        markSymbol = MarkSymbol.COLON;
                    case '-':
                        markSymbol = MarkSymbol.HYPHEN;
                    case '=':
                        markSymbol = MarkSymbol.EQUALITYSIGN;
                }
                preClassNo = classNo.substring(0, index);
                laClassNo = classNo.substring(index + 1);
            }
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

    }
}
