package dk.aau.cs.giraf.pictocreator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramTagController;
import dk.aau.cs.giraf.oasis.lib.controllers.TagController;
import dk.aau.cs.giraf.oasis.lib.models.Pictogram;
import dk.aau.cs.giraf.oasis.lib.models.PictogramTag;
import dk.aau.cs.giraf.oasis.lib.models.Tag;

/**
 * Class for storage of pictograms
 *
 * @author: croc
 */
public class StoragePictogram {
    private static final String TAG = "StoragePictogram";

    private String imagePath;
    private String textLabel;
    private String audioPath;
    private long author;
    private boolean publicPictogram;
    private List<String> tags = new ArrayList<String>(); // tags added by the user which should be converted via generateTagList
    private HashSet<Tag> tagList = new HashSet<Tag>();
    private HashSet<Tag> globalTags = new HashSet<Tag>();
    private long pictogramID;
    private Context context;
    private Helper databaseHelper;

    /**
     * Constructor for the class
     * @param context The context in which the StoragePictogram is created
     */
    public StoragePictogram(Context context){
        this.context = context;
        this.databaseHelper = new Helper(this.context);
    }

    /**
     * Constructor for the class
     * @param context The context in which the StoragePictogram is created
     * @param imagePath Path to the image-file used by the pictogram
     * @param textLabel The text-label/name of the pictogram
     * @param audioPath Path to the audio-file used by the pictogram
     */
    public StoragePictogram(Context context, String imagePath, String textLabel, String audioPath){
        this.context = context;

        this.databaseHelper = new Helper(this.context);

        this.imagePath = imagePath;
        this.textLabel = textLabel;
        this.audioPath = audioPath;
    }

    /**
     * Setter for the imagePath variable
     * @param imagePath The imagePath to set
     */
    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    /**
     * Setter for the textLabel variable
     * @param textLabel TextLabet to set
     */
    public void setTextLabel(String textLabel){
        this.textLabel = textLabel;
    }

    /**
     * Setter for the audioPath
     * @param audioPath The audioPath to set
     */
    public void setAudioPath(String audioPath){
        this.audioPath = audioPath;
    }

    /**
     * Setter for the author variable
     * @param author The author to set
     */
    public void setAuthor(long author){
        this.author = author;
    }

    /**
     * Getter for the pictogramID variable
     * @return The pictogramID
     */
    public long getId(){
        return pictogramID;
    }

    /**
     * Getter for the imagePath variable
     * @return The imagePath
     */
    public String getImagePath(){
        return imagePath;
    }

    /**
     * Getter for the textLabel variable
     * @return The textLabel
     */
    public String getTextLabel(){
        return textLabel;
    }

    /**
     * Getter for the audioPath variable
     * @return The audioPath
     */
    public String getAudioPath(){
        return audioPath;
    }

    /**
     * Getter for the author variable
     * @return The author
     */
    public long getAuthor(){
        return author;
    }

    /**
     * Function for adding tags to the pictogram
     * @param tag The tag to add
     */
    public void addTag(String tag){
        tags.add(tag);
    }

    /**
     * Creates a tag given a string, and inserts it to the database
     * @param tag The tag to create and insert
     * @return The newly created and inserted Tag
     */
    private Tag insertTag(String tag){
        Tag newTag = null;
        TagController tagsHelper = databaseHelper.tagsHelper;
        boolean added = false;

        if(!globalTags.isEmpty()){
            for(Tag t : globalTags){
                if(t.getName().equals(tag)){
                    newTag = t;
                    added = true;
                    break;
                }
            }
        }

        if(!added){
            newTag = new Tag(tag);
            int tagId;
            tagId = tagsHelper.insertTag(newTag);
            newTag.setId(tagId);
        }

        return newTag;
    }

    /**
     * Method for generating the list of tags associated with the pictogram
     * @return The generated list of tags
     */
    private List<Tag> generateTagList(){
        List<Tag> addedTags = new ArrayList<Tag>();

        if(tags.size() > 0){
            for(String tag : tags){
                Tag newTag = insertTag(tag);

                addedTags.add(newTag);
            }
        }

        return addedTags;
    }

    /**
     * Method for generation of media given a path and type
     * @param path The path to use for the media
     * @param type The type of the media
     * @return The newly generated Media
     */
    private Pictogram makeMedia(String path, String type){
        Pictogram media = null;
        String[] validTypes = {"sound", "word", "image"};

        for(String t : validTypes){
            if(t.equalsIgnoreCase(type)){
                media = new Pictogram();
                //media = new Media(textLabel, path, publicPictogram, type, author);
                break;
            }
        }

        return media;
    }

    /**
     * Method for inserting Media into the database
     * @param media The media to insert
     * @return the newly inserted Media
     */
    private Pictogram insertMedia(Pictogram media){
        PictogramController pictogramHelper = databaseHelper.pictogramHelper;
        int mediaId;

        mediaId = pictogramHelper.insertPictogram(media);
        media.setId(mediaId);

        return media;
    }

    /**
     * Method for generation of image and sound media
     * @return The generated Media
     */
    private Pictogram generateMedia(){
        Pictogram pictureMedia =  makeMedia(imagePath, "image");
        PictogramController pictogramHelper = databaseHelper.pictogramHelper;

        pictureMedia = insertMedia(pictureMedia);
        pictogramID = pictureMedia.getId();

        if(!audioPath.equals("") && audioPath != null){
            // this part is pretty dumb:
            Pictogram audioMedia = makeMedia(audioPath, "sound");
            audioMedia = insertMedia(audioMedia);

            //pictogramHelper.attachSubMediaToMedia(audioMedia, pictureMedia);
        }

        return pictureMedia;
    }

    /**
     * Method for adding a pictogram to the database
     * @return True if the pictogram was successfully added, false otherwise
     */
    public boolean addPictogram(){
        Pictogram media;
        PictogramTagController pictogramHelper = databaseHelper.pictogramTagHelper;
        boolean retVal = false;

        media = generateMedia();

        if(media != null){
            List<Tag> addTags = generateTagList();
            if(addTags.size() > 0){
                //pictogramHelper.addTagsToMedia(addTags, media);
            }
            retVal = true;
        }

        return retVal;
    }
}
