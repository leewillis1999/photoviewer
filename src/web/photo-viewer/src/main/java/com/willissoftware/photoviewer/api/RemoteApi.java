package com.willissoftware.photoviewer.api;

import com.willissoftware.photoviewer.common.domain.model.Photo;
import com.willissoftware.photoviewer.service.EventEmitterService;
import com.willissoftware.photoviewer.service.PhotoViewerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/remote")
public class RemoteApi {

    private static final Logger logger = LoggerFactory.getLogger(RemoteApi.class);

    private PhotoViewerService photoViewerService;
    private EventEmitterService eventEmitterService;

    @Autowired
    public RemoteApi(PhotoViewerService photoViewerService, EventEmitterService eventEmitterService) {
        this.photoViewerService = photoViewerService;
        this.eventEmitterService = eventEmitterService;
    }

    @GetMapping(value = "/nextPicture")
    public ResponseEntity<Photo> NextPicture(){
        logger.info("/nextPicture");
        photoViewerService.movePictureIndex(true);    //this will move the counter along
        this.eventEmitterService.sendMessage("screen-event", "refresh");
        return ResponseEntity.ok(this.photoViewerService.getCurrentPicture());
    }

    @GetMapping(value = "/prevPicture")
    public ResponseEntity<Photo> PrevPicture(){
        logger.info("/nextPicture");
        photoViewerService.movePictureIndex(false);    //this will move the counter along
        this.eventEmitterService.sendMessage("screen-event", "refresh");
        return ResponseEntity.ok(this.photoViewerService.getCurrentPicture());
    }

    @GetMapping(value = "/delete")
    public ResponseEntity<Photo> deletePicture(){
        photoViewerService.deleteCurrentPicture();
        this.eventEmitterService.sendMessage("screen-event", "refresh");
        return ResponseEntity.ok(this.photoViewerService.getCurrentPicture());
    }

    @GetMapping(value = "/like")
    public ResponseEntity<Photo> likePicture(){
        logger.info("/like");
        photoViewerService.likeCurrentPicture();
        this.eventEmitterService.sendMessage("screen-event", "refresh");
        return ResponseEntity.ok(this.photoViewerService.getCurrentPicture());
    }

    @GetMapping(value = "/dontlike")
    public ResponseEntity<Photo> unlikePicture(){
        logger.info("/dontlike");
        photoViewerService.unlikeCurrentPicture();
        this.eventEmitterService.sendMessage("screen-event", "refresh");
        return ResponseEntity.ok(this.photoViewerService.getCurrentPicture());
    }


}
