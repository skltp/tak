package se.skltp.tak.monitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix="tak.monitor.reset")
public class ResetConfig {

    private boolean usePodLookup;
    private String podNamespace;
    private List<NodeResetConfig> nodes = new ArrayList<>();

    public boolean getUsePodLookup() { return usePodLookup; }

    public void setUsePodLookup(boolean usePodLookup) {
        this.usePodLookup = usePodLookup;
    }

    public String getPodNamespace() { return podNamespace; }

    public void setPodNamespace(String podNamespace) { this.podNamespace = podNamespace; }

    public List<NodeResetConfig> getNodes() {
        return this.nodes;
    }

    public void setNodes(List<NodeResetConfig> nodes) {
        this.nodes = nodes;
    }
}
