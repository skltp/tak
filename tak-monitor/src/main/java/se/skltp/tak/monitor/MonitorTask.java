package se.skltp.tak.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.monitor.service.ResetService;

import java.util.Objects;

@Component
public class MonitorTask {
  private static final Logger log = LoggerFactory.getLogger(MonitorTask.class);
  private final PubVersionDao pubVersionDao;
  private final ResetService resetService;

  private Long previousPubVersionId = null;

  public MonitorTask(@Autowired PubVersionDao pubVersionDao, @Autowired ResetService resetService) {
    this.pubVersionDao = pubVersionDao;
    this.resetService = resetService;
  }

  @Scheduled(fixedDelayString = "${tak.monitor.interval}", initialDelayString = "${tak.monitor.initial-delay}")
  public void pollPubVersion() {
    try {
      PubVersion pv = pubVersionDao.getLatestPubVersion();
      if (pv == null) {
        log.error("Could not get latest PubVersion from database");
        return;
      }
      if (Objects.equals(previousPubVersionId, pv.getId())) {
        log.debug("Latest PubVersion unchanged: {}", pv.getId());
        return;
      }
      log.info("Latest PubVersion is {}, previous {}", pv.getId(), previousPubVersionId);
      if (pv.getStorlek() == 0L) {
        log.warn("PubVersion {} size is zero, aborting", pv.getId());
        return;
      }
      resetService.resetNodes();
      previousPubVersionId = pv.getId();
    } catch (Exception e) {
      log.error("Failed to check or reset PubVersion", e);
    }
  }
}
