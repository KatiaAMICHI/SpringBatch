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

  private HazelcastInstance _client = HazelcastClient.newHazelcastClient(creatClientConfig());

  public Collection<JobEvent> getAll() {
    final IMap<Long, JobEvent> locMapCache = _client.getMap(JOBEVENTS);
    return locMapCache.values();
  }

  public JobEvent put(final Long locId, final JobEvent locJobEvent) {
    final IMap<Long, JobEvent> map = _client.getMap(JOBEVENTS);
    return map.putIfAbsent(locId, locJobEvent);
  }

  public JobEvent get(final Long locId) {
    final IMap<Long, JobEvent> map = _client.getMap(JOBEVENTS);
    return map.get(locId);
  }

  private ClientConfig creatClientConfig() {
    final ClientConfig locClientConfig = new ClientConfig();
    locClientConfig.addNearCacheConfig(createNearCacheConfig());
    return locClientConfig;
  }

  private NearCacheConfig createNearCacheConfig() {
    final NearCacheConfig locNearCacheConfig = new NearCacheConfig();
    locNearCacheConfig.setName(JOBEVENTS);
    locNearCacheConfig.setTimeToLiveSeconds(360);
    locNearCacheConfig.setMaxIdleSeconds(60);
    return locNearCacheConfig;
  }
}