package server;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class DeserializeObject {
    public static FileIndexes deserializeObj (String filename) {
        try {
            FileIndexes res;
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            res = (FileIndexes) ois.readObject();
            ois.close();
            return res;
        }
        catch (Exception e) {
            e.getLocalizedMessage();
        }
        return null;
    }
}