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

import org.feathercore.protogen.version.MinecraftVersion;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class MaterialUtil {

    private MaterialUtil() { }

    public static Set<String> buildSolidMaterials(Path jar, String initClass, MinecraftVersion version) throws IOException {
        URI uri = jar.toUri();
        // Prevent encoding issues, meh
        try {
            Field field = URI.class.getDeclaredField("scheme");
            field.setAccessible(true);
            field.set(uri, "jar:file");
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
            uri = URI.create("jar:file:" + uri.getPath());
        }
        ClassNode classNode;
        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            Path path = fs.getPath(initClass.concat(".class").replace('.', '/'));
            if (!Files.exists(path)) {
                throw new FileSystemException("No such class: " + initClass);
            }
            try (InputStream in = Files.newInputStream(path)) {
                classNode = new ClassNode();
                new ClassReader(in).accept(classNode, 0);
            }
        }
    }
}
