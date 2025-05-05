package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import jakarta.servlet.ServletContext;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ConfigurationService implements ServletContextAware {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);

    BuildProperties buildProperties;
    SslBundles sslBundles;
    ServletContext context;

    Path certificateDirectory;
    @Value("${tak.web.resource.dir:#{null}}") String resourceDir;

    @Value("${tak.platform:SKLTP-DEFAULT}") String platform;

    @Value("${tak.image.logo:inera-logo.png}") String logoImage;
    static final String DEFAULT_LOGO_IMAGE = "inera-logo.png";
    @Value("${tak.background:#ffffff;}") String backgroundStyle;

    @Value("${tak.bestallning.on:false}") boolean bestallningOn;
    @Value("${tak.bestallning.url:#{null}}") String bestallningUrl;
    @Value("${tak.bestallning.cert:#{null}}") String bestallningClientCert;
    @Value("${tak.bestallning.certType:pkcs12}") String bestallningClientCertType;
    @Value("${tak.bestallning.pw:#{null}}") String bestallningClientCertPassword;
    @Value("${tak.bestallning.serverCert:#{null}}") String bestallningServerCert;
    @Value("${tak.bestallning.serverCertType:jks}") String bestallningServerCertType;
    @Value("${tak.bestallning.serverPw:#{null}}") String bestallningServerCertPassword;

    @Value("${tak.bestallning.certBundle:bestallning}") String bestallningCertBundleName;

    @Value("${tak.alert.on.publicera:false}") boolean alertOn;


    public SslBundles getSslBundles() {
        return sslBundles;
    }

    public SslBundle getBestallningCertBundle() {
        return sslBundles.getBundle(bestallningCertBundleName);
    }

    @Autowired
    public ConfigurationService(SslBundles sslBundles, BuildProperties buildProperties) {
        this.sslBundles = sslBundles;
        this.buildProperties = buildProperties;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    public void init() throws IOException {
        if(resourceDir == null) {
            log.warn("tak.web.resource.dir not set");
            return;
        }
        certificateDirectory = Paths.get(resourceDir, "security");
        if (!DEFAULT_LOGO_IMAGE.equals(logoImage)) {
            // Custom logo needs to be copied
            Path source = Paths.get(resourceDir, logoImage);
            Path destination = Paths.get(context.getRealPath("/static/images"), logoImage);
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public String getAppVersion() {
        return buildProperties.getVersion();
    }

    public String getPlatform() { return platform; }

    public String getLogoImage() { return logoImage; }

    public String getBackgroundStyle() { return backgroundStyle; }

    public boolean getBestallningOn() { return bestallningOn; }

    public List<String> getBestallningUrls()  {
        return bestallningUrl == null
                ? List.of()
                : List.of(bestallningUrl.split("\\s*,\\s*"));
    }


    public Path getBestallningClientCert()  {
        if (certificateDirectory == null || bestallningClientCert == null) {
            return null;
        }
        return certificateDirectory.resolve(bestallningClientCert);
    }

    public String getBestallningClientCertType()  { return bestallningClientCertType; }

    public String getBestallningClientCertPassword()  { return bestallningClientCertPassword; }

    public Path getBestallningServerCert()  {
        if (certificateDirectory == null || bestallningServerCert == null) {
            return null;
        }
        return certificateDirectory.resolve(bestallningServerCert);
    }

    public String getBestallningServerCertType()  { return bestallningServerCertType; }

    public String getBestallningServerCertPassword()  { return bestallningServerCertPassword; }

    public boolean getAlertOn() { return alertOn; }
}
