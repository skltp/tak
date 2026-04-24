/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.services;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;

@Transactional
public abstract class AbstractServiceTest {

    @Autowired
    DataSource dataSource;

    @BeforeTransaction
    public void onSetUpInTransaction() throws Exception {
        AbstractServiceTest.cleanInsert(dataSource);
    }

    public static void cleanInsert(DataSource dataSource) throws Exception {
        ITableMetaData tableMetaData = new DefaultTableMetaData("pubVersion", new Column[]{
                new Column("id", DataType.INTEGER),
                new Column("formatVersion", DataType.INTEGER),
                new Column("time", DataType.TIMESTAMP),
                new Column("utforare", DataType.VARCHAR),
                new Column("kommentar", DataType.VARCHAR),
                new Column("version", DataType.INTEGER),
                new Column("storlek", DataType.INTEGER),
                new Column("data", DataType.VARCHAR)
        });

        DefaultTable table = new DefaultTable(tableMetaData);
        table.addRow(new Object[]{1, 1, "2009-03-10 12:01:09", "Kalle", "Kommentar", 1, 2, "./src/test/resources/export.gzip"});

        DefaultDataSet dataSet = new DefaultDataSet();
        dataSet.addTable(table);

        try (Connection connection = dataSource.getConnection()) {
            IDatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
        }
    }
}
