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
import org.feathercore.protogen.types.Protocol;
import org.feathercore.protogen.types.Sender;

import java.util.List;

public class WikiPacketInfo {
    private final String originalName;
    private final String name;
    private final int id;
    private final Sender sender;
    private final Protocol protocol;
    private List<WikiPacketField> fields;
    private boolean hasNulls;
    private List<String> notes;

    public WikiPacketInfo(String name, int id, Sender sender, Protocol protocol, List<String> notes) {
        this.originalName = name;
        this.name = "Packet" + name.replace(" ", "").replace("-", "").split("\\(")[0];
        this.id = id;
        this.sender = sender;
        this.protocol = protocol;
        this.notes = notes;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getName() {
        return name;
    }

    public String getNonPrefixedName() {
        return name.startsWith("Packet") ? name.substring(6) : name;
    }

    // <Phase>Packet<Sender><Name>
    public String getStandardClassName() {
        return protocol.getCommonName() + "Packet" + sender.getCommonName() + getNonPrefixedName();
    }

    public int getId() {
        return id;
    }

    public Sender getSender() {
        return sender;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setFields(final List<WikiPacketField> fields) {
        this.fields = fields;
    }

    public List<WikiPacketField> getFields() {
        return fields;
    }

    public List<String> getNotes() {
        return notes;
    }

    public boolean isBroken() {
        return fields.stream().map(WikiPacketField::getFieldType).anyMatch(t -> t == FieldType.UNKNOWN);
    }

    public boolean hasOptionals() {
        return fields.stream().anyMatch(WikiPacketField::isOptional);
    }

    public void setHasNulls(boolean hasNulls) {
        this.hasNulls = hasNulls;
    }

    public boolean hasNulls() {
        return hasNulls;
    }

    @Override
    public String toString() {
        return name + "(" + Integer.toHexString(id) + ")";
    }
}
