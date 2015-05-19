package dk.aau.cs.giraf.pictocreator;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import dk.aau.cs.giraf.dblib.models.Department;
import dk.aau.cs.giraf.dblib.models.DepartmentPictogram;
import dk.aau.cs.giraf.pictocreator.audiorecorder.AudioHandler;
import dk.aau.cs.giraf.pictocreator.canvas.DrawStackSingleton;
import dk.aau.cs.giraf.pictocreator.management.ByteConverter;
import dk.aau.cs.giraf.pictogram.TextToSpeech;
import dk.aau.cs.giraf.dblib.models.Pictogram;
import dk.aau.cs.giraf.dblib.models.Profile;
import dk.aau.cs.giraf.dblib.Helper;
import dk.aau.cs.giraf.dblib.controllers.TagController;
import dk.aau.cs.giraf.dblib.controllers.PictogramController;
import dk.aau.cs.giraf.dblib.controllers.ProfilePictogramController;
import dk.aau.cs.giraf.dblib.controllers.PictogramTagController;
import dk.aau.cs.giraf.dblib.models.Tag;
import dk.aau.cs.giraf.dblib.models.ProfilePictogram;
import dk.aau.cs.giraf.dblib.models.PictogramTag;

/**
 * Class for storage of pictograms
 *
 * @author: croc
 */
public class StoragePictogram {
    private static final String TAG = "StoragePictogram";

    private String pictogramName;
    private String inlineTextLabel;
    private String imagePath;
    private File audioFile;
    private long author;
    private boolean isPublic;
    private long pictogramID;
    private List<String> tags = new ArrayList<String>(); // tags added by the user which should be converted via generateTagList
    private List<Profile> citizens = new ArrayList<Profile>();

    private Context context;
    private Helper databaseHelper;

