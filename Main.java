import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * 实现了Discuz论坛自动回帖功能
 */
public class Main {
    //帖子的URL 具体id可以作为入参
    private static final String baseRefer = "http://coursebbs.open.com.cn/forum.php?mod=post&action=reply&fid=519&tid=";
    //通过开发者工具获得的本地cookie
    private static final String yourCookeie ="b_t_s_100200=5f4c7430-b58d-4956-9ff8-1a94f16633da; up_first_date=2019-12-02; up_beacon_user_id_100200=xuzhe431802; up_beacon_uni_id_100200=10055; Oe7Z_2132_saltkey=bp6POODu; Oe7Z_2132_lastvisit=1575252793; Oe7Z_2132_ulastactivity=5d11tM4IyDJA1KU7gN5UIJnwQqWPpFgOQPwwsroXpdIhU%2BzDBSAH; Oe7Z_2132_nofavfid=1; Hm_lvt_7a9892f9dfe3da76cedd12e5d2304c83=1576460819,1576461052,1576548212,1576553980; Oe7Z_2132_forum_lastvisit=D_497_1575426430D_50_1576460963D_2_1576467926D_52_1576548808D_48_1576552063D_519_1576557105; Oe7Z_2132_smile=1D1; Oe7Z_2132_editormode_e=1; Oe7Z_2132_sid=Qe0gkc; Oe7Z_2132_lastact=1576557219%09forum.php%09ajax; Hm_lpvt_946766664d58c814a94301842a7a73fb=1575021759; Oe7Z_2132_auth=2f7fIoiLuzl4ep3ouu3qBgCFvDf8Xh0TdtM8rQy7zCf8LXcanEcb6Ia%2BsX4TozRKHLgfsBwB3mo%2FmPAQ5hd2%2FU6m%2F8w; Oe7Z_2132_st_p=302059%7C1576557040%7Cc3dcaae1ad246527a5e623a0276565c9; Oe7Z_2132_viewid=tid_149981; Oe7Z_2132_lip=60.191.0.180%2C1576555535; up_page_stime_test=1575021591053; up_beacon_vist_count_test=2; up_beacon_id_test=c8afd9f7-6d32-47b1-a62b-e730e411b863-1571114925648; up_beacon_id_100200=c32f38c4-de2d-4749-bc30-812e17389dd3-1571190952042; Oe7Z_2132_ignore_notice=1; Oe7Z_2132_space_top_credit_302059_all=457; Oe7Z_2132_space_top_friendnum_302059=310; up_page_stime_100200=1576548200148; up_beacon_vist_count_100200=90; Hm_lpvt_7a9892f9dfe3da76cedd12e5d2304c83=1576557106; Oe7Z_2132_st_t=302059%7C1576557105%7C2df246a21883a9a02a5fc574f012c6a8; up_page_stime_100201=1574389608590; up_beacon_vist_count_100201=1; up_beacon_id_100201=646e4777-6b3f-4723-9f43-638d20b1a741-1574389608599; Oe7Z_2132_space_top_credit_302059_2=190; Oe7Z_2132_home_diymode=1; Oe7Z_2132_sendmail=1";



    public static void main(String[] args) {
        int startId = 150114; // 开始的帖子id
        for (int i = 0; i < 1000; i++) {
            postMessage(startId);
            startId++;
        }
    }

    /*
    检查所访问帖子是否存在
    */
    public static boolean isExist(int id) {
        String tmpPath = baseRefer + id+"&fromvf=1&extra=page=1&replysubmit=yes&infloat=yes&handlekey=vfastpost&inajax=1";
        URL url;
        try {
            url = new URL(tmpPath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("Content-Type", "text/html; charset=UTF-8");
            con.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0");
            con.addRequestProperty("Referer", "Referer: http://coursebbs.open.com.cn/forum.php?mod=viewthread&tid="+id+"&extra=page%3D1");
            con.setRequestMethod("GET");
            if (con.getResponseCode() == 200) {
                InputStream inputStr = con.getInputStream();
                String info = new String(StreamTool.read(inputStr), "UTF-8");
                if (info.contains("抱歉，指定的主题不存在或已被删除或正在被审核")) {
                    System.out.println("id=" + id + "帖子存在或已被删除！");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 回帖
     * @param id 帖子id
     */
    public static void postMessage(int id) {
        if (!isExist(id)) {
            return;
        }
        String tmpPath = baseRefer + id;
        StringBuilder path = new StringBuilder(tmpPath);
        Map<String, String> mapData = new LinkedHashMap<>();
        mapData.put("mod", "post");
        mapData.put("action", "reply");
        mapData.put("replysubmit", "yes");
        mapData.put("message", "学习快乐");
        mapData.put("formhash", "c4ad5bc9");
        try {
            for (Map.Entry<String, String> mapEnt : mapData.entrySet()) {
                path.append("&");
                path.append(mapEnt.getKey() + "=");
                path.append(URLEncoder.encode(mapEnt.getValue(), "UTF-8"));
            }
            URL url = new URL(path.toString());
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length",
                    String.valueOf(path.length()));
            con.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            con.setRequestProperty("Cookie", yourCookeie);
            con.setDoOutput(true);
            OutputStream outStr = con.getOutputStream();
            outStr.write(path.toString().getBytes());
            if (con.getResponseCode() == 200) {
                System.out.println("在id=" + id + "成功发帖！");
                try {
                    //休眠一分钟，避免封号
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class StreamTool {
    public static byte[] read(InputStream inputStr) throws Exception {
        ByteArrayOutputStream outStr = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStr.read(buffer)) != -1) {
            outStr.write(buffer, 0, len);
        }
        inputStr.close();
        return outStr.toByteArray();
    }

}
