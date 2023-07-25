package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import client.Message;
public class SerializeObject {
    public static void serializeInFile (Object obj, Path dir, String filename) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dir.toString() + java.io.File.separator + filename);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch  (IOException e){
            e.printStackTrace();
        }
    }
    public static void sendFile (Message msg, Path path) {
        File file = new File(path.toString() + java.io.File.separator + msg.filename);
        if(file.exists()) {
            try {
                msg.content = Files.readAllBytes(Path.of(path + java.io.File.separator + msg.filename));
            } catch (IOException e) {
                System.out.println("Exception during file reading.");
                System.exit(0);
            }
        } else {
            msg.response = "The response says that this file is not found!";
        }
    }
    public static boolean downloadFile (String type, Message msg, Path path) {
        if (type.equals("client")) {
            msg.newName = askForName();
        }
        File newFile = new File(path.toString() + java.io.File.separator + msg.newName);
        try {
            if (!newFile.createNewFile()) {
                msg.newName = askForName();
                return false;
            }
            if (msg.content != null) {
                try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(Path.of(String.valueOf(newFile))))) {
                    out.write(msg.content, 0, msg.content.length);
                } catch (IOException e) {
                    //System.out.println("Cannot save file: " + e.getMessage());
                    return false;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception during file creation");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private static String askForName() {
        Scanner scan = new Scanner(System.in);
        System.out.print("The file was downloaded! ");
        String res = "";
        while (res.equals("")) {
            System.out.print("Specify a name for it: ");
            res = scan.nextLine();
        }
        scan.close();
        return res;
    }
    public static Object bytesToObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream is = new ObjectInputStream(in);
            obj = is.readObject();
            in.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return obj;
    }
}