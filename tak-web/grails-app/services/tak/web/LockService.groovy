/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 					<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package tak.web

import se.skltp.tak.web.entity.Locktb
import se.skltp.tak.core.exception.PubVersionLockedException
import org.springframework.transaction.annotation.Transactional

@Transactional
class LockService {
		
	def retrieveLock() {
		def locktb = Locktb.lock("PubVersion")
		if(locktb.locked == 1) {
			String msg = "Publish failed. A publication job is already running"
			log.error msg
			throw new PubVersionLockedException(msg)
		} else {
			locktb.locked = 1
			locktb.save(flush: true)
		}
		return locktb
	}

	def releaseLock(Locktb locktb) {
		locktb.locked = 0
		locktb.save(flush: true)
	}
}
