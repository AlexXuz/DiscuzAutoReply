
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现了Discuz论坛自动回帖功能
 */
public class Main {
    //帖子的URL 具体id可以作为入参
    private static final String BASE_REFER="http://coursebbs.open.com.cn/forum.php?mod=post&action=reply&fid=519&tid=";
    //登录地址
    private static final String LOGIN_URL="http://coursebbs.open.com.cn";
    //通过开发者工具获得的本地cookie
    private static final String yourCookeie ="b_t_s_100200=5f4c7430-b58d-4956-9ff8-1a94f16633da; up_first_date=2019-12-02; up_beacon_user_id_100200=xuzhe431802; up_beacon_uni_id_100200=10055; Hm_lvt_7a9892f9dfe3da76cedd12e5d2304c83=1576567421,1576569796,1576570808,1576633595; Oe7Z_2132_sid=uTUTfm; Oe7Z_2132_saltkey=O4ee6458; Oe7Z_2132_lastvisit=1576565854; Oe7Z_2132_lastact=1576635629%09misc.php%09patch; Oe7Z_2132_ulastactivity=2700KZetSslgYxEQNBLX7Xkue9RKSb66R4Hl1szwUrBOpSkk3zV2; Oe7Z_2132_lastcheckfeed=302059%7C1576569485; Oe7Z_2132_nofavfid=1; Oe7Z_2132_forum_lastvisit=D_519_1576633819; Oe7Z_2132_smile=1D1; Hm_lpvt_946766664d58c814a94301842a7a73fb=1575021759; up_page_stime_test=1575021591053; up_beacon_vist_count_test=2; up_beacon_id_test=c8afd9f7-6d32-47b1-a62b-e730e411b863-1571114925648; up_beacon_id_100200=c32f38c4-de2d-4749-bc30-812e17389dd3-1571190952042; Hm_lpvt_7a9892f9dfe3da76cedd12e5d2304c83=1576635629; up_page_stime_100201=1574389608590; up_beacon_vist_count_100201=1; up_beacon_id_100201=646e4777-6b3f-4723-9f43-638d20b1a741-1574389608599; up_page_stime_100200=1576633585699; up_beacon_vist_count_100200=92; Oe7Z_2132_st_p=302059%7C1576635628%7C4cad24f99088dd06eb8cccde32922a30; Oe7Z_2132_viewid=tid_150269; Oe7Z_2132_seccode=57.cfb175bb07f6247945; Oe7Z_2132_auth=c5ebeXB73fim7zJqXwui5EW%2Fx4AHP6Rxp3mgs%2BqGOU3sbkfyJAojQZh6lU0JgScARWXDN%2BQOm6pL7PyI5DNg2djb9Vo; Oe7Z_2132_lip=60.191.0.180%2C1576635618; Oe7Z_2132_st_t=302059%7C1576633819%7C1cfe9570adb526b3e6f0b95ba48c20d9; Oe7Z_2132_home_diymode=1; Oe7Z_2132_ignore_notice=1";
    //用户名
    private static final String USER_NAME="xuzhe431802";
    //密码
    private static final String PASS_WORK="5431802";


    public static void main(String[] args) {
        HttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
        //自动登录刷帖（功能暂有bug待修复）
        start(httpClient);
        //手动传cookie刷帖
//        int startId = 150271; // 开始的帖子id
//        for (int i = 0; i < 1000; i++) {
//            postMessage(startId);
//            startId++;
//        }
    }

    /**
     * 开始刷帖
     * @param client
     * @return
     */
    public static boolean start(HttpClient client){
        HttpClient httpClient = client;
        HttpPost httpPost=new HttpPost(LOGIN_URL+"/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1");
        List params=new ArrayList<>();
        params.add(new BasicNameValuePair("username",USER_NAME));
        params.add(new BasicNameValuePair("password",PASS_WORK));
        String ans;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity( params, "UTF-8"));
            HttpResponse response=httpClient.execute(httpPost);
            HttpEntity entity=response.getEntity();
            ans=EntityUtils.toString(entity);
            if((ans.lastIndexOf(LOGIN_URL+"/./")) !=-1){
                //获取用户的一些信息
                HttpGet getD=new HttpGet(LOGIN_URL+"/");
                response = httpClient.execute(getD);
                entity=response.getEntity();
                ans=EntityUtils.toString(entity,"GBK");
                int startId = 150271; // 开始的帖子id
                for (int i = 0; i < 1000; i++) {
                    if (!isExist(startId)) {
                        startId++;
                    }else{
                        List params1=new ArrayList<>();
                        params1.add(new BasicNameValuePair("mod","post"));
                        params1.add(new BasicNameValuePair("action","reply"));
                        params1.add(new BasicNameValuePair("replysubmit","yes"));
                        params1.add(new BasicNameValuePair("message","学习快乐"));
                        params1.add(new BasicNameValuePair("formhash",ans.substring(ans.lastIndexOf("formhash=")+9,ans.lastIndexOf("formhash=")+17)));
                        HttpPost httpPost1=new HttpPost(BASE_REFER+startId);
                        httpPost1.setEntity(new UrlEncodedFormEntity(params1,"UTF-8"));
                        httpClient.execute(httpPost1);
                        System.out.println("id="+startId+"回复成功！60秒后回复下一贴");
                        startId++;
//                        Thread.sleep(60 * 1000);
                    }
                }
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 检查所访问帖子是否存在
     * @param id
     * @return
     */
    public static boolean isExist(int id) {
        String tmpPath = BASE_REFER + id+"&fromvf=1&extra=page=1&replysubmit=yes&infloat=yes&handlekey=vfastpost&inajax=1";
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
        String tmpPath = BASE_REFER + id;
        StringBuilder path = new StringBuilder(tmpPath);
        Map<String, String> mapData = new LinkedHashMap<>();
        mapData.put("mod", "post");
        mapData.put("action", "reply");
        mapData.put("replysubmit", "yes");
        mapData.put("message", "学习快乐");
        mapData.put("formhash", "d9afd44d");
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

