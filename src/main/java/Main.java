
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
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

    public static void main(String[] args) {
        CloseableHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
        if("auto".equals(PropertiesUtil.getParamFromProp("pattern"))){
            //自动登录刷帖
            start(httpClient);
        }else if ("manual".equals(PropertiesUtil.getParamFromProp("pattern"))){
            //手动传cookie刷帖
            int startId=Integer.parseInt((String) PropertiesUtil.getParamFromProp("startId"));
            for (int i = 0; i < (int)PropertiesUtil.getParamFromProp("number"); i++) {
                postMessage(startId);
                startId++;
            }
        }
    }

    /**
     * 开始刷帖
     * @param client
     * @return
     */
    public static boolean start(CloseableHttpClient client){
        CloseableHttpClient httpClient = client;
        HttpPost httpPost=new HttpPost(PropertiesUtil.getParamFromProp("loginUrl")+"/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1");
        List params=new ArrayList<>();
        params.add(new BasicNameValuePair("username",(String)PropertiesUtil.getParamFromProp("username")));
        params.add(new BasicNameValuePair("password",(String)PropertiesUtil.getParamFromProp("password")));
        String ans;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity( params, "UTF-8"));
            CloseableHttpResponse response=httpClient.execute(httpPost);
            HttpEntity entity=response.getEntity();
            ans=EntityUtils.toString(entity);
            if((ans.lastIndexOf(PropertiesUtil.getParamFromProp("loginUrl")+"/./")) !=-1){
                //获取用户的一些信息
                HttpGet getD=new HttpGet(PropertiesUtil.getParamFromProp("loginUrl")+"/");
                CloseableHttpResponse response2 = httpClient.execute(getD);
                entity=response2.getEntity();
                ans=EntityUtils.toString(entity,"GBK");
                int startId=Integer.parseInt((String) PropertiesUtil.getParamFromProp("startId"));
                for (int i = 0; i < (int)PropertiesUtil.getParamFromProp("number"); i++) {
                    if (!isExist(startId)) {
                        startId++;
                    }else{
                        List params1=new ArrayList<>();
                        params1.add(new BasicNameValuePair("mod","post"));
                        params1.add(new BasicNameValuePair("action","reply"));
                        params1.add(new BasicNameValuePair("replysubmit","yes"));
                        params1.add(new BasicNameValuePair("message", (String)PropertiesUtil.getParamFromProp("message")));
                        params1.add(new BasicNameValuePair("formhash",ans.substring(ans.lastIndexOf("formhash=")+9,ans.lastIndexOf("formhash=")+17)));
                        HttpPost httpPost1=new HttpPost((String)PropertiesUtil.getParamFromProp("baseUrl")+startId);
                        httpPost1.setEntity(new UrlEncodedFormEntity(params1,"UTF-8"));
                        CloseableHttpResponse response1 = httpClient.execute(httpPost1);
                        response1.getEntity().getContent().close();
                        System.out.println("id="+startId+"回复成功！60秒后回复下一贴");
                        startId++;
                        Thread.sleep(60 * 1000);
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
        String tmpPath = (String)PropertiesUtil.getParamFromProp("baseUrl") + id+"&fromvf=1&extra=page=1&replysubmit=yes&infloat=yes&handlekey=vfastpost&inajax=1";
        URL url;
        try {
            url = new URL(tmpPath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("Content-Type", "text/html; charset=UTF-8");
            con.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0");
            con.addRequestProperty("Referer", "Referer: "+PropertiesUtil.getParamFromProp("loginUrl")+"/forum.php?mod=viewthread&tid="+id+"&extra=page%3D1");
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
        String tmpPath = (String)PropertiesUtil.getParamFromProp("baseUrl") + id;
        StringBuilder path = new StringBuilder(tmpPath);
        Map<String, String> mapData = new LinkedHashMap<>();
        mapData.put("mod", "post");
        mapData.put("action", "reply");
        mapData.put("replysubmit", "yes");
        mapData.put("message", (String)PropertiesUtil.getParamFromProp("message"));
        mapData.put("formhash", (String)PropertiesUtil.getParamFromProp("formhash"));
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
            con.setRequestProperty("Cookie", (String)PropertiesUtil.getParamFromProp("yourCookie"));
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

