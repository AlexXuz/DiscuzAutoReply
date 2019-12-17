
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/**
 * 实现了Discuz论坛自动回帖功能
 */
public class Main {
    //帖子的URL 具体id可以作为入参
    private static final String BASE_REFER="http://coursebbs.open.com.cn/forum.php?mod=post&action=reply&fid=519&tid=";
    //登录地址
    private static final String LOGIN_URL="http://coursebbs.open.com.cn";
    //用户名
    private static final String USER_NAME="xuzhe431802";
    //密码
    private static final String PASS_WORK="5431802";


    public static void main(String[] args) {
        HttpClient httpClient = new DefaultHttpClient();
        start(httpClient);
    }

    /* 客户端登录的方法
	 * @param client
     * @param url 论坛地址
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
                int startId = 150258; // 开始的帖子id
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
                        startId++;
                        System.out.println("id="+startId+"回复成功！60秒后回复下一贴");
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
}

