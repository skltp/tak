package se.skltp.tak.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix="tak.reset")
public class ResetConfig {

    private boolean usePodLookup;
    private List<NodeResetConfig> applications = new ArrayList<>();
    private List<NodeResetConfig> takServices = new ArrayList<>();

    public boolean getUsePodLookup() { return usePodLookup; }

    public void setUsePodLookup(boolean usePodLookup) {
        this.usePodLookup = usePodLookup;
    }

    public List<NodeResetConfig> getTakServices() {
        return this.takServices;
    }

    public void setTakServices(List<NodeResetConfig> takServices) {
        this.takServices = takServices;
    }

    public List<NodeResetConfig> getApplications() {
        return this.applications;
    }

    public void setApplications(List<NodeResetConfig> applications) {
        this.applications = applications;
    }
}
