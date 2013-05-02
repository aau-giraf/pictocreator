package dk.aau.cs.giraf.pictocreator;

import dk.aau.cs.giraf.oasis.lib.*;
import dk.aau.cs.giraf.oasis.lib.controllers.*;
import dk.aau.cs.giraf.oasis.lib.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import android.content.Context;

/**
 * @author: croc
 */
public class StoragePictogram {
    private static final String TAG = "StoragePictogram";

    private String imagePath;
    private String textLabel;
    private String audioPath;
    private long author;
    private boolean publicPictogram;
    private List<String> tags; // tags added by the user which should be converted via generateTagList
    private HashSet<Tag> tagList;
    private HashSet<Tag> globalTags;
    private long pictogramID;
    private Context context;
    private Helper databaseHelper;

    public StoragePictogram(Context context){
        this.context = context;
    }

    public StoragePictogram(Context context, String imagePath, String textLabel, String audioPath){
        this.context = context;

        this.imagePath = imagePath;
        this.textLabel = textLabel;
        this.audioPath = audioPath;
    }

    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public void setTextLabel(String textLabel){
        this.textLabel = textLabel;
    }

    public void setAudioPath(String audioPath){
        this.audioPath = audioPath;
    }

    public void setAuthor(long author){
        this.author = author;
    }

    public String getImagePath(){
        return imagePath;
    }

    public String getTextLabel(){
        return textLabel;
    }

    public String getAudioPath(){
        return audioPath;
    }

    public long getAuthor(){
        return author;
    }

    public void addTag(String tag){
        tags.add(tag);
    }

    private Tag insertTag(String tag){
        Tag newTag = null;
        TagsHelper tagsHelper = databaseHelper.tagsHelper;
        boolean added = false;

        if(!globalTags.isEmpty()){
            for(Tag t : globalTags){
                if(t.getCaption().equals(tag)){
                    newTag = t;
                    added = true;
                    break;
                }
            }
        }

        if(!added){
            newTag = new Tag(tag);
            long tagId;
            tagId = tagsHelper.insertTag(newTag);
            newTag.setId(tagId);
        }

        return newTag;
    }

    private List<Tag> generateTagList(){
        List<Tag> addedTags = new ArrayList<Tag>();

        for(String tag : tags){
            Tag newTag = insertTag(tag);

            addedTags.add(newTag);
        }

        return addedTags;
    }

    private Media makeMedia(String path, String type){
        Media media = null;
        String[] validTypes = {"sound", "word", "image"};

        for(String t : validTypes){
            if(t.equalsIgnoreCase(type)){
                media = new Media(textLabel, path, publicPictogram, type, author);
                break;
            }
        }

        return media;
    }

    private Media insertMedia(Media media){
        MediaHelper mediaHelper = databaseHelper.mediaHelper;
        long mediaId;

        mediaId = mediaHelper.insertMedia(media);
        media.setId(mediaId);

        return media;
    }

    private Media generateMedia(){
        Media pictureMedia =  makeMedia(imagePath, "image");
        MediaHelper mediaHelper = databaseHelper.mediaHelper;

        pictureMedia = insertMedia(pictureMedia);
        pictogramID = pictureMedia.getId();

        if(!audioPath.equals("") && audioPath != null){
            // this part is pretty dumb:
            Media audioMedia = makeMedia(audioPath, "sound");
            audioMedia = insertMedia(audioMedia);

            mediaHelper.attachSubMediaToMedia(audioMedia, pictureMedia);
        }

        return pictureMedia;
    }

    public boolean addPictogram(){
        Media media;
        MediaHelper mediaHelper = databaseHelper.mediaHelper;
        boolean retVal = true;

        media = generateMedia();

        if(media != null){
            List<Tag> addTags = generateTagList();
            if(addTags != null && addTags.size() > 0){
                mediaHelper.addTagsToMedia(addTags, media);
            }
        } else {
            retVal = false;
        }

        return retVal;
    }
}
