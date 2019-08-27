package com.by.movx.ui.utils;

import com.by.movx.entity.Film;
import com.by.movx.utils.CreatedDateCalculator;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by movx
 * on 27.08.2019.
 */
public class ControllerUtils {
    private static final Logger LOG = Logger.getLogger(ControllerUtils.class);

    public static void startExplorer(Film f)  throws Exception {
        File file = CreatedDateCalculator.getFile(f);
        if(file == null) return;
        LOG.info("explorer.exe /select,\"" + file + "\"");
        Runtime.getRuntime().exec("explorer.exe /select,\"" + file + "\"");
    }
}
