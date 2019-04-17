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

package org.feathercore.protogen.generate;

import org.apache.commons.text.WordUtils;
import org.feathercore.protogen.types.FieldType;
import org.feathercore.protogen.types.Sender;
import org.feathercore.protogen.wiki.WikiPacketField;
import org.feathercore.protogen.wiki.WikiPacketInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xtrafrancyz
 */
public class Generator {
    private static final String LF = "\n";
    private static final String T1 = "    ";
    private static final String T2 = T1 + T1;
    private static final String T3 = T2 + T1;

    private WikiPacketInfo info;

    public Generator(WikiPacketInfo info) {
        this.info = info;
    }

    public String generateClass() {
        StringBuilder sb = new StringBuilder();
        appendCopyright(sb);
        sb.append(LF).append(LF);

        sb.append("package org.feathercore.protocol.minecraft.packet.")
          .append(info.getProtocol().name().toLowerCase())
          .append('.')
          .append(info.getSender().name().toLowerCase())
          .append(';')
          .append(LF);

        sb.append(LF);

        sb.append("import lombok.AccessLevel;").append(LF);
        sb.append("import lombok.Data;").append(LF);
        if (!info.getFields().isEmpty()) {
            sb.append("import lombok.AllArgsConstructor;").append(LF);
            sb.append("import lombok.NoArgsConstructor;").append(LF);
        }
        sb.append("import lombok.experimental.FieldDefaults;").append(LF);
        sb.append("import org.feathercore.protocol.Buffer;").append(LF);
        sb.append("import org.feathercore.protocol.minecraft.packet.MinecraftPacket;").append(LF);
        sb.append("import org.jetbrains.annotations.NotNull;").append(LF);
        if (info.hasOptionals()) {
            sb.append("import org.jetbrains.annotations.Nullable;").append(LF);
        }

        sb.append(LF);

        if (info.isBroken()) {
            sb.append("// CRITICAL !!").append(LF);
            sb.append("// This class is totally broken. Unknown types and brown shit.").append(LF);
            sb.append("// https://wiki.vg/Protocol#").append(info.getOriginalName().replace(" ", "_")).append(LF);
            sb.append(LF);
        } else {
            if (info.hasNulls()) {
                sb.append("// WARNING !!").append(LF);
                sb.append("// This class has null fields that are not included in generated class.").append(LF);
                sb.append("// https://wiki.vg/Protocol#").append(info.getOriginalName().replace(" ", "_")).append(LF);
                sb.append(LF);
            }

            if (info.hasOptionals()) {
                sb.append("// WARNING !!").append(LF);
                sb.append("// This class has optional fields. You need to write valid reader and writer.").append(LF);
                sb.append("// https://wiki.vg/Protocol#").append(info.getOriginalName().replace(" ", "_")).append(LF);
                sb.append(LF);
            }
        }

        if (info.getNotes() != null) {
            sb.append("/**").append(LF);
            boolean first = true;
            for (String note : info.getNotes()) {
                if (!first) {
                    sb.append(" *").append(LF);
                }
                first = false;
                String[] lines = WordUtils.wrap(note, 70, LF, false).split(LF);
                for (String line : lines) {
                    sb.append(" * ").append(line).append(LF);
                }
            }
            sb.append(" */").append(LF);
        }

        sb.append("@Data").append(LF);
        if (!info.getFields().isEmpty()) {
            sb.append("@NoArgsConstructor").append(LF);
            sb.append("@AllArgsConstructor").append(LF);
        }
        sb.append("@FieldDefaults(level = AccessLevel.PROTECTED)").append(LF);
        sb.append("public class ").append(info.getName()).append(" implements MinecraftPacket {").append(LF);
        sb.append(LF);

        // ID field
        sb.append(T1).append("public static final int ID = ").append(getBeautifulId()).append(";")
          .append(LF);
        sb.append(LF);

        // Fields
        appendFields(sb);
        sb.append(LF);

        // Constructor
        //appendConstructors(sb);
        //sb.append(LF);

        // Write / Read
        if (info.getSender() == Sender.SERVER) {
            appendWrite(sb);
        } else {
            appendRead(sb);
        }

        sb.append(LF);

        // GetId
        sb.append(T1).append("@Override").append(LF);
        sb.append(T1).append("public int getId() {").append(LF);
        sb.append(T2).append("return ID;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);
        sb.append("}").append(LF);

        return sb.toString();
    }

    private void appendFields(StringBuilder sb) {
        for (WikiPacketField field : info.getFields()) {
            if (field.getSizeOf() != null) {
                continue;
            }

            if (!field.getNotes().isEmpty()) {
                sb.append(T1).append("/**").append(LF);
                String[] lines = WordUtils.wrap(field.getNotes(), 60, LF, false).split(LF);
                for (String line : lines) {
                    sb.append(T1).append(" * ").append(line).append(LF);
                }
                sb.append(T1).append(" */").append(LF);
            }
            sb.append(T1)
              .append(field.isOptional() ? "@Nullable " : "");
            if (field.getFieldType() == FieldType.UNKNOWN) {
                sb.append("Object")
                  .append(field.isArray() ? "[] " : " ")
                  .append(field.getJavaName())
                  .append(";")
                  .append(" // Type: ").append(field.getFieldTypeString())
                  .append(LF);
            } else {
                sb.append(field.getFieldType().getJavaType())
                  .append(field.isArray() ? "[] " : " ")
                  .append(field.getJavaName())
                  .append(";").append(LF);
            }
        }
    }

    private void appendConstructors(StringBuilder sb) {
        sb.append(T1).append("public ").append(info.getName()).append("() {}")
          .append(LF).append(LF);

        sb.append(T1).append("public ").append(info.getName()).append("(");
        boolean first = true;
        for (WikiPacketField field : info.getFields()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(field.getFieldType().getJavaType()).append(' ').append(field.getJavaName());
        }
        sb.append(") {").append(LF);

        for (WikiPacketField field : info.getFields()) {
            sb.append(T2)
              .append("this.").append(field.getJavaName())
              .append(" = ")
              .append(field.getJavaName())
              .append(";").append(LF);
        }

        sb.append(T1).append('}').append(LF);
    }

    private void appendWrite(StringBuilder sb) {
        sb.append(T1).append("@Override").append(LF);
        sb.append(T1).append("public void write(@NotNull final Buffer buffer) {").append(LF);
        for (WikiPacketField field : info.getFields()) {
            if (field.getSizeOf() != null) {
                sb.append(T2)
                  .append("buffer.write").append(field.getFieldType().getBufferMethod()).append("(")
                  .append("this.").append(field.getSizeOf().getJavaName()).append(".length")
                  .append(");")
                  .append(LF);
            } else if (field.isArray() && field.getFieldType() == FieldType.BYTE) {
                sb.append(T2)
                  .append("buffer.writeBytes(")
                  .append("this.").append(field.getJavaName())
                  .append(");")
                  .append(LF);
            } else if (field.isArray()) {
                //sb.append(T2)
                //  .append("buffer.writeVarInt(this.").append(field.getJavaName()).append(".length);").append(LF);
                sb.append(T2)
                  .append("for (int i = 0; i < this.").append(field.getJavaName()).append(".length; i++) {").append(LF);
                sb.append(T3)
                  .append("buffer.write").append(field.getFieldType().getBufferMethod()).append("(")
                  .append("this.").append(field.getJavaName()).append("[i]")
                  .append(");")
                  .append(LF);
                sb.append(T2).append("}").append(LF);
            } else {
                sb.append(T2)
                  .append("buffer.write").append(field.getFieldType().getBufferMethod()).append("(")
                  .append("this.").append(field.getJavaName())
                  .append(");")
                  .append(LF);
            }
        }
        sb.append(T1).append("}").append(LF);
    }

    private void appendRead(StringBuilder sb) {
        sb.append(T1).append("@Override").append(LF);
        sb.append(T1).append("public void read(@NotNull final Buffer buffer) {").append(LF);
        Map<WikiPacketField, String> arraySizes = new HashMap<>();
        for (WikiPacketField field : info.getFields()) {
            String sizeVar = arraySizes.get(field);
            if (field.getSizeOf() != null) {
                sizeVar = field.getSizeOf().getJavaName() + "_size";
                sb.append(T2)
                  .append("int ").append(sizeVar)
                  .append(" = ")
                  .append("buffer.read").append(field.getFieldType().getBufferMethod()).append("();")
                  .append(LF);
                arraySizes.put(field.getSizeOf(), sizeVar);
            } else if (field.isArray()) {
                if (sizeVar != null) {
                    sb.append(T2)
                      .append("this.").append(field.getJavaName())
                      .append(" = ")
                      .append("new ").append(field.getFieldType().getJavaType()).append("[").append(sizeVar)
                      .append("];")
                      .append(LF);
                } else {
                    sb.append(T2).append("// Array size for ").append(field.getJavaName()).append(" not found")
                      .append(LF);
                }
                if (field.getFieldType() == FieldType.BYTE) {
                    sb.append(T2)
                      .append("buffer.readBytes(")
                      .append("this.").append(field.getJavaName())
                      .append(");")
                      .append(LF);
                } else {
                    sb.append(T2)
                      .append("for (int i = 0; i < ").append(sizeVar).append("; i++) {").append(LF);
                    sb.append(T3)
                      .append("this.").append(field.getJavaName()).append("[i]")
                      .append(" = ");
                    if (field.getFieldType() == FieldType.STRING) {
                        sb.append("buffer.readString(").append(field.getStringTypeSize()).append(");");
                    } else {
                        sb.append("buffer.read").append(field.getFieldType().getBufferMethod()).append("();");
                    }
                    sb.append(LF);
                    sb.append(T2).append("}").append(LF);
                }
            } else if (field.getFieldType() == FieldType.STRING) {
                sb.append(T2)
                  .append("this.").append(field.getJavaName())
                  .append(" = ")
                  .append("buffer.read").append(field.getFieldType().getBufferMethod())
                  .append("(").append(field.getStringTypeSize()).append(");")
                  .append(LF);
            } else {
                sb.append(T2)
                  .append("this.").append(field.getJavaName())
                  .append(" = ")
                  .append("buffer.read").append(field.getFieldType().getBufferMethod()).append("();")
                  .append(LF);
            }
        }
        sb.append(T1).append("}").append(LF);
    }

    private void appendCopyright(StringBuilder sb) {
        sb.append("/*\n"
                + " * Copyright 2019 Feather Core\n"
                + " *\n"
                + " * Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                + " * you may not use this file except in compliance with the License.\n"
                + " * You may obtain a copy of the License at\n"
                + " *\n"
                + " *     http://www.apache.org/licenses/LICENSE-2.0\n"
                + " *\n"
                + " * Unless required by applicable law or agreed to in writing, software\n"
                + " * distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                + " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                + " * See the License for the specific language governing permissions and\n"
                + " * limitations under the License.\n"
                + " */");
    }

    private String getBeautifulId() {
        String hex = Integer.toHexString(info.getId());
        if (hex.length() == 1) {
            return "0x0" + hex;
        }
        return "0x" + hex;
    }
}
