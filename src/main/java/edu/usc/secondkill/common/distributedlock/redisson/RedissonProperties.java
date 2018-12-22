package edu.usc.secondkill.common.distributedlock.redisson;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;


@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {
    private int timeout = 3000;
    private String address;
    private String password;
    private int connectionPoolSize = 64;
    private int connectionMinimumIdleSize=10;
    private int slaveConnectionPoolSize = 250;
    private int masterConnectionPoolSize = 250;
    private String[] sentinelAddresses;
    private String masterName;
}
