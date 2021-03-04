package com.willissoftware.photoviewer.common.domain.model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import javaxt.io.Image;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
//import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Data
public class Photo{

    private static final Logger logger = LoggerFactory.getLogger(Photo.class);

    private String fullFileName;
    private String fileName;
    private String imageAsB64;
    private int width;
    private int height;
    private boolean landscape;
    private float ratio;
    private String label;
    private String exifDate;
    private String count;

    private int likes;

    public Photo(String fullFileName) {
        this.fullFileName = fullFileName;
        this.fileName = fullFileName.substring(fullFileName.lastIndexOf(File.separator)+1);
    }

    private boolean loaded = false;

    public void load() {

        if(loaded){return;}

        logger.info("Loading picture " + fullFileName);

        File file = new File(fullFileName);

        try {
            BufferedImage image = ImageIO.read(file);

            width = image.getWidth();
            height = image.getHeight();
            landscape = width > height;
            ratio = (float) width / (float) height;

            logger.info("Width: {}, Height: {}, landscape: {}, ratio: {}", width, height, landscape, ratio);
            imageAsB64 = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));

            Image xtimg = new Image(fullFileName);
            HashMap<Integer, Object> exif = xtimg.getExifTags();

            try{
                Metadata metadata = ImageMetadataReader.readMetadata(file);
                ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                // query the tag's value
                if(directory.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)){
                    this.label = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL).toString();

                    //int ori = directory.getInt(ExifSubIFDDirectory.ANG);
                }else{
                    this.label = this.fullFileName;
                }
            }catch(Exception ex){
                //failed to read the metadata so ignore it
                this.label = this.fullFileName;
            }

            //ExifSubIFDDirectory.TAG_IMAGE_HEIGHT
            //String dt = directory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            //if(directory.containsTag(ExifSubIFDDirectory.TAG_ORIENTATION)){
            //    int ori = directory.getInt(ExifSubIFDDirectory.TAG_ORIENTATION);
            //}
            //exifDate  = (String) exif.get(0x0132);
            //this.label = (this.exifDate==null ? "" :  " - " + this.exifDate);

            loaded = true;
        } catch (IOException e) {
            loaded = false;
            e.printStackTrace();
        }

    }

}
