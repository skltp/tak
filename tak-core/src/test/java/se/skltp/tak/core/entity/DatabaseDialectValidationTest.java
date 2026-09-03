/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.entity;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseDialectValidationTest {

    @ParameterizedTest
    @CsvSource({
            "org.hibernate.dialect.SQLServerDialect, bit",
            "org.hibernate.dialect.PostgreSQLDialect, boolean",
            "org.hibernate.dialect.MySQLDialect, bit",
            "org.hibernate.dialect.MariaDBDialect, bit",
            "org.hibernate.dialect.H2Dialect, boolean"
    })
    void testEntitiesAndSqlTypesForDialect(String dialectClass, String expectedBooleanSqlType) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.DIALECT, dialectClass)
                .build();

        try {
            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(AnropsAdress.class);
            sources.addAnnotatedClass(Anropsbehorighet.class);
            sources.addAnnotatedClass(Filter.class);
            sources.addAnnotatedClass(Filtercategorization.class);
            sources.addAnnotatedClass(LogiskAdress.class);
            sources.addAnnotatedClass(PubVersion.class);
            sources.addAnnotatedClass(RivTaProfil.class);
            sources.addAnnotatedClass(Tjanstekomponent.class);
            sources.addAnnotatedClass(Tjanstekontrakt.class);
            sources.addAnnotatedClass(Vagval.class);

            MetadataImplementor metadata = (MetadataImplementor) sources.buildMetadata();
            assertNotNull(metadata);

            // Verifiera att basklassens 'deleted'-kolumn faktiskt genererar rätt dialektspecifik SQL-typ
            Database database = metadata.getDatabase();
            Table vagvalTable = database.getDefaultNamespace().locateTable(Identifier.toIdentifier("Vagval"));
            assertNotNull(vagvalTable, "Table Vagval should exist in metadata");

            Column deletedColumn = vagvalTable.getColumn(Identifier.toIdentifier("deleted"));
            assertNotNull(deletedColumn, "Column 'deleted' should exist in Vagval");

            String actualSqlType = deletedColumn.getSqlType(metadata).toLowerCase();
            assertTrue(actualSqlType.startsWith(expectedBooleanSqlType.toLowerCase()),
                    "Expected SQL type for 'deleted' under " + dialectClass + " to start with '" +
                            expectedBooleanSqlType + "', but was: '" + actualSqlType + "'");

        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
