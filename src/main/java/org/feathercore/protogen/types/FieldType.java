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

package org.feathercore.protogen.types;

/**
 * @author xtrafrancyz
 */
public enum FieldType {
    VARINT("int", "VarInt"),
    INT("int", "Int"),
    DOUBLE("double", "Double"),
    ANGLE("float", "Float"),
    BYTE("byte", "Byte"),
    UNSIGNED_BYTE("int", "UnsignedByte"),
    UUID("UUID", "UUID"),
    SHORT("short", "Short"),
    FLOAT("float", "Float"),
    LONG("long", "Long"),
    VARLONG("long", "VarLong"),
    BOOLEAN("boolean", "Boolean"),
    UNSIGNED_SHORT("int", "UnsignedShort"),
    ENTITY_METADATA("EntityMetadata", "Metadata"),
    POSITION("Position", "Position"),
    NBT("NbtTagCompound", "Nbt"),
    CHAT("BaseComponent[]", "BaseComponent"),
    ITEMSTACK("ItemStack", "ItemStack"),
    STRING("String", "String"),

    UNKNOWN("Unknown", "Unknown"),
    NULL("null", "Unknown");

    private final String javaType;
    private final String bufferMethod;

    FieldType() {
        this(null, null);
    }

    FieldType(String javaType, final String bufferMethod) {
        this.javaType = javaType;
        this.bufferMethod = bufferMethod;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getBufferMethod() {
        return bufferMethod;
    }

    public static FieldType parse(String type0) {
        if (type0 == null) {
            return NULL;
        }
        String type = type0.toLowerCase();
        if (type.contains("varint")) {
            return VARINT;
        }
        if (type.contains("float")) {
            return FLOAT;
        }
        if (type.equals("varlong")) {
            return VARLONG;
        }
        if (type.equals("long")) {
            return LONG;
        }
        if (type.equals("boolean")) {
            return BOOLEAN;
        }
        if (type.equals("angle")) {
            return ANGLE;
        }
        if (type.contains("unsigned") && type.contains("byte")) {
            return UNSIGNED_BYTE;
        }
        if (type.contains("byte")) {
            return BYTE;
        }
        if (type.contains("unsigned") && type.contains("short")) {
            return UNSIGNED_SHORT;
        }
        if (type.equals("short")) {
            return SHORT;
        }
        if (type.equals("double")) {
            return DOUBLE;
        }
        if (type.equals("uuid")) {
            return UUID;
        }
        if (type.contains("entity") && type.contains("metadata")) {
            return ENTITY_METADATA;
        }
        if (type.contains("nbt")) {
            return NBT;
        }
        if (type.contains("string") || type.contains("identifier")) {
            return STRING;
        }
        if (type.equals("position")) {
            return POSITION;
        }
        if (type.contains("slot")) {
            return ITEMSTACK;
        }
        if (type.contains("int")) {
            return INT;
        }
        if (type.contains("chat")) {
            return CHAT;
        }
        System.out.println("Unknown type " + type0);
        return UNKNOWN;
    }
}
