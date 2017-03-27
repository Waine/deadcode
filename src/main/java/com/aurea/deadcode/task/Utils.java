package com.aurea.deadcode.task;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by ekonovalov on 14.03.2017.
 */
@Slf4j
public class Utils {

    public static void copyDatabase(File from, File to) throws IOException {
        @Cleanup InputStream is = new FileInputStream(from);
        @Cleanup OutputStream os = new BufferedOutputStream(new FileOutputStream(to));
        IOUtils.copy(is, os);
    }

    public static void copyDatabase(String from, File to) throws IOException {
        @Cleanup InputStream is = Utils.class.getResourceAsStream(from);
        @Cleanup OutputStream os = new BufferedOutputStream(new FileOutputStream(to));
        IOUtils.copy(is, os);
    }

    public static String runCommand(File dir, String cmd) throws IOException {
        log.info("Working dir = " + dir.getAbsolutePath());
        log.info("Command = " + cmd);
        CommandLine cmdLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.setWorkingDirectory(dir);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(3600000);
        executor.setWatchdog(watchdog);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(output);
        executor.setStreamHandler(streamHandler);
        try {
            int exitValue = executor.execute(cmdLine);
            log.info("Exit value: " + exitValue);
        } catch (ExecuteException e) {
            log.info(new String(output.toByteArray(), "UTF-8"));
            throw e;
        }
        log.info("Exit value: " + executor.execute(cmdLine));

        return new String(output.toByteArray(), "UTF-8");
    }

}
