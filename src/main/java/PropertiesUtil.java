import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
    public static Object getParamFromProp(String key){
        InputStream is=PropertiesUtil.class.getClassLoader().getResourceAsStream("config.properties");
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        Properties props = new Properties();
        try {
            props.load(br);
            return props.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
