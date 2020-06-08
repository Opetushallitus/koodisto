package fi.vm.sade.koodisto.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "koodisto-service.postgresql")
@ConstructorBinding
public class PostgreSqlConfigurationProperties {
    public final String poolName = "springHikariCP";
    public final String connectionTestQuery = "SELECT 1";
    public final String dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource";
    public final int maxactive;
    public final long maxwait;
    public final long maxlifetimemillis;
    public PostgreSqlConfigurationProperties(int maxactive, long maxwait, long maxlifetimemillis) {
        this.maxactive = maxactive;
        this.maxwait = maxwait;
        this.maxlifetimemillis = maxlifetimemillis;
    }
}
