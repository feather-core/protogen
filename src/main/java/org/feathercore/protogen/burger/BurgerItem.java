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

package org.feathercore.protogen.burger;

import com.google.gson.JsonObject;

/**
 * @author xtrafrancyz
 */
public class BurgerItem {
    //private static boolean warned;

    private String textId;
    private String enumName;
    private int id;
    private int maxStackSize;
    private boolean solid;

    public BurgerItem(JsonObject json) {
        this.textId = json.get("text_id").getAsString();
        this.id = json.get("numeric_id").getAsInt();
        this.enumName = textId.toUpperCase().replace(".", "_");
        this.maxStackSize = json.get("max_stack_size").getAsInt();
        /*if (jar != null && methodName != null && desc != null) {
            // Parse isSolid
            JsonPrimitive primitive = json.getAsJsonPrimitive("class");
            if (primitive != null) {
                String className = primitive.getAsString();
                if (className == null) {
                    System.err.println("No class name for item: " + this.textId + "/" + this.id);
                    return;
                }
                JarEntry entry = jar.getJarEntry(className.concat(".class"));
                if (entry == null) {
                    System.err.println("No class entry for item: " + this.textId + "/" + this.id + "/" + className);
                    return;
                }
                byte[] buffer = new byte[256];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (InputStream in = jar.getInputStream(entry)) {
                    for (int r; ((r = in.read(buffer)) != -1); ) {
                        baos.write(buffer, 0, r);
                    }
                } catch (IOException ex) {
                    System.err.println(
                            "Could not load class entry for item: " + this.textId + "/" + this.id + "/" + className);
                    ex.printStackTrace();
                    return;
                }
                ClassNode cn = new ClassNode();
                new ClassReader(baos.toByteArray()).accept(cn, 0);
                cn.methods.forEach(System.out::println);
                for (MethodNode o : cn.methods) {
                    if (methodName.equals(o.name) && desc.equals(o.name)) {
                        InsnList list = o.instructions;
                        for (AbstractInsnNode node : list.toArray()) {
                            System.out.println(node);
                        }
                        break;
                    }
                }
            }
        } else if (!warned) {
            System.err.println("No jar file or method name/descriptor was specified. isSolid will not be resolved");
            warned = true;
        }*/
    }

    public String getEnumName() {
        return enumName;
    }

    public String getTextId() {
        return textId;
    }

    public int getId() {
        return id;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public boolean isSolid() {
        return solid;
    }
}
