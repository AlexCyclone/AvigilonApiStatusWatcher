package icu.cyclone.avigilon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public class FileUtils {
    public static <T extends Serializable> void saveList(String path, List<T> list) throws IOException {
        if (list == null) {
            list = Collections.emptyList();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFile(path)))) {
            oos.writeObject(list);
            oos.flush();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> readList(String path) throws IOException {
        File file = getFile(path);
        if (file.length() == 0) {
            return Collections.emptyList();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            try {
                return (List<T>) ois.readObject();
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new IOException("Incorrect file format");
            }
        }
    }

    private static File getFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("File \"" + path + "\" not created");
        } else if (!file.isFile()) {
            throw new IOException("Expected file \"" + path + "\" but directory found");
        }
        return file;
    }
}
