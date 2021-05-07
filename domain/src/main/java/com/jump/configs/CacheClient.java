package com.jump.configs;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jump.objects.jobObject.JobEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CacheClient {

  private static final String JOBEVENTS = "jobevents";

  private HazelcastInstance client = HazelcastClient.newHazelcastClient(creatClientConfig());

  public Collection<JobEvent> getAll() {
    IMap<Long, JobEvent> map = client.getMap(JOBEVENTS);
    return map.values();
  }

  public JobEvent put(final Long key, final JobEvent jobEvent) {
    IMap<Long, JobEvent> map = client.getMap(JOBEVENTS);
    return map.putIfAbsent(key, jobEvent);
  }

  public JobEvent get(final Long key) {
    IMap<Long, JobEvent> map = client.getMap(JOBEVENTS);
    return map.get(key);
  }

  private ClientConfig creatClientConfig() {
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.addNearCacheConfig(createNearCacheConfig());
    return clientConfig;
  }

  private NearCacheConfig createNearCacheConfig() {
    NearCacheConfig nearCacheConfig = new NearCacheConfig();
    nearCacheConfig.setName(JOBEVENTS);
    nearCacheConfig.setTimeToLiveSeconds(360);
    nearCacheConfig.setMaxIdleSeconds(60);
    return nearCacheConfig;
  }
}