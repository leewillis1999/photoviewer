package com.willissoftware.photoviewer.api;

import com.willissoftware.photoviewer.common.domain.model.Photo;
import com.willissoftware.photoviewer.service.EventEmitterService;
import com.willissoftware.photoviewer.service.PhotoViewerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping("/api/screen")
public class ScreenApi {

    private static final Logger logger = LoggerFactory.getLogger(ScreenApi.class);

    private PhotoViewerService photoViewerService;
    private EventEmitterService eventEmitterService;

    @Autowired
    public ScreenApi(PhotoViewerService photoViewerService, EventEmitterService eventEmitterService) {
        this.photoViewerService = photoViewerService;
        this.eventEmitterService = eventEmitterService;
    }

    @GetMapping(value = "/nextPicture")
    public ResponseEntity<Photo> getNextPicture(){
        logger.info("/nextPicture");
        return ResponseEntity.ok(photoViewerService.getNextPicture());
    }

    @GetMapping(value="/prevPicture")
    public ResponseEntity<Photo> getPrevPicture(){
        logger.info("/prevPicture");
        return ResponseEntity.ok(photoViewerService.getPreviousPicture());
    }

    @GetMapping(value="/current")
    public ResponseEntity<Photo> getCurrentPicture(){
        logger.info("/current");
        return ResponseEntity.ok(photoViewerService.getCurrentPicture());
    }

    @GetMapping(value = "/delete")
    public ResponseEntity<Photo> deletePicture(){
        logger.info("/delete");
        return ResponseEntity.ok(photoViewerService.deleteCurrentPicture());
    }

    @GetMapping(value = "/like")
    public ResponseEntity<Photo> likePicture(){
        logger.info("/like");
        return ResponseEntity.ok(photoViewerService.likeCurrentPicture());
    }

    @GetMapping(value = "/dontlike")
    public ResponseEntity<Photo> unlikePicture(){
        logger.info("/dontlike");
        return ResponseEntity.ok(photoViewerService.unlikeCurrentPicture());
    }

    @GetMapping(value = "/home")
    public ResponseEntity<Photo> getFirstPicture(){
        logger.info("/home");
        return ResponseEntity.ok(photoViewerService.getFirstPicture());
    }


    @GetMapping(value = "/sse", produces = {"text/event-stream"})
    public SseEmitter getSseEmitter(HttpServletResponse response){
        logger.info("/sse");
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        logger.info("Returning SseEmitter");
        return this.eventEmitterService.getSseEmitter();
    }

/*
    @PostMapping(value="/remote")
    public void sendRemoteMessage(@RequestBody String message){
        logger.info("/remote : {}", message);
        sendMessage(message);
    }
*/


}
