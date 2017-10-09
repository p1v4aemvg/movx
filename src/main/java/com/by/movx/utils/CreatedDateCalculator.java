package com.by.movx.utils;

import com.by.movx.entity.Film;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Stream;

/**
 * Created by movx
 * on 27.11.2016.
 */
public class CreatedDateCalculator {

    private static final String BASE_DIR = "J:\\Video\\";

    public static Long getCreatedAt(Film film) throws Exception {
        File file = getFile(film);
        if (file == null) return null;

        Path path = file.toPath();
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

        return attr.creationTime().toMillis();
    }

    public static File getFile(Film film) {

        Deque<Film> deque = new ArrayDeque<>();
        deque.addLast(film);

        while(!CollectionUtils.isEmpty(film.getChildren())) {
            film = film.getChildren().iterator().next();
            deque.addLast(film);
        }

        Film where = film;
        while (where.getParent() != null) {
            where = where.getParent();
            deque.addFirst(where);
        }

        String folder = BASE_DIR + film.getType().getName() + "\\" + where.getDuration().getDescription();
        File found = findInFolder(folder, deque);
        if (found != null) {
            return found;
        }

        folder += "\\" + String.valueOf(10 * (where.getYear() / 10)) + "-е";
        found = findInFolder(folder, deque);
        if (found != null) {
            return found;
        }

        folder += "\\" + where.getYear();
        found = findInFolder(folder, deque);
        if (found != null) {
            return found;
        }

        return null;
    }

    private static File findInFolder(String folder, Film film) {
        if (film.getParent() != null) {
            Film parent = film.getParent();
            File parentFolder = findInDirectFolder(folder, parent);
            if (parentFolder != null && parentFolder.isDirectory()) {
                File temp = findInDirectFolder(parentFolder.getAbsolutePath(), film);
                if(temp != null) return temp;
            }
            return parentFolder;
        } else {
            File filmFile = findInDirectFolder(folder, film);
            if (filmFile != null && filmFile.isDirectory()) {
                File temp = findInDirectFolder(filmFile.getAbsolutePath(), film);
                if (temp != null) return temp;
            }
            return filmFile;
        }
    }

    private static File findInFolder(String folder, Deque<Film> deque) {
        File found = null;
        for (Film parent : deque) {
            found = findInDirectFolder(folder, parent);
            if (found != null && found.isDirectory()) {
                folder = found.getAbsolutePath();
            }
            if (found != null && !found.isDirectory()) {
                return found;
            }
        }
        if(found != null && found.isDirectory()) {
            File temp = findInDirectFolder(found.getAbsolutePath(), deque.getLast());
            if (temp != null) return temp;
        }
        return found;
    }

    private static File findInDirectFolder(String folder, Film film) {
        File f = new File(folder);
        if (f.listFiles() == null)
            return null;

        return Stream.of(f.listFiles())
                .filter(a ->
                                prepare(a.getName()).contains(prepare(film.getName())) ||
                          (
                                film.getEnName() != null &&
                                prepare(a.getName()).contains(prepare(film.getEnName()))
                          )
                )
                .findFirst().orElse(null);
    }

    private static String prepare(String name) {
        return name.toLowerCase().replaceAll("ё", "е").replaceAll("[^0-9a-zа-я]", "");
    }
}
