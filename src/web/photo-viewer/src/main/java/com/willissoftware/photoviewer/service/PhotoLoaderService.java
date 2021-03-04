package com.willissoftware.photoviewer.service;

import com.willissoftware.photoviewer.common.configuration.PhotoViewerConfiguration;
import com.willissoftware.photoviewer.common.domain.entity.PhotoEntity;
import com.willissoftware.photoviewer.dao.PhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class takes care of the photos on disk and in the database.
 * used by the photoViewer service
 */
@Service
public class PhotoLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(PhotoLoaderService.class);

    private int photoCount;

    public int getPhotoCount() {
        return photoCount;
    }

    private PhotoViewerConfiguration configuration;
    private PhotoRepository photoRepository;

    //list of folders to search
    private Queue<String> folders;

    //list of files found
    private List<String> files;

    //list of files from the database. The will have likes, etc
    private List<PhotoEntity> photoEntities;

    //extensions to search for
    private Map<String, String> extensions;

    @Autowired
    public PhotoLoaderService(PhotoViewerConfiguration configuration, PhotoRepository photoRepository) {
        this.configuration = configuration;
        this.photoRepository = photoRepository;
    }

    @PostConstruct
    public void Initialise(){

        logger.info("Initialising with folder: " + configuration.getFolder() + ". Deleted: " + configuration.getDeleted());

        folders = new LinkedList<>();
        files = new ArrayList<>();
        extensions =  new HashMap<>();

        extensions.put(".jpg", ".jpg");
        extensions.put(".png", ".png");
        folders.add(configuration.getFolder());

        logger.info("Loading photos from database");
        this.photoEntities = StreamSupport
                .stream(this.photoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        logger.info("Loaded {} from database", this.photoEntities.size());

        logger.info("Loading photos from {}", configuration.getFolder());

        int count = 0;
        count +=getFiles(folders.remove(), extensions);
        shuffleFiles(count);
        photoCount = count;

        logger.info("Found {} pictures", photoCount);
    }

    private int getFiles(String folder, Map<String, String> extensions){

        logger.info("Loading files from " + folder);
        FileFilter ff = pathname -> {
            if(pathname.isDirectory()){
                return true;
            }
            if(!pathname.getName().contains(".")){
                return false;
            }
            String ext = pathname.getName().substring(pathname.getName().lastIndexOf(".")).toLowerCase();
            return extensions.containsKey(ext);
        };
        try{
            final Stream<Path> stream = Files.walk(Paths.get(folder));
            stream.forEach( f -> {
                File file = f.toFile();
                if (file.isDirectory()) {
                    folders.add(file.getAbsolutePath());
                } else {

                    String ext = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
                    if(extensions.containsKey(ext)){
                        //check that the picture isn't in the not liked list (has < 0 likes)
                        String fn = file.getAbsolutePath().replace(configuration.getFolder(), "");
                        PhotoEntity pe = this.photoEntities.stream().filter( p ->
                                p.getFilename().equals(fn)
                        ).findFirst().orElse(null);
                        if(pe==null || pe.getLikes()>=0){
                            files.add(file.getAbsolutePath());
                        }else{
                            logger.info("Not adding " + file.getAbsolutePath() + " as has " + pe.getLikes() + " likes");
                        }
                    }
                }
            });
        }catch(Exception ex){
            logger.error("Exception: " + ex.getMessage() + " - " + ex.getStackTrace());
        }
        return files.size();
    }

    private void shuffleFiles(int times){
        logger.info("Shuffling...");
        int idx1;
        try{
            for(int outer = 0; outer < 10; outer ++){
                logger.info("Outer shuffle " + (outer + 1) + " of 10. Inner shuffle will be done " + times + " times");
                for(int idx=0; idx<times; idx++){
                    idx1 = new Random().nextInt(files.size());
                    //idx2 = new Random().nextInt(files.size());
                    String tmp = files.get(idx);
                    files.set(idx, files.get(idx1));
                    files.set(idx1, tmp);
                }
            }
        }catch(Exception ex){
            logger.error(ex.getMessage());
        }
    }

    public List<String> getFiles() {
        return files;
    }

    public boolean movePictureToDeleted(String fileName){
        String newFile = fileName.replace(this.configuration.getFolder(), this.configuration.getDeleted());
        logger.info("Moving picture from {} to {}", fileName, newFile);
        try {

            Path folder = Paths.get(newFile).getParent();
            Files.createDirectories(folder);

            Files.move(Paths.get(fileName), Paths.get(newFile), StandardCopyOption.REPLACE_EXISTING);

            //remove the file from the getFiles array
            files.remove(fileName);

            return true;
        } catch (IOException e) {
            logger.error("movePictureToDeleted: {} {}", e.getMessage(), e.getStackTrace());
            return false;
        }
    }

    public PhotoEntity getPictureFromDb(String file){
        logger.info("Loading {} from db", file);
        String newFile = file.replace(this.configuration.getFolder(), "");
        PhotoEntity pe = this.photoRepository.findFirstByFilenameEquals(newFile);
        return pe;
    }

    public int setLikes(String file, int add){

        //PhotoEntity pe = this.photoRepository.findFirstByFilenameEquals(newFile);

        PhotoEntity pe = getPictureFromDb(file);
        if(pe==null){
            logger.info("picture currently has no likes so creating a new entry");
            String newFile = file.replace(this.configuration.getFolder(), "");
            pe = new PhotoEntity();
            pe.setFilename(newFile);
            pe.setLikes(add);
        }else{
            pe.setLikes(pe.getLikes()+add);
            logger.info("picture now has {} likes", pe.getLikes());
        }

        if(pe.getLikes()==0){
            logger.info("picture has 0 likes so removing from database");
            this.photoRepository.delete(pe);
        }else{
            logger.info("saving picture in database");
            this.photoRepository.save(pe);
        }

        //int idx = files.indexOf(file);
        //String photo = files.get(idx);

        if(pe.getLikes()<0){
            logger.info("picture has less than 0 likes so removing from collection");
            files.remove(file);
        }
        return pe.getLikes();
    }


}
