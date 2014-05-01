package dk.aau.cs.giraf.pictocreator;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.io.*;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramTagController;
import dk.aau.cs.giraf.oasis.lib.controllers.TagController;
import dk.aau.cs.giraf.oasis.lib.models.Pictogram;
import dk.aau.cs.giraf.oasis.lib.models.PictogramTag;
import dk.aau.cs.giraf.oasis.lib.models.Tag;
import dk.aau.cs.giraf.pictogram.tts;

/**
 * Class for storage of pictograms
 *
 * @author: croc
 */
public class StoragePictogram {
    private static final String TAG = "StoragePictogram";

    private String imagePath;
    private String textLabel;
    private String inlineTextLabel;
    private File audioFile;
    private int author;
    private int publicPictogram = 1; //temporary since we do not have a checkbox implemented for this
    private List<String> tags = new ArrayList<String>(); // tags added by the user which should be converted via generateTagList
    private int pictogramID;
    private Context context;
    private Helper databaseHelper;
    private byte[] drawStack;

    /**
     * Constructor for the class
     * @param context The context in which the StoragePictogram is created
     */
    public StoragePictogram(Context context){
        this.context = context;
        try{
            this.databaseHelper = new Helper(this.context);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Constructor for the class
     * @param context The context in which the StoragePictogram is created
     * @param imagePath Path to the image-file used by the pictogram
     * @param textLabel The text-label/name of the pictogram
     * @param audioFile The audio-file used by the pictogram
     */
    public StoragePictogram(Context context, String imagePath, String textLabel, String inlineTextLabel, File audioFile){
        this.context = context;
        try{
            this.databaseHelper = new Helper(this.context);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        this.imagePath = imagePath;
        this.textLabel = textLabel;
        this.inlineTextLabel = inlineTextLabel;
        this.audioFile = audioFile;
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
     * @param textLabel TextLabel to set
     */
    public void setTextLabel(String textLabel){
        this.textLabel = textLabel;
    }


    /**
     * Setter for the inlineTextLabel variable
     * @param inlineTextLabel inlineTextLabel to set
     */
    public void setinlineTextLabel(String inlineTextLabel){
        this.inlineTextLabel = inlineTextLabel;
    }

    /**
     * Setter for the audioFile
     * @param audioFile The audioFile to set
     */
    public void setAudioFile(File audioFile){
        this.audioFile = audioFile;
    }

    /**
     * Setter for the author variable
     * @param author The author to set
     */
    public void setAuthor(int author){
        this.author = author;
    }

    /**
     * Setter for the publicPictogram variable
     * @param publicPictogram The publicPictogram to set
     */
    public void setpublicPictogram(int publicPictogram){
        this.publicPictogram = publicPictogram;
    }

    /**
     * Getter for the pictogramID variable
     * @return The pictogramID
     */
    public int getId(){
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
     * Getter for the audioFile variable
     * @return The audioFile
     */
    public File getAudioFile(){
        return audioFile;
    }

    /**
     * Getter for the author variable
     * @return The author
     */
    public int getAuthor(){
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

        newTag = new Tag(tag);
        int tagId;
        tagId = tagsHelper.insertTag(newTag);
        newTag.setId(tagId);

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
     * Method for generation of image given a path
     * @param path The path to use for the image
     * @return The newly generated image
     */
    private Pictogram makeImage(String path){
        Pictogram pictogram = new Pictogram();

        pictogram.setImage(BitmapFactory.decodeFile(path));
        pictogram.setName(textLabel);
        pictogram.setPub(publicPictogram);
        pictogram.setInlineText(inlineTextLabel);
        pictogram.setAuthor(author);

        return pictogram;
    }

    /**
     * Method for inserting pictogram into the database
     * @param pictogram The pictogram to insert
     * @return the newly inserted pictogram
     */
    private Pictogram insertPictogram(Pictogram pictogram){
        PictogramController pictogramHelper = databaseHelper.pictogramHelper;

        pictogramID = pictogramHelper.insertPictogram(pictogram);
        pictogram.setId(pictogramID);

        return pictogram;
    }

    public void setEditableImage(byte[] drawstack){
       this.drawStack = drawstack;
    }

    /**
     * Method for generation of pictogram
     * @return The generated pictogram
     */
    private Pictogram generatePictogram(){
        Pictogram pictogram =  makeImage(imagePath);

        if(audioFile != null){
            byte[] b = new byte[(int) audioFile.length()];
            try{
                FileInputStream fileInputStream = new FileInputStream(audioFile);
                fileInputStream.read(b);
                fileInputStream.close();
            }catch (FileNotFoundException e) {
                Log.e(TAG, "Audio file not found " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Could not convert audio file to byte array " + e.getMessage());
            }
            pictogram.setSoundDataBytes(b);
        }
        else{
            tts t = new tts(this.context);
            t.NoSound(pictogram);
        }

        if(this.drawStack != null){
            pictogram.setEditableImage(drawStack);
        }
        pictogram = insertPictogram(pictogram);

        return pictogram;
    }

    /**
     * Method for adding a pictogram to the database
     * @return True if the pictogram was successfully added, false otherwise
     */
    public boolean addPictogram(){
        Pictogram pictogram;
        PictogramTagController tagHelper = databaseHelper.pictogramTagHelper;
        boolean retVal = false;

        pictogram = generatePictogram();

        if(pictogram != null){
            List<Tag> addTags = generateTagList();
            if(addTags.size() > 0){
                for(Tag t : addTags)
                {
                    tagHelper.insertPictogramTag(new PictogramTag(pictogram.getId(), t.getId()));
                }
            }
            retVal = true;
        }

        return retVal;
    }
}
