package com.tesla.obs;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class DeleteDirectoryContents {


    public static void emptyDirectory(Path directoryPath) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
                // EnumSet ensures that symbolic links are not followed
                EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);

                Files.walkFileTree(directoryPath, options, Integer.MAX_VALUE, new SimpleFileVisitor());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static class SimpleFileVisitor extends java.nio.file.SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // Delete each file in the directory
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            // Handle file visit failure
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc == null) {
                // Delete the directory after its contents are deleted
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            } else {
                // Handle directory visit failure
                return FileVisitResult.CONTINUE;
            }
        }
    }
}
