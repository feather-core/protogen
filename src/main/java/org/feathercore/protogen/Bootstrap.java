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

import org.feathercore.protogen.burger.BurgerReader;
import org.feathercore.protogen.generate.PacketGenerator;
import org.feathercore.protogen.generate.ParticleGenerator;
import org.feathercore.protogen.generate.SoundGenerator;
import org.feathercore.protogen.util.FileUtil;
import org.feathercore.protogen.wiki.WikiPacketInfo;
import org.feathercore.protogen.wiki.WikiReader;

import java.io.File;
import java.util.List;

/**
 * @author xtrafrancyz
 */
public class Bootstrap {
    private static final File VALID_DIR = new File("gen");
    private static final File BROKEN_DIR = new File("gen-broken");

    private static final CachedDataSource<BurgerReader> BURGER = new CachedDataSource<>(BurgerReader::new);
    private static final CachedDataSource<WikiReader> WIKI = new CachedDataSource<>(WikiReader::new);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Choose what to generate:");
            System.out.println("    sounds, particles or packets");
            return;
        }

        FileUtil.deleteRecursive(VALID_DIR);
        FileUtil.deleteRecursive(BROKEN_DIR);

        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "sounds":
                    genSounds();
                    break;
                case "particles":
                    genParticles();
                    break;
                case "packets":
                    genPackets();
                    break;
            }
        }
    }

    private static void genParticles() throws Exception {
        //String generated = new ParticleGenerator(new WikiReader(new File("proto.html")).getParticles()).generate();
        String generated = new ParticleGenerator(WIKI.get().getParticles()).generate();
        FileUtil.writeFile(new File(VALID_DIR, ParticleGenerator.CLASS_NAME + ".java"), generated);
    }

    private static void genSounds() throws Exception {
        String generated = new SoundGenerator(BURGER.get().getSounds()).generate();
        FileUtil.writeFile(new File(VALID_DIR, SoundGenerator.CLASS_NAME + ".java"), generated);
    }

    private static void genPackets() throws Exception {
        List<WikiPacketInfo> packets = WIKI.get().getPackets();
        for (WikiPacketInfo info : packets) {
            String generated = new PacketGenerator(info).generate();
            File protoDir = new File(info.isBroken() ? BROKEN_DIR : VALID_DIR, info.getProtocol().name().toLowerCase());
            File finalDir = new File(protoDir, info.getSender().name().toLowerCase());
            FileUtil.writeFile(new File(finalDir, info.getStandardClassName() + ".java"), generated);
        }
    }
}
