
public class Test1 {

    public static void main(String [] args){
        String ss = "转发了赞[45923] \uD83C\uDFFB 原文转发[182663] 原文评论\uD83E\uDD4A\uD83E\uDD4A\uD83E\uDD4A[7098] 转发理由:稳准\uD83D\uDEB4\uD83C\uDFFB狠，大力扣杀，接招！\ud83c\uDFD0\uD83C\uDFD0\uD83C\udfD0 赞[722027] 转发[173467] 评论[97224] 收藏 2017-08-21";
        String sss = "[\\\\uD83C\\\\uDC00-\\\\uD83C\\\\uDFFF] | \" +\n" +
                "                \"[\\\\uD83D\\\\uDC00-\\\\uD83D\\\\uDFFF] | [\\\\u2600-\\\\u27FF]";
        String sa = "原文http://t.cn/RSMKvaq 转发[182663] 原www.google.com 文评http://orzfly.com/";


        String str= "转发了 黄子韬的weibo: 转发理由: 煤油1+1=3, hello world !! 正規表現 ありがとう 정규식 彰化地檢$$" +
                "署檢察官莊珂惠當庭痛批法官「腦袋不%%清楚」，讓法官相當氣憤，" +
                "今www.google.com 天@李老\uD83E\uDD4A\uD83E\uDD4A\uD83E\uDD4A师星" +
                "http://t.cn/RSMKvaq 期五，哈http://orzfly.com/ 哈 #奔\uD83C\uDFFB跑吧# [坏笑][坏笑][坏笑] [组图共3张] " +
                "原图  赞[1925515] 转发[772761] 评论[335659] 收藏 01月25日 20:44 来自iPhone 7 Plus";

        String str1= "转发了 浙江卫视燃情贺岁夜 的微博:#浙江卫视领跑2018# #鹿晗愿望季# " +
                "#鹿晗# @M鹿M 送来了超级棒的2018新年祝福你们收到了吗～祝大家2018都能领跑自己！" +
                "[太阳]浙江卫视领跑2018演唱会的微博视频 赞[14808] 原文转发[94922] 原文评论[4952] 转发理由:大家玩儿得开心！" +
                "想你们[嘻嘻][嘻嘻][嘻嘻]" +
                " 赞[431233] 转发[93384] 评论[60892] " +
                "收藏 2017-12-30 22:41:16 来自vivo X20全面屏手机";

//        System.out.println(LangTool.getOneWeiBoContent(ss));
//        System.out.println(LangTool.filterEmoji(LangTool.getOneWeiBoContent(ss)));
//        System.out.println(LangTool.filterMailWeb(sa));
        String q = "";
        System.out.println(LangTool.run(str1).getTime());
        System.out.println(q.replaceAll(" ", "3"));

    }
}
