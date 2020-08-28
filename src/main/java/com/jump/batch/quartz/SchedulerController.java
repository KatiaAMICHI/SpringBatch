package com.jump.batch.quartz;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
  
@RestController
@RequestMapping("/scheduler")
@ConditionalOnExpression("'${scheduler.enabled}'=='true'")
public class SchedulerController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
  
    @Autowired
    private TriggerMonitor triggerMonitor;
  
    @Resource
    private Scheduler scheduler;
  
    @GetMapping(produces = "application/json")
    public Map<String, String> getStatus() throws SchedulerException {
        log.trace("SCHEDULER -> GET STATUS");
        String schedulerState = "";
        if (scheduler.isShutdown() || !scheduler.isStarted())
            schedulerState = SchedulerStates.STOPPED.toString();
        else if (scheduler.isStarted() && scheduler.isInStandbyMode())
            schedulerState = SchedulerStates.PAUSED.toString();
        else
            schedulerState = SchedulerStates.RUNNING.toString();
        return Collections.singletonMap("data", schedulerState.toLowerCase());
    }
  
    @GetMapping("/pause")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pause() throws SchedulerException {
        log.info("SCHEDULER -> PAUSE COMMAND");
        scheduler.standby();
    }
  
    @GetMapping("/resume")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resume() throws SchedulerException {
        log.info("SCHEDULER -> RESUME COMMAND");
        scheduler.start();
    }
  
    @GetMapping("/run")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void run() throws SchedulerException {
        log.info("SCHEDULER -> START COMMAND");
        scheduler.start();
    }
  
    @GetMapping("/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stop() throws SchedulerException {
        log.info("SCHEDULER -> STOP COMMAND");
        scheduler.shutdown(true);
    }
  
    @GetMapping("/config")
    public ResponseEntity getConfig() {
        log.debug("SCHEDULER -> GET CRON EXPRESSION");
        CronTrigger trigger = (CronTrigger) triggerMonitor.getTrigger();
        return new ResponseEntity<>(Collections.singletonMap("data", trigger.getCronExpression()), HttpStatus.OK);
    }

}