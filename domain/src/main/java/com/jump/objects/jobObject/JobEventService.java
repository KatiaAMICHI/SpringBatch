package com.jump.objects.jobObject;

import com.jump.configs.CacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "jobEvents")
public class JobEventService {

	@Autowired
	private CacheClient cacheClient;
	
	@Cacheable(value="jobEvents", key="#parJobEventId", unless = "#result==null")
	public JobEvent getJobEventById(final Long parJobEventId) {
		return cacheClient.get(parJobEventId);
	}

	@CacheEvict(value="jobEvents", key="#parJobEvent")
	public JobEvent save(final JobEvent parJobEvent) {
		return cacheClient.put(parJobEvent.getJobId(), parJobEvent);
	}

	@CachePut(value="jobEvents")
	public JobEvent setPartitonId(final Long parJobEventId, final Integer parPartitionId) {
		JobEvent updatedUser = null;
		JobEvent userFromDB = cacheClient.get(parJobEventId);
		if(userFromDB != null) {
			userFromDB.setChannelPartition(parPartitionId);
			updatedUser = cacheClient.put(userFromDB.getJobId(), userFromDB);
		}
		return updatedUser;
	}
}