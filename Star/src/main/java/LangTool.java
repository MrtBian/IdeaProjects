import com.hankcs.hanlp.HanLP;
import java.util.*;
import  java.util.regex.*;



class OneWeiBo{
    private String dateTime;
    private String text;
    OneWeiBo(String dt, String txt){this.dateTime = dt; this.text = txt;}
    public String getTime(){return this.dateTime;}
    public String getText(){return this.text;}
}

public class LangTool {

    public static String getChinese(String words){
        return words.replaceAll("[^(\\u4e00-\\u9fa5) | ^\\，| ^\\。 | ^\\！| ^\\？| ^\\；" +
                "| ^\\“ | ^\\：| ^\\（| ^\\）| ^\\,| ^\\.| ^\\! | ^\\?| ^\\:| ^\\;| ^\\(| ^\\)]", "");
    }

    public static String transfer2Simple(String words){
        String text = words.replaceAll("\\[|\\]", " ");
        return  HanLP.convertToSimplifiedChinese(text);
    }

    public static  String getChiEng(String words){
        return words.replaceAll("[^a-zA-Z\u4e00-\u9fa5.，,。？“”;；！! ]+","");
    }

    public static  String filterSpecialNotation(String words){
        return words.replaceAll("[@#$%&^* _+/-=~<>]+"," ");
    }

    public static Boolean isForward(String text) {

        boolean b = false;

        Pattern p = Pattern.compile("\\s*转发了");
        Matcher m = p.matcher(text);

        if(m.find()){
            b =  m.start() == 0;
        }
        return b;
    }

    public static String filterMailWeb(String text){

        if(text.equals("")){
            return "";
        }

        String reg = "((https?://|ftp://|www\\.|[^\\s:=]+@www\\.).*?[a-z_\\/0-9\\-\\#=&])(?=(\\.|,|;|\\?|\\!)?(\"|'|«|»|\\[|\\s|\\r|\\n|$))";
        String reg1 = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+" +
                "[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/" +
                "(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})";

        String st = text.replaceAll(reg, "");
        return st;
    }

    public static String filterEmoji(String text){
        if(text.equals("")){
            return "";
        }

        Pattern p = Pattern.compile("[\\ud83c\\udc00-\\ud83f\\udfff]|[\\u2600-\\u27ff]",
                Pattern.UNICODE_CASE | Pattern . CASE_INSENSITIVE );

        Matcher m = p.matcher(text);
        return m.replaceAll("");
    }


    public static String getDateAndTime(String text){

        String dateTime = "";
        if (text.equals("")){
            return dateTime;
        }

        try {
            Pattern p = Pattern.compile("(\\d{1,4}[-|\\/]\\d{1,2}[-|\\/]\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2} | \\d{1,2}[月]\\d{1,2}[日] \\d{1,2}:\\d{1,2})", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
            Matcher matcher = p.matcher(text);
            if (matcher.find() ){
                dateTime = matcher.group();
                if(!dateTime.contains("日")){
                    dateTime = dateTime.substring(0, dateTime.length()-1);
                }
            }

        } catch (Exception e) {
            return dateTime;
        }

        if(dateTime.contains("日") ){
            String year = "" + Calendar.getInstance().get(Calendar.YEAR);
            String [] list = dateTime.split("日");
            String [] list2 = list[0].split("月");
            dateTime = year + list2[0] + "-"+ list2[1] + list[1] +":00";
        }

        return dateTime.replaceAll(" ", "-");
    }

    public static String getOneWeiBoContent(String text) {

        String weiBo = "";
        int start = 0;
        int end = -1;

        if (isForward(text)){
            String regex1 = "转发理由:";
            Pattern pt =  Pattern.compile(regex1);
            Matcher mt = pt.matcher(text);
            if (mt.find()){
                start = mt.end();
            }
        }

        String reg = "(\\[组图共\\d+张\\])?\\s*(原图)?\\s*(已赞|赞)\\[\\d+\\]\\s*转发\\[\\d+\\]\\s*评论\\[\\d+\\]\\s*收藏";
        Pattern p =  Pattern.compile(reg);
        Matcher m = p.matcher(text);

        if(m.find()){
            end = m.start();
        }

        if(end != -1){
            weiBo = text.substring(start, end);
        }

        return weiBo;
    }

    public static OneWeiBo run(String text){

        String dateTime = getDateAndTime(text);
        String weiBo = getOneWeiBoContent(text);
        weiBo = filterEmoji(weiBo);
        weiBo = filterMailWeb(weiBo);
        weiBo = filterSpecialNotation(weiBo);
        weiBo = transfer2Simple(weiBo);

        return new OneWeiBo(dateTime, weiBo);
    }
}
