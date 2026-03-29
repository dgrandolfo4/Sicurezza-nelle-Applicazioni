package uniba.sna.utils;

import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
    
    public static String getConfigProperty(String key) {
        return getProperty(key, "config.ini");
    }
    
    public static String getQueryProperty(String key) {
        return getProperty(key, "queries.properties");
    }
    
    private static String getProperty(String key, String configFileName) {
    	Properties prop = new Properties();
        String value = null;
        try (InputStream input = AppProperties.class.getClassLoader().getResourceAsStream(configFileName)) {
            if (input != null) {
                prop.load(input);
                value = prop.getProperty(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
