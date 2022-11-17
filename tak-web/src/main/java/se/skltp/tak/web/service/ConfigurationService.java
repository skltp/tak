package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

@Service
public class ConfigurationService implements ServletContextAware {

    @Autowired
    BuildProperties buildProperties;

    ServletContext context;

    Properties configFileProperties = new Properties();

    Path certificateDirectory;

    private static final String DEFAULT_LOGO_IMAGE = "inera-logo.png";

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    /**
     * Reads optional environment properties from external file.
     * Does not use PropertySource in order for the location to be configurable.
     * @param configFilePath
     * @param resourceDir
     * @throws IOException
     */
    public void init(String configFilePath, String resourceDir) throws IOException {
        File configFile = new File(configFilePath);
        InputStream in = new FileInputStream(configFile);
        configFileProperties.load(in);
        certificateDirectory = Paths.get(resourceDir, "security");
        prepareLogoImage(resourceDir, getLogoImage());
    }

    private void prepareLogoImage(String resourceDir, String logoImage) throws IOException {
        if (logoImage == DEFAULT_LOGO_IMAGE) return;
        // Custom logo needs to be copied
        Path source = Paths.get(resourceDir, logoImage);
        Path destination = Paths.get(context.getRealPath("/static/images"), logoImage);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    public String getAppVersion() {
        return buildProperties.getVersion();
    }

    public String getEnvironment() {
        return configFileProperties.getProperty("tak.environment", "LOCAL");
    }

    public String getLogoImage() {
        return configFileProperties.getProperty("tak.image.logo", DEFAULT_LOGO_IMAGE);
    }

    public String getBackgroundStyle() {
        return configFileProperties.getProperty("tak.background", "#ffffff;");
    }

    public boolean getBestallningOn() { return Boolean.parseBoolean(configFileProperties.getProperty("tak.bestallning.on")); }

    public String getBestallningUrl()  { return configFileProperties.getProperty("tak.bestallning.url"); }

    public Path getBestallningClientCert()  {

        return certificateDirectory.resolve(configFileProperties.getProperty("tak.bestallning.cert"));
    }

    public String getBestallningClientCertPassword()  { return configFileProperties.getProperty("tak.bestallning.pw"); }

    public Path getBestallningServerCert()  {
        return certificateDirectory.resolve(configFileProperties.getProperty("tak.bestallning.serverCert"));
    }

    public String getBestallningServerCertPassword()  { return configFileProperties.getProperty("tak.bestallning.serverPw"); }

    public String getPlatform() { return configFileProperties.getProperty("tak.platform"); }
}
