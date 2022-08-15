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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public final class MaterialUtil {

    private MaterialUtil() { }

    public static Set<String> buildSolidMaterials(Path jar, MinecraftVersion version)
            throws IOException {
        if (version.getMaterialSlot() == -1) {
            System.err.println("isSolid is unsupported for specified version, skipping");
            return Collections.emptySet();
        }
        // Let's rock!
        ClassLoader loader = MaterialUtil.class.getClassLoader();
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(loader, jar.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            System.err.println("Exception occurred while injecting jar into class path, skipping isSolid");
            ex.printStackTrace();
            return Collections.emptySet();
        }
        Class<?> klass;
        try {
            klass = Class.forName(version.getBlockClass(), true, loader);
        } catch (ClassNotFoundException ex) {
            System.err.println("Class loader failed to load class, skipping isSolid");
            ex.printStackTrace();
            return Collections.emptySet();
        }
        // Call init
        Optional<Method> optional = Arrays.stream(klass.getDeclaredMethods())
                                          .filter(m -> Modifier.isPublic(m.getModifiers()))
                                          .filter(m -> Modifier.isStatic(m.getModifiers()))
                                          .filter(m -> Void.TYPE == m.getReturnType())
                                          .findFirst();
        if (!optional.isPresent()) {
            System.err.println("No init method in Block class, skipping isSolid");
            return Collections.emptySet();
        }
        try {
            optional.get().invoke(null);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            System.err.println("Init method invocation failed, skipping isSolid");
            ex.printStackTrace();
            return Collections.emptySet();
        }
        // Search for material field
        // TODO
        return Collections.emptySet();
    }
}
