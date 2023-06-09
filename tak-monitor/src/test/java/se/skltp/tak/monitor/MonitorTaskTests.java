package se.skltp.tak.monitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.monitor.service.ResetService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MonitorTaskTests {

	MonitorTask monitorTask;

	@Mock
	PubVersionDao pubVersionDaoMock;

	@Mock
	ResetService resetServiceMock;

	@BeforeEach
	public void setUp() {
		PubVersion pv = new PubVersion();
		pv.setId(42);
		MockitoAnnotations.openMocks(this);
		Mockito.when(pubVersionDaoMock.getLatestPubVersion()).thenReturn(pv);
		monitorTask = new MonitorTask(pubVersionDaoMock, resetServiceMock);
	}

	@Test
	void pollPubVersion_callsResetOnFirstPoll() {
		monitorTask.pollPubVersion();
		verify(resetServiceMock, times(1)).resetNodes();
	}

	@Test
	void pollPubVersion_noResetCallIfUnchanged() {
		monitorTask.pollPubVersion();
		monitorTask.pollPubVersion();
		verify(resetServiceMock, times(1)).resetNodes();
	}

	@Test
	void pollPubVersion_callResetAgainIfNewPv() {
		monitorTask.pollPubVersion();
		PubVersion newPv = new PubVersion();
		newPv.setId(43);
		Mockito.when(pubVersionDaoMock.getLatestPubVersion()).thenReturn(newPv);
		monitorTask.pollPubVersion();
		verify(resetServiceMock, times(2)).resetNodes();
	}

	@Test
	void pollPubVersion_handleNullFromDatabase() {
		Mockito.when(pubVersionDaoMock.getLatestPubVersion()).thenReturn(null);
		monitorTask.pollPubVersion();
		verify(resetServiceMock, times(0)).resetNodes();
	}
}
