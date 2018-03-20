package com.by.movx.utils;

import com.by.movx.entity.Film;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;
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

    public static Long getFileSize(Film film) throws Exception {
        return getFiles(film).collect(Collectors.summingLong(File::length));
    }

    private static BasicFileAttributes getFileAttributes(Film film) throws Exception {
        File file = getFile(film);
        if (file == null) return null;

        Path path = file.toPath();
        return Files.readAttributes(path, BasicFileAttributes.class);
    }

    public static File getFile(Film film) {
        return getFiles(film).findFirst().orElse(null);
    }

    private static Stream<File> getFiles(Film film) {

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
        Stream<File> found = findInFolder(folder, deque);
        if (found.count() > 0) {
            return found;
        }

        folder += "\\" + String.valueOf(10 * (where.getYear() / 10)) + "-е";
        found = findInFolder(folder, deque);
        if (found.count() > 0) {
            return found;
        }

        folder += "\\" + where.getYear();
        found = findInFolder(folder, deque);
        if (found.count() > 0) {
            return found;
        }

        return Stream.empty();
    }

    private static Stream<File> findInFolder(String folder, Deque<Film> deque) {
        Stream<File> found = Stream.empty();
        for (Film parent : deque) {
            found = findInDirectFolder(folder, parent);
            if (found.count() > 0 && found.findFirst().get().isDirectory()) {
                folder = found.findFirst().get().getAbsolutePath();
            }
            if (found.count() > 0 && !found.findFirst().get().isDirectory()) {
                return found;
            }
        }
        if(found.count() > 0l && found.findFirst().get().isDirectory()) {
            Stream<File> temp = findInDirectFolder(found.findFirst().get().getAbsolutePath(), deque.getLast());
            if (temp.count() > 0) return temp;
        }
        return found;
    }

    private static Stream<File> findInDirectFolder(String folder, Film film) {
        File f = new File(folder);

        return f.listFiles() == null ? Stream.empty(): Stream.of(f.listFiles())
                .filter(a ->
                                prepare(a.getName()).contains(prepare(film.getName())) ||
                                        (
                                                film.getEnName() != null &&
                                                        prepare(a.getName()).contains(prepare(film.getEnName()))
                                        )
                );
    }

    private static String prepare(String name) {
        return name.toLowerCase().replaceAll("ё", "е").replaceAll("[^0-9a-zа-я]", "");
    }
}
