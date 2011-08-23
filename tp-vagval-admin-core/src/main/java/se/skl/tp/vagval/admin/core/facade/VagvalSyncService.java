/**
 * Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
 */
package se.skl.tp.vagval.admin.core.facade;

import java.util.List;
import java.util.Set;

public interface VagvalSyncService {
	
	List<VirtualiseringInfo> getAllVirtualisering();
		
	List<AnropsbehorighetInfo> getAllAnropsbehorighet();

	List<VirtualiseringInfo> getVirtualiseringByTjanstekontrakt(String namnrymd);
	
	List<AnropsbehorighetInfo> getAnropsbehorighetByTjanstekontrakt(String namnrymd);
	
	Set<String> getAllSupportedNamespacesByLogicalAddress(final String logicalAddress);

}
