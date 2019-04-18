/*
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

import org.feathercore.protogen.types.Protocol;
import org.feathercore.protogen.types.Sender;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Retrieve valuable information from the Minecraft Protocol Wiki.
 *
 * @author Kristian
 */
public class WikiReader {
    public static final String STANDARD_URL = "http://www.wiki.vg/Protocol";

    // Stored packet information
    private List<WikiPacketInfo> packets;
    private List<WikiParticle> particles;

    public WikiReader() throws IOException {
        this(STANDARD_URL);
    }

    public WikiReader(String url) throws IOException {
        load(Jsoup.connect(url).get());
    }

    public WikiReader(File file) throws IOException {
        load(Jsoup.parse(file, null));
    }

    private void load(Document doc) {
        packets = loadPackets(doc);
        particles = loadParticles(doc);
    }

    private List<WikiParticle> loadParticles(Document doc) {
        List<WikiParticle> list = new ArrayList<>();
        Element table = doc.getElementById("Particle")
                           .parent()
                           .nextElementSibling()
                           .nextElementSibling();
        Elements rows = table.child(0).children();

        for (int i = 1; i < rows.size(); i++) {
            Elements tds = rows.get(i).getElementsByTag("td");
            String name = tds.get(0).text();
            int id = Integer.parseInt(tds.get(1).text().trim());
            boolean complex = !tds.get(2).text().trim().equals("None");
            list.add(new WikiParticle(name, id, complex));
        }

        return list;
    }

    private List<WikiPacketInfo> loadPackets(Document doc) {
        List<WikiPacketInfo> packets = new ArrayList<WikiPacketInfo>();
        Element bodyContent = doc.getElementById("mw-content-text");

        // Current protocol and sender
        Protocol protocol = null;
        Sender sender = null;
        String packetName = null;
        List<String> notes = null;

        for (Element element : bodyContent.children()) {
            String tag = element.tagName();

            // Protocol candidate
            if (tag.equals("h2")) {
                try {
                    protocol = Protocol.valueOf(getEnumText(element.select(".mw-headline").first()));
                } catch (IllegalArgumentException e) {
                    // We are in a section that is not a protocol
                    protocol = null;
                }

                // Sender candidates
            } else if (tag.equals("h3")) {
                String text = getEnumText(element.select(".mw-headline").first());

                if ("SERVERBOUND".equals(text)) {
                    sender = Sender.CLIENT;
                } else if ("CLIENTBOUND".equals(text)) {
                    sender = Sender.SERVER;
                }

                // Packet name candidate
            } else if (tag.equals("h4")) {
                packetName = element.select("span").text();
                notes = null;

            } else if (tag.equals("p")) {
                if (notes == null) {
                    notes = new ArrayList<>();
                }
                notes.add(element.text());

                // Table candidate
            } else if (tag.equals("table")) {
                int columnPacketId = getPacketIDColumn(element);

                // We have a real packet table
                if (columnPacketId >= 0) {
                    String string = element.select("td").get(columnPacketId).text().replace("0x", "").trim();
                    int packetId = Integer.parseInt(string, 16);

                    WikiPacketInfo info = new WikiPacketInfo(packetName, packetId, sender, protocol, notes);
                    processTable(info, element);
                    packets.add(info);
                    notes = null;
                    packetName = null;
                }
            }
        }
        return packets;
    }

    private void processTable(WikiPacketInfo info, Element table) {
        Set<String> names = new HashSet<>();
        List<WikiPacketField> fields = new ArrayList<>();
        Elements rows = table.select("tr");

        // Skip the first row
        for (int i = 1; i < rows.size(); i++) {
            String[] data = getCells(rows.get(i), i == 1 ? 3 : 0, 3);
            if (data[0] == null || data[1] == null) {
                info.setHasNulls(true);
                continue;
            }
            WikiPacketField field = new WikiPacketField(data[0], data[1], data[2]);
            while (names.contains(field.getJavaName())) {
                field = new WikiPacketField("dup_" + field.getName(), data[1], data[2]);
            }
            names.add(field.getJavaName());

            fields.add(field);
        }

        // Optimize array size fields
        for (int i = 0; i < fields.size(); i++) {
            WikiPacketField field = fields.get(i);
            if (field.isArray() && i > 0) {
                WikiPacketField prevField = fields.get(i - 1);
                switch (prevField.getFieldType()) {
                    case VARINT:
                    case BYTE:
                    case INT:
                    case SHORT:
                        String name = prevField.getName().toLowerCase();
                        if (name.contains("size") || name.contains("count")) {
                            prevField.setSizeOf(field);
                        }
                        break;
                }
            }
        }

        info.setFields(fields);
    }

    private String[] getCells(Element row, int start, int count) {
        String[] result = new String[count];
        Elements columns = row.getElementsByTag("td");

        // Convert each cell to text
        for (int i = 0; i < count; i++) {
            // We'll ignore non-existant columns
            if (i + start < columns.size()) {
                result[i] = columns.get(i + start).text();
            }
        }
        return result;
    }

    /**
     * Retrieve the column that contains the packet ID of a packet table.
     *
     * @param table - a table.
     * @return The 0-based index of this column, or -1 if not found.
     */
    private int getPacketIDColumn(Element table) {
        Elements headers = table.select("th");

        // Find the header with the packet ID
        for (int i = 0; i < headers.size(); i++) {
            final String text = getEnumText(headers.get(i));

            if ("PACKET_ID".equals(text)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retrieve the upper-case enum version of the textual content of an element.
     *
     * @param element - the element.
     * @return The textual content.
     */
    private String getEnumText(Element element) {
        return element.text().trim().toUpperCase().replace(" ", "_");
    }

    public List<WikiPacketInfo> getPackets() {
        return packets;
    }

    public List<WikiParticle> getParticles() {
        return particles;
    }
}
