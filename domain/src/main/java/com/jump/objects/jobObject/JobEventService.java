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
	
	@Cacheable(value="jobEvents", key="#jobEventId", unless = "#result==null")
	public JobEvent getJobEventById(final Long jobEventId) {
		return cacheClient.get(jobEventId);
	}

	@CacheEvict(value="jobEvents", key="#jobEvent")
	public JobEvent save(final JobEvent jobEvent) {
		return cacheClient.put(jobEvent.getJobId(), jobEvent);
	}

	@CachePut(value="jobEvents")
	public JobEvent setPartitonId(final Long jobEventId, final Integer parPartitionId) {
		JobEvent updatedUser = null;
		JobEvent userFromDB = cacheClient.get(jobEventId);
		if(userFromDB != null) {
			userFromDB.setChannelPartition(parPartitionId);
			updatedUser = cacheClient.put(userFromDB.getJobId(), userFromDB);
		}
		return updatedUser;
	}
}