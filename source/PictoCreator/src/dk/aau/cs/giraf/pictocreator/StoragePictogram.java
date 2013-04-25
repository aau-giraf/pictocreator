package dk.aau.cs.giraf.pictocreator;

import dk.aau.cs.giraf.oasis.lib.*;
import dk.aau.cs.giraf.oasis.lib.controllers.*;
import dk.aau.cs.giraf.oasis.lib.models.*;

import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<String> tags;
    private ArrayList<Tag> allTags;
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

    private List<Tag> generateTagList(){
        ArrayList<Tag> addedTags = new ArrayList<Tag>();
        TagsHelper tagsHelper = databaseHelper.tagsHelper;
        Boolean added;
        if(!tags.isEmpty()){
            if(!allTags.isEmpty())
                for(String tag : tags){
                    added = false;
                    for(Tag t : allTags){
                        if(t.getCaption().equals(tag)){
                            addedTags.add(t);
                            added = true;
                            break;
                        }
                    }


                if(!added){
                    Tag newTag = new Tag(tag);
                    long tagId;
                    tagId = tagsHelper.insertTag(newTag);
                    newTag.setId(tagId);

                    addedTags.add(newTag);
                }
            }
        }

        return addedTags;
    }

    private Media insertMedia(Media media){
        MediaHelper mediaHelper = databaseHelper.mediaHelper;
        int mediaId;

        mediaId = mediaHelper.insertMedia(media);
        media.setId(mediaId);

        return media;
    }

    private Media generateMedia(){
        //public Media(String name, String mPath, boolean mPublic, String mType, long ownerId) {
        Media pictureMedia = new Media(textLabel, imagePath, publicPictogram, "picture", author);
                MediaHelper mediaHelper = databaseHelper.mediaHelper;

        pictureMedia = insertMedia(pictureMedia);
        mediaHelper.insertMedia(pictureMedia);

        if(!audioPath.equals("") && audioPath != null){
            // this part is fucking stupid:
            Media audioMedia = new Media(textLabel, audioPath, publicPictogram, "sound", author);
            mediaHelper.insertMedia(audioMedia);
        }

        return pictureMedia;
    }

    public Boolean addPictogram(){
        return null;
    }
}
