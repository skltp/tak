/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
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
package se.skltp.tak.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.memdb.LatestPublishedVersion;

@Service()
public class TjanstekomponentDao {
	@Autowired
	private LatestPublishedVersion lpv;

	public List<Tjanstekomponent> getAllTjanstekomponentAndAnropsAdresserAndAnropsbehorigheter() {
		List<Tjanstekomponent> list = new ArrayList<Tjanstekomponent>(lpv.getPvc().tjanstekomponent.values());
		return list;
	}
}
