package server;

import client.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class FileStorage {
    private FileIndexes indexes;
    private final Path path;
    private final String saveFile;
    private final Path forSerialize;

    public FileStorage() {
        saveFile = "files.bin";
        path = Path.of(System.getProperty("user.dir"), "src", "server", "data");
        forSerialize = Path.of(System.getProperty("user.dir"), "src", "server", "serialization");
        new File(path.toString());
        new File(forSerialize.toString());
        indexes = DeserializeObject.deserializeObj(forSerialize + java.io.File.separator + saveFile);
        if (indexes == null) {
            indexes = new FileIndexes();
        }
    }
    public Message parseRequest(Message msg) {
        Message response = new Message();
        if (msg.cmd.equals("exit")) {
            return (response);
        }
        if (msg.cmd.equals("GET")) {
            return (getFile(msg, response));
        }
        else if (msg.cmd.equals("DELETE")) {
            return (deleteFile(msg, response));
        } else {
            return (putFile(msg, response));
        }
    }
    private Message putFile (Message msg, Message  response) {
        if (msg.newName == null || msg.newName.equals("")) {
            msg.newName = generateRandomName();
            if (msg.filename.contains(".")) {
                msg.newName += msg.filename.substring(msg.filename.indexOf('.'));
            } else {
                msg.newName += ".file";
            }
        }
        if (SerializeObject.downloadFile("server", msg, path))
            response.response = "Response says that file is saved! ID = " + indexes.addFile(msg.newName);
        else
            response.response = "Response says that creating the file was forbidden!\n";
        return response;
    }
    private String generateRandomName() {
        Random rng = new Random();
        String availableChars = "abcdefghijklmnopqrstuvwxyz1234567890";
        char[] text = new char[5];
        for (int i = 0; i < 5; i++)
        {
            text[i] = availableChars.charAt(rng.nextInt(availableChars.length()));
        }
        return new String(text);
    }
    private Message deleteFile (Message msg, Message response) {
        if (msg.nameOrId == 2) {
            msg.filename = indexes.findNameByIndex(msg.id);
            if (msg.filename == null) {
                response.response = "The response says that this file is not found!\n";
                return response;
            }
        }
        try {
            if (Files.deleteIfExists(Path.of(path.toString() + java.io.File.separator + msg.filename))) {
                indexes.deleteFile(msg.filename);
                response.response = "Response says that the file is successfully deleted!";
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.response = "The response says that this file is not found!";
        return response;
    }
    private Message getFile (Message msg, Message response) {
        if (msg.nameOrId == 2) {
            response.filename = indexes.findNameByIndex(msg.id);
            if (msg.filename == null) {
                response.response = "The response says that this file is not found!\n";
                return response;
            }
        } else {
            response.filename = msg.filename;
        }
        response.response = "";
        SerializeObject.sendFile(response, path);
        return response;
    }

    public void saveFiles() {
        SerializeObject.serializeInFile(indexes, forSerialize, saveFile);
    }

}