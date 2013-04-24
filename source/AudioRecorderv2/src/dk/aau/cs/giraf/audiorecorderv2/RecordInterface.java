package dk.aau.cs.giraf.audiorecorderv2;

/**
 * Interface for binding the recording thread, the activity and the decibelmeter
 * @author Croc
 *
 */
public interface RecordInterface {

	/**
	 * Function for updating the value for the decibelmeter
	 * @param dbValue The value to updating the decibelmeter
	 */
    public void decibelUpdate(double dbValue);

}
