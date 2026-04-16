package se.skltp.tak.core.facade;

import java.io.OutputStream;
import java.util.List;

import se.skltp.tak.core.entity.PubVersion;

public interface TakPublishVersion {

	List<PubVersion> getAllPubVersions();
	
	void getJSONFromDb(OutputStream jsonOutputStream) throws Exception;
	
	void resetPVCache(Integer version);

	long getCurrentVersion();

}
