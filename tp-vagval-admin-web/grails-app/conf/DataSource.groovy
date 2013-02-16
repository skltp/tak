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
			driverClassName = "org.hsqldb.jdbcDriver"
			username = "sa"
			password = ""
			url = "jdbc:hsqldb:mem:devDB"
		}
		hibernate {
			dialect = "org.hibernate.dialect.HSQLDialect"
		}
    }
	test {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "org.hsqldb.jdbcDriver"
			username = "sa"
			password = ""
			url = "jdbc:hsqldb:mem:testDB"
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
