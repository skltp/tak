/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.exception;
public class PubVersionLockedException extends RuntimeException {

	private static final long serialVersionUID = 7213649767294975339L;

	public PubVersionLockedException() {
		super();
	}

	public PubVersionLockedException(String message) {
		super(message);
	}

}
