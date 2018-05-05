package com.by.movx.utils;

import com.by.movx.entity.Film;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
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
        return getFiles(film, false).stream()
                .filter(matches(film))
                .collect(Collectors.summingLong(FileUtils::sizeOf));
    }

    public static File getFile(Film film) {
        return getFiles(film, true).stream().filter(f -> !f.isDirectory()).findFirst().orElse(null);
    }

    private static List<File> getFiles(Film film, boolean deep) {

        Deque<Film> deque = new ArrayDeque<>();
        deque.addLast(film);
        Film where = film;

        while(deep && !CollectionUtils.isEmpty(film.getChildren())) {
            film = film.getChildren().iterator().next();
            deque.addLast(film);
        }

        while (where.getParent() != null) {
            where = where.getParent();
            deque.addFirst(where);
        }

        String folder = BASE_DIR + film.getType().getName() + "\\" + where.getDuration().getDescription();
        List<File> found = findInFolder(folder, deque, deep);
        if (found.size() > 0) {
            return found;
        }

        folder += "\\" + String.valueOf(10 * (where.getYear() / 10)) + "-е";
        found = findInFolder(folder, deque, deep);
        if (found.size() > 0) {
            return found;
        }

        folder += "\\" + where.getYear();
        found = findInFolder(folder, deque, deep);
        if (found.size() > 0) {
            return found;
        }

        return new ArrayList<>();
    }

    private static List<File> findInFolder(String folder, Deque<Film> deque, boolean deep) {
        List<File> found = new ArrayList<>();
        String latestNotNull = null;
        for (Film parent : deque) {
            found = findInDirectFolder(folder, parent);
            if (found.size() > 0 && found.stream().allMatch(File::isDirectory)) {
                folder = found.get(0).getAbsolutePath();
                latestNotNull = folder;
            }
            if (found.size() > 0 && found.stream().anyMatch(f -> !f.isDirectory())) {
                return found;
            }
        }
        if(deep && found.size() > 0 && found.stream().allMatch(File::isDirectory)) {
            List<File> temp = findInDirectFolder(found.get(0).getAbsolutePath(), deque.getLast());
            if (temp.size() > 0) return temp;
        }

        if(latestNotNull != null) {
            return deep ? Lists.newArrayList(new File(latestNotNull).listFiles()) : Lists.newArrayList(new File(latestNotNull));
        }
        return found;
    }

    private static List<File> findInDirectFolder(String folder, Film film) {
        File f = new File(folder);

        return f.listFiles() == null ? new ArrayList<>(): Stream.of(f.listFiles())
                .filter(matches(film)).collect(Collectors.toList());
    }

    private static Predicate<File> matches(Film film) {
        return a -> prepare(a.getName()).contains(prepare(film.getName())) ||
                (film.getEnName() != null && prepare(a.getName()).contains(prepare(film.getEnName())));
    }

    private static String prepare(String name) {
        return name.toLowerCase().replaceAll("ё", "е").replaceAll("[^0-9a-zа-я~]", "");
    }
}
