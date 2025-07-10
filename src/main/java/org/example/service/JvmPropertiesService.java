package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class JvmPropertiesService {
    @Autowired
    private Environment environment;

    public String getJvmProperty(String key) {
        return environment.getProperty(key);
    }

    public String getJvmProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }
}
