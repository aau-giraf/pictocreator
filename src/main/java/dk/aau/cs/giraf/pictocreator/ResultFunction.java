package dk.aau.cs.giraf.pictocreator;


/**
 * Used for controlling which activity's/fragment's onActivityResult gets called
 */
public enum ResultFunction {
    NOTFORUS(-1), LOADIMAGE(0), LOADPICTOINFO(1);

    //The user defined requestcode starts at 1
    public static final int REQUEST_FIRST_USER = 1;
    private static final ResultFunction[] values = ResultFunction.values();

    private int offset;

    ResultFunction(int offset) {
        this.offset = offset;
    }

    public int getRequestCode() {
        return offset + REQUEST_FIRST_USER;
    }

    public static ResultFunction fromRequestCode(int requestCode) {
        //The 16 most significant bits of the requestCode are the fragment index
        //We should ignore those
        if ((requestCode & 0xFFFF0000) != 0)
            return NOTFORUS;

        if (requestCode < 0 || requestCode >= values.length)
            throw new IllegalArgumentException("Unknown request code");

        return values[requestCode];
    }

    public static ResultFunction fromRequestCodeIgnoreReceiver(int requestCode) {
        return fromRequestCode(requestCode & 0xFFFF);
    }
}
