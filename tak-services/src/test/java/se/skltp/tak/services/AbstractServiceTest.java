/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * <http://cehis.se/>
 * <p>
 * This file is part of SKLTP.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.tak.services;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;

import javax.sql.DataSource;
import java.sql.Connection;

public abstract class AbstractServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

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
