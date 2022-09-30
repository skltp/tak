package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

@Service
public class ConfigurationService {

    @Autowired
    BuildProperties buildProperties;

    Properties configFileProperties = new Properties();

    /**
     * Reads optional environment properties from external file.
     * Does not use PropertySource in order for the location to be configurable.
     * @param configFile
     * @throws IOException
     */
    public void init(File configFile) throws IOException {
        InputStream in = new FileInputStream(configFile);
        configFileProperties.load(in);
    }

    public String getAppVersion() {
        return buildProperties.getVersion();
    }

    public String getEnvironment() {
        return configFileProperties.getProperty("tak.environment", "LOCAL");
    }

    public String getLogoImage() {
        return configFileProperties.getProperty("tak.image.logo", "inera-logo.png");
    }

    public String getBackgroundStyle() {
        return configFileProperties.getProperty("tak.background", "#ffffff;");
    }
}
