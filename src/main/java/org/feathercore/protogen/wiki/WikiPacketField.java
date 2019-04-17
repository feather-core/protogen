/**
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 * Copyright (C) xtrafrancyz <https://xtrafrancyz.net>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.feathercore.protogen.wiki;

import org.feathercore.protogen.types.FieldType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xtrafrancyz
 */
public class WikiPacketField {
    private static final Pattern SIZE_EXTRACTOR = Pattern.compile("^[^(]+\\(([0-9]+)\\)$");

    private String name;
    private String javaName;
    private String fieldTypeString;
    private FieldType fieldType;
    private int stringTypeSize;
    private String notes;
    private boolean optional;
    private boolean array;
    private WikiPacketField sizeOf;

    public WikiPacketField(String name, String type, String notes) {
        this.name = name;
        this.fieldTypeString = type;
        this.fieldType = FieldType.parse(type);
        this.notes = notes;
        if (name != null) {
            if (name.equals("ID")) {
                this.javaName = "id";
            } else {
                this.javaName =
                        (Character.toLowerCase(name.charAt(0)) + name.substring(1).replace(" ", ""))
                                .split("\\(")[0];
            }
        }
        if (fieldType == FieldType.STRING) {
            Matcher matcher = SIZE_EXTRACTOR.matcher(type);
            if (!matcher.matches()) {
                this.stringTypeSize = 32767;
                System.out.println("Illegal string type: " + type);
            } else {
                this.stringTypeSize = Integer.parseInt(matcher.group(1));
            }
        }
        this.optional = type != null && type.toLowerCase().contains("optional");
        this.array = type != null && type.toLowerCase().contains("array");
    }

    public String getName() {
        return name;
    }

    public String getJavaName() {
        return javaName;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getFieldTypeString() {
        return fieldTypeString;
    }

    public int getStringTypeSize() {
        return stringTypeSize;
    }

    public boolean isOptional() {
        return optional;
    }
    
    public boolean isArray() {
        return array;
    }

    public void setSizeOf(final WikiPacketField sizeOf) {
        this.sizeOf = sizeOf;
    }

    public WikiPacketField getSizeOf() {
        return sizeOf;
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }
}
