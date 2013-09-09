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
dataSource {
    pooled = true
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
	naming_strategy=org.hibernate.cfg.EJB3NamingStrategy
}
// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "create-drop" // one of 'create', 'create-drop','update'
			driverClassName = "org.h2.Driver"
			username = "sa"
			password = ""
			url = "jdbc:h2:mem:devDB;MVCC=TRUE;LOCK_TIMEOUT=10000"
		}
		hibernate {
			dialect = "org.hibernate.dialect.HSQLDialect"
		}
    }
	test {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "org.h2.Driver"
			username = "sa"
			password = ""
			url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
		}
		hibernate {
			dialect = "org.hibernate.dialect.HSQLDialect"
		}
	}
	production {
		dataSource {
			// driverClassName = "com.mysql.jdbc.Driver"
			// username = "tpadminuser"
			// password = "tpadminpassword"
			// url = "jdbc:mysql://localhost:3306/tp_admin"
			jndiName = "java:comp/env/jdbc/TP"
		}
	}
}
