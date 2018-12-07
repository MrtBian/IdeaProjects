package common;

public class BookInfo implements Comparable{


    public String tagId;                        // 图书中RFID标签的epc
    public String bookIndex;                    // 图书索书号
    public String bookId;                       //条形码
    public String bookName;                     //书名

    public BookInfo(String tagId, String bookIndex) {
        this.tagId = tagId;
        this.bookIndex = bookIndex;
    }

    public BookInfo(String tagId, String bookId, String bookIndex, String bookName) {
        this.tagId = tagId;
        this.bookIndex = bookIndex;
        this.bookName = bookName;
        this.bookId = bookId;
    }

    public BookInfo(String ...bookInfo){
        this.tagId = bookInfo[0];
        this.bookIndex = bookInfo[1];
        this.bookName = bookInfo[2];
        this.bookId = bookInfo[3];
    }

    @Override
    public int compareTo(Object o) {
        BookInfo b2 = (BookInfo)o;
        if(this.bookIndex == null){
            return -1;
        }
        else if(b2.bookIndex==null){
            return 1;
        }
        return this.bookIndex.compareTo(b2.bookIndex);
    }

    @Override
    public String toString() {
        return tagId + " " + bookId+" "+bookIndex+" "+bookName;
    }
}
