package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Autowired
    BuildProperties buildProperties;

    @Value("${tak.environment:unknown}")
    String environment;

    @Value("${tak.image.logo:unknown}")
    String logoImage;

    @Value("${tak.background:unknown}")
    String backgroundColor;

    public String getAppVersion() {
        return buildProperties.getVersion();
    }

    public String getEnvironment() {
        return environment;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public String getBackgroundStyle() {
        return backgroundColor;
    }
}
