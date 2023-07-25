package server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class FileIndexes  implements Serializable {
    private final HashMap<String, Integer> files;
    private final ArrayList<Integer> deletedIndexes;

    FileIndexes() {
        files = new HashMap<>();
        deletedIndexes = new ArrayList<>();
    }
    public int addFile(String file) {
        int index;
        if(deletedIndexes.size() == 0) {
            index = files.size();
            files.put(file, index);
        }
        else {
            index = deletedIndexes.get(0);
            files.put(file, index);
            deletedIndexes.remove(0);
        }
        return index;
    }
    public void deleteFile(String file) {
        if(files.containsKey(file)) {
            deletedIndexes.add(files.get(file));
            files.remove(file);
        }
    }
    public String findNameByIndex(int index) {
        for (String key : files.keySet()) {
            if (files.get(key) == index)
                return key;
        }
        return null;
    }
    public void printMap() {
        for (String name : files.keySet()) {
            String value = files.get(name).toString();
            System.out.println(name + " " + value);
        }
    }
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String name : files.keySet()) {
            String value = files.get(name).toString();
            str.append(name).append(" ").append(value).append("\n");
        }
        return str.toString();
    }
}