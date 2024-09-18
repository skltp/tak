package se.skltp.tak.web.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.session.JdbcSessionProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "spring.session.store-type", havingValue = "jdbc")
@EnableJdbcHttpSession
public class JdbcSessionConf {

    // Bean for JDBC session properties, ensuring the correct configuration is used for JDBC sessions
    @Bean
    JdbcSessionProperties jdbcSessionProperties() {
        return new JdbcSessionProperties();
    }

    // This bean initializes the session schema only when the session store-type is set to JDBC
    @Bean
    public DataSourceInitializer sessionDataSourceInitializer(DataSource dataSource, DataSourceProperties dataSourceProperties, JdbcSessionProperties properties) {

        // Detects the correct database driver based on the DataSource URL
        DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl());

        // Dynamically selects the appropriate schema script for the detected database platform
        String schemaLocation = properties.getSchema().replace("@@platform@@", databaseDriver.getId());
        Resource schemaScript = new PathMatchingResourcePatternResolver().getResource(schemaLocation);

        // Sets up a populator to execute the schema script to create session tables
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        populator.setContinueOnError(true); // Prevents failure if the tables already exist

        // Initializes the DataSource with the schema populator
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

}
