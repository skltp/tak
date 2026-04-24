/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.monitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "tak.monitor.reset")
public class ResetConfig {

  private boolean usePodLookup;
  private String podNamespace;
  private List<NodeResetConfig> nodes = new ArrayList<>();

  public boolean getUsePodLookup() {
    return usePodLookup;
  }

  public void setUsePodLookup(boolean usePodLookup) {
    this.usePodLookup = usePodLookup;
  }

  public String getPodNamespace() {
    return podNamespace;
  }

  public void setPodNamespace(String podNamespace) {
    this.podNamespace = podNamespace;
  }

  public List<NodeResetConfig> getNodes() {
    return this.nodes;
  }

  public void setNodes(List<NodeResetConfig> nodes) {
    this.nodes = nodes;
  }
}
