package com.willissoftware.photoviewer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class EventEmitterService {

    private static final Logger logger = LoggerFactory.getLogger(EventEmitterService.class);

    private SseEmitter sseEmitter;

    public SseEmitter getSseEmitter() {
        if(sseEmitter==null){
            logger.info("Creating new SseEmitter");
            sseEmitter = new SseEmitter(0L);
            sseEmitter.onCompletion( () -> {
                logger.info("Emitter is complete");
            });
            sseEmitter.onError( (e) -> {
                logger.error("Emitter onerror " + e.getMessage());
            });
            sseEmitter.onTimeout( () -> {
                logger.warn("Emitter has timed out");
            });
        }
        return sseEmitter;
    }

    public void sendMessage(String event, String message){
        if(sseEmitter==null){
            return;
        }

        logger.info("Sending sse message: " + message);
        try {
            SseEmitter.SseEventBuilder builder = SseEmitter.event();
            builder.name(event);
            builder.data(message);
            sseEmitter.send(builder);
        } catch (IOException e) {
            logger.error("Failed to send the message: " + e.getMessage());
        }
    }

}
