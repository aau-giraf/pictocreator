package dk.aau.cs.giraf.pictocreator;

import dk.aau.cs.giraf.oasis.lib.*;
import dk.aau.cs.giraf.oasis.lib.controllers.*;
import dk.aau.cs.giraf.oasis.lib.models.*;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

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
    private List<Tag> tagList;
    private List<Tag> globalTags;
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

    // public setImagePath(String imagePath);
    // public setTextLabel(String setTextLabel);
    // public setAudioPath(String audioPath);
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
            addedTags.add(insertTag(tag));
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

        media = generateMedia();
        if(media != null){}
        return true;
    }
}
