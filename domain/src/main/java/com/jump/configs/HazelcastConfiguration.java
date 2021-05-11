package com.jump.configs;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hazelcast.HazelcastKeyValueAdapter;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.core.KeyValueTemplate;

@Configuration
@EnableCaching
public class HazelcastConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        final Config locConfig = new Config();
        final MapConfig locMapConfig = new MapConfig();
        locMapConfig.setName("jobevents");
        locMapConfig.setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE));
        locMapConfig.setEvictionPolicy(EvictionPolicy.LRU);
        locMapConfig.setTimeToLiveSeconds(60);
        locConfig.setInstanceName("hazelcast-instance");
        locConfig.addMapConfig(locMapConfig);

        return Hazelcast.newHazelcastInstance(locConfig);
    }

    @Bean
    public KeyValueOperations keyValueTemplate() {
        return new KeyValueTemplate(new HazelcastKeyValueAdapter(hazelcastInstance()));
    }
    @Bean
    public HazelcastKeyValueAdapter  hazelcastKeyValueAdapter() {
        return new HazelcastKeyValueAdapter(hazelcastInstance());
    }

}