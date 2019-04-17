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

package org.feathercore.protogen;

import org.feathercore.protogen.generate.Generator;
import org.feathercore.protogen.wiki.WikiPacketInfo;
import org.feathercore.protogen.wiki.WikiPacketReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author xtrafrancyz
 */
@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
public class Bootstrap {
    private static final File VALID_DIR = new File("gen");
    private static final File BROKEN_DIR = new File("gen-broken");

    public static void main(String[] args) throws IOException {
        //List<WikiPacketInfo> packets = new WikiPacketReader(new File("proto.html")).getPackets();
        List<WikiPacketInfo> packets = new WikiPacketReader().getPackets();

        deleteRecursive(VALID_DIR);
        VALID_DIR.mkdir();
        deleteRecursive(BROKEN_DIR);
        BROKEN_DIR.mkdir();

        for (WikiPacketInfo info : packets) {
            save(info);
        }
    }

    private static void save(WikiPacketInfo info) {
        String generated = new Generator(info).generateClass();
        File protoDir = new File(info.isBroken() ? BROKEN_DIR : VALID_DIR, info.getProtocol().name().toLowerCase());
        if (!protoDir.exists()) {
            protoDir.mkdir();
        }
        File finalDir = new File(protoDir, info.getSender().name().toLowerCase());
        if (!finalDir.exists()) {
            finalDir.mkdir();
        }
        File out = new File(finalDir, info.getName() + ".java");
        try (FileOutputStream fos = new FileOutputStream(out)) {
            fos.write(generated.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteRecursive(f);
            }
        }
        file.delete();
    }
}
