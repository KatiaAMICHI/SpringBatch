package com.jump.integration;

import java.util.concurrent.Executors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

@Slf4j
@RequiredArgsConstructor
public class HeartbeatServer {
	@Autowired
	@Qualifier("inboundChannel")
	private final PollableChannel inboundChannel;
	@Autowired
	@Qualifier("outboundChannel")
	private final MessageChannel outboudChannel;

	@EventListener
	public void initializaAfterContextIsReady(ContextRefreshedEvent event) {
		log.info("Starting Heartbeat");
		start();
	}

	public void start() {
		Executors.newSingleThreadExecutor().execute(() -> {
			while (true) {
				try {
					Message<?> message = inboundChannel.receive();
					if (message == null) {
						log.error("Heartbeat timeouted");
					} else {
						String messageStr = new String((byte[]) message.getPayload());
						if (messageStr.equals("status")) {
							log.info("Heartbeat received");
							outboudChannel.send(new GenericMessage<>("OK"));
						} else {
							log.error("Unexpected message content from client: " + messageStr);
						}
					}
				} catch (Exception e) {
					log.error("Excepetion listener Server" + String.valueOf(e));
				}
			}
		});
	}
}