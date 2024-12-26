package com.tsspdcl.sas.common;

import java.io.InputStream;
import java.util.Properties;

public final class AppUtils {
    private static final Properties properties;

    static {
        properties = new Properties();

        try {
            ClassLoader classLoader = AppUtils.class.getClassLoader();
            InputStream applicationPropertiesStream = classLoader.getResourceAsStream("application.properties");
            properties.load(applicationPropertiesStream);
        } catch (Exception e) {
            // process the exception
        }
    }
}
