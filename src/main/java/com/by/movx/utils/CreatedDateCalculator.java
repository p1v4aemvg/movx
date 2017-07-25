package com.by.movx.utils;

import com.by.movx.entity.Film;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.OffsetDateTime;
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

        Film where = film.getParent() != null ?  film.getParent() : film;

        String folder = BASE_DIR + film.getType().getName() + "\\" + where.getDuration().getDescription();
        File found = findInFolder(folder, film);
        if (found != null) {
            return found;
        }

        folder += "\\" + String.valueOf(10 * (where.getYear() / 10)) + "-е";
        found = findInFolder(folder, film);
        if (found != null) {
            return found;
        }

        folder += "\\" + where.getYear();
        found = findInFolder(folder, film);
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


    private static File findInDirectFolder(String folder, Film film) {
        File f = new File(folder);
        if (f.listFiles() == null) return null;

        File itself = Stream.of(f.listFiles())
                .filter(a -> prepare(a.getName()).contains(prepare(film.getName())) ||
                        (film.getEnName() != null && prepare(a.getName()).contains(prepare(film.getEnName()))))
                .findFirst().orElse(null);
        if (itself != null) return itself;

        File season = Stream.of(f.listFiles())
                .filter(File::isDirectory)
                .filter(a -> (a.getName().contains(film.getYear().toString())) &&
                        (prepare(a.getName()).contains("season") || prepare(a.getName()).contains("сезон")))
                .findFirst().orElse(null);
        if (season != null) {
            return findInDirectFolder(folder + "\\" + season.getName(), film);
        }

        return null;
    }

    private static String prepare(String name) {
        return name.toLowerCase().replaceAll("ё", "е").replaceAll("[^0-9a-zа-я]", "");
    }
}
