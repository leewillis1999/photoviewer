package com.willissoftware.photoviewer.service;

import com.willissoftware.photoviewer.common.domain.entity.PhotoEntity;
import com.willissoftware.photoviewer.common.domain.model.Photo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class PhotoViewerService {

    private static final Logger logger = LoggerFactory.getLogger(PhotoViewerService.class);

    private PhotoLoaderService photoLoaderService;

    private int currentPicture = -1;

    @Autowired
    public PhotoViewerService(PhotoLoaderService photoLoaderService) {
        this.photoLoaderService = photoLoaderService;
    }

    public Photo getNextPicture(){
        //currentPicture++;
        movePictureIndex(true);
        logger.info("getNextPhoto: id is - " + currentPicture);
        return loadPicture(currentPicture);
    }

    public Photo getPreviousPicture(){
        movePictureIndex(false);
        logger.info("getPreviousPicture: id is - " + currentPicture);
        return loadPicture(currentPicture);
    }

    public int movePictureIndex(boolean forwards){
        if(forwards){
            currentPicture++;
            if(currentPicture >= this.photoLoaderService.getPhotoCount()-1){
                logger.info("Got to the end of the list... going back to 0");
                currentPicture =0;
            }
        }else{
            currentPicture--;
            if(currentPicture <0){
                logger.info("Got to the start of the list - going back to end");
                currentPicture = photoLoaderService.getPhotoCount()-1;
            }
        }
        return currentPicture;
    }

    public Photo getCurrentPicture(){
        if(currentPicture<0){
            currentPicture=0;
        }
        logger.info("getCurrentPicture: id is - " + currentPicture);
        return loadPicture(currentPicture);
    }

    public Photo getFirstPicture(){
        currentPicture=0;
        logger.info("getFirstPicture: id is - " + currentPicture);
        return loadPicture(currentPicture);
    }

    public Photo deleteCurrentPicture(){
        logger.info("deleteCurrentPicture: id is - " + currentPicture);
        String fileName = this.photoLoaderService.getFiles().get(currentPicture);
        logger.info("deleting picture {}", fileName);

        if(this.photoLoaderService.movePictureToDeleted(fileName)){
            logger.info("Successfully deleted the file");
        }else{
            logger.warn("Failed to delete the file. Check the log for errors");
        }
        //currentPicture should now refer to the next on in the list
        return getCurrentPicture();
    }

    public Photo likeCurrentPicture(){
        logger.info("likeCurrentPicture: id is - " + currentPicture);
        //like it and return the same pic
        setLikes(1);
        return getCurrentPicture();
    }

    public Photo unlikeCurrentPicture(){
        logger.info("unlikeCurrentPicture: id is - " + currentPicture);
        //unlike will remove it from the list if it falls below 0
        int likes = setLikes(-1);
        if(likes < 0){
            logger.info("Likes has fallen below zero so return the current picture");
            return getCurrentPicture();
        }else{
            logger.info("Likes are above zero ({}) so return the next picture", likes);
            return getNextPicture();
        }
    }

    private int setLikes(int add){
        String file = this.photoLoaderService.getFiles().get(currentPicture);
        logger.info("setLikes : {}, {}", file, add);
        int likes = this.photoLoaderService.setLikes(file, add);
        logger.info("Photo {} has {} likes", file, likes);
        return likes;
    }

    private Photo loadPicture(int picId){
        String file = this.photoLoaderService.getFiles().get(picId);
        logger.info("loadPicture: " + picId + " - " + file);

        //get info from database
        PhotoEntity pe = this.photoLoaderService.getPictureFromDb(file);
        Photo ret = new Photo(file);
        if(pe!=null){
            logger.info("Picture {} has {} likes in database", file, pe.getLikes());
            ret.setLikes(pe.getLikes());
        }
        ret.setCount( (picId+1) + "/" + this.photoLoaderService.getFiles().size());
        ret.load();
        return ret;
    }

}