    /**
     * Constructor for the class
     *
     * @param context The context in which the StoragePictogram is created
     */
    public StoragePictogram(Context context) {
        this.context = context;
        try {
            databaseHelper = new Helper(this.context);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Constructor for the class
     *
     * @param context       The context in which the StoragePictogram is created
     * @param imagePath     Path to the image-file used by the pictogram
     * @param pictogramName The text-label/name of the pictogram
     * @param audioFile     The audio-file used by the pictogram
     */
    public StoragePictogram(Context context, String imagePath, String pictogramName, String inlineTextLabel, File audioFile) {
        this.context = context;
        try {
            databaseHelper = new Helper(this.context);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        this.imagePath = imagePath;
        this.pictogramName = pictogramName;
        this.inlineTextLabel = inlineTextLabel;
        this.audioFile = audioFile;
    }

    /**
     * Setter for the imagePath variable
     *
     * @param imagePath The imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Setter for the pictogramName variable
     *
     * @param pictogramName TextLabel to set
     */
    public void setPictogramName(String pictogramName) {
        this.pictogramName = pictogramName;
    }


    /**
     * Setter for the inlineTextLabel variable
     *
     * @param inlineTextLabel inlineTextLabel to set
     */
    public void setInlineTextLabel(String inlineTextLabel) {
        this.inlineTextLabel = inlineTextLabel;
    }

    /**
     * Setter for the audioFile
     *
     * @param audioFile The audioFile to set
     */
    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * Setter for the author variable
     *
     * @param author The author to set
     */
    public void setAuthor(long author) {
        this.author = author;
    }


    public void setIsPublic(boolean b) {
        this.isPublic = b;
    }

    /**
     * Getter for the pictogramID variable
     *
     * @return The pictogramID
     */
    public long getId() {
        return pictogramID;
    }

    /**
     * Getter for the imagePath variable
     *
     * @return The imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Getter for the pictogramName variable
     *
     * @return The pictogramName
     */
    public String getPictogramName() {
        return pictogramName;
    }

    /**
     * Getter for the audioFile variable
     *
     * @return The audioFile
     */
    public File getAudioFile() {
        return audioFile;
    }

    /**
     * Getter for the author variable
     *
     * @return The author
     */
    public long getAuthor() {
        return author;
    }

    /**
     * Function for adding tags to the pictogram
     *
     * @param tag The tag to add
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Function for adding citizen profiles to a private pictogram
     *
     * @param profile
     */
    public void addCitizen(Profile profile) {
        citizens.add(profile);
    }

    /**
     * Creates a tag given a string, and inserts it to the database
     *
     * @param tag The tag to create and insert
     * @return The newly created and inserted Tag
     */
    private Tag insertTag(String tag) {
        TagController tagsHelper = databaseHelper.tagsHelper;
        Tag newTag = new Tag(tag);
        long tagId = tagsHelper.insert(newTag);
        newTag.setId(tagId);

        return newTag;
    }

    /**
     * Method for generating the list of tags associated with the pictogram
     *
     * @return The generated list of tags
     */
    private List<Tag> generateTagList() {
        List<Tag> addedTags = new ArrayList<Tag>();

        //adds tags to the database and gets their ID
        //IDs are used to create relation between tag and pictogram
        for (String tag : tags) {
            Tag newTag = insertTag(tag);
            addedTags.add(newTag);
        }

        return addedTags;
    }


    /**
     * Method for generation of image given a path
     *
     * @param path The path to use for the image
     * @return The newly generated image
     */
    private Pictogram makeImage(String path) {
        Pictogram pictogram = new Pictogram();

        Bitmap pictogramImage = BitmapFactory.decodeFile(path);
        pictogramImage = Bitmap.createScaledBitmap(pictogramImage, 283, 283, true);

        pictogram.setImage(pictogramImage);
        pictogram.setName(pictogramName);
        pictogram.setIsPublic(isPublic);
        pictogram.setInlineText(inlineTextLabel);
        pictogram.setAuthor(author);

        return pictogram;
    }

    /**
     * Method for inserting pictogram into the database
     * @param pictogram The pictogram to insert
     * @return the newly inserted pictogram
     */
    private Pictogram insertPictogram(Pictogram pictogram) {
        PictogramController pictogramHelper = databaseHelper.pictogramHelper;
        
        pictogramID = pictogramHelper.insert(pictogram);
        pictogram.setId(pictogramID);

        return pictogram;
    }

    /**
     * Method for generation of pictogram
     *
     * @return The generated pictogram
     */
    private Pictogram generatePictogram() {
        Pictogram pictogram = makeImage(imagePath);

        generateAudio(pictogram);
        generateEditableImage(pictogram);

        return pictogram;
    }

    /**
     * Method for generation of audio for the pictogram
     *
     * @param pictogram The generated pictogram
     */
    private void generateAudio(Pictogram pictogram) {
        if (audioFile != null) {
            byte[] byteArray = new byte[(int) audioFile.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(audioFile);
                fileInputStream.read(byteArray);
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Audio file not found: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Could not convert audio file to byte array: " + e.getMessage());
            }
            pictogram.setSoundDataBytes(byteArray);
        } else {
            TextToSpeech textToSpeech = new TextToSpeech(this.context);
            if (textToSpeech.NoSound(pictogram)) {
                try {
                    if (AudioHandler.getFinalPath() == null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
                        String date = dateFormat.format(new Date());
                        AudioHandler.setFinalPath(context.getCacheDir().getPath() + File.separator + date);
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(AudioHandler.getFinalPath());
                    fileOutputStream.write(pictogram.getSoundData());
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    /**
     * Method for generating editable image for the pictogram
     *
     * @param pictogram
     */
    private void generateEditableImage(Pictogram pictogram) {
        if (DrawStackSingleton.getInstance().getSavedData() != null) {
            try {
                pictogram.setEditableImage(ByteConverter.serialize(DrawStackSingleton.getInstance().getSavedData()));
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        insertPictogram(pictogram);
    }

    /**
     * Method for adding a pictogram to the database
     *
     * @return True if the pictogram was successfully added, false otherwise
     */
    public boolean addPictogram(int loadedPictogramId) {
        if (author == 0)
            return false;

        Pictogram pictogram;
        PictogramTagController tagHelper = databaseHelper.pictogramTagHelper;
        ProfilePictogramController profileHelper = databaseHelper.profilePictogramHelper;

        pictogram = generatePictogram(); // Creates pictogram and inserts it into the database

        if (pictogram != null) {
            List<Tag> addTags = generateTagList();
            for (Tag t : addTags) {
                tagHelper.insert(new PictogramTag(pictogram.getId(), t.getId()));
            }

            for (Profile p : citizens) {
                profileHelper.insert(new ProfilePictogram(p.getId(), pictogram.getId()));
            }
            return true;
        }

        return false;
    }

    private boolean saveDepartmentPictogram(Pictogram pictogram) {
        try {
            Profile user = databaseHelper.profilesHelper.getById(author);
            long departmentId = user.getDepartmentId();
            DepartmentPictogram dp = new DepartmentPictogram(pictogram.getId(), departmentId);
            databaseHelper.departmentPictogramHelper.insert(dp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
