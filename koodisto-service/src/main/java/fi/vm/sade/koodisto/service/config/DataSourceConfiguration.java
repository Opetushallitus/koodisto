package fi.vm.sade.koodisto.service.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataSourceConfiguration {

    @Bean
    public HikariConfig hikariConfig(PostgreSqlConfigurationProperties properties) {
        HikariConfig config = new HikariConfig();
        config.setConnectionTestQuery(properties.connectionTestQuery);
        config.setConnectionTimeout(properties.maxwait);
        config.setDataSourceClassName(properties.dataSourceClassName);
        config.setMaximumPoolSize(properties.maxactive);
        config.setMaxLifetime(properties.maxlifetimemillis);
        config.setPoolName(properties.poolName);
        return config;
    }

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }
}
