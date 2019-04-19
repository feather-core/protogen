/*
 * Copyright 2019 Feather Core
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.feathercore.protogen.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author xtrafrancyz
 */
public class FileUtil {
    public static void writeFile(Path file, String content) {
        try {
            Path parent = file.getParent();
            if(parent != null && !Files.isDirectory(parent)) {
                Files.createDirectories(parent);
            }
            Files.write(file, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteRecursive(Path dir) {
        if (Files.isDirectory(dir)) {
            try {
                Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        FileVisitResult result = super.postVisitDirectory(dir, exc);
                        Files.delete(dir);
                        return result;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return super.visitFile(file, attrs);
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
