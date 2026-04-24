/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
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
