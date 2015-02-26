package dk.aau.cs.giraf.pictocreator.management;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is used to convert serializes objects to byte array and vice versa.
 * @author SW608f14
 */
public final class ByteConverter {
    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(byteArrayOutput);
        objectOutput.writeObject(object);
        return byteArrayOutput.toByteArray();
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(data);
        ObjectInputStream objectInput = new ObjectInputStream(byteArrayInput);
        return objectInput.readObject();
    }
}
