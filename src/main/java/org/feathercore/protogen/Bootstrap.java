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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.EnumConverter;
import org.feathercore.protogen.burger.BurgerReader;
import org.feathercore.protogen.generate.MaterialGenerator;
import org.feathercore.protogen.generate.PacketGenerator;
import org.feathercore.protogen.generate.ParticleGenerator;
import org.feathercore.protogen.generate.SoundGenerator;
import org.feathercore.protogen.util.FileUtil;
import org.feathercore.protogen.version.MinecraftVersion;
import org.feathercore.protogen.wiki.WikiPacketInfo;
import org.feathercore.protogen.wiki.WikiReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xtrafrancyz
 */
public class Bootstrap {
    private static final Path VALID_DIR;
    private static final Path BROKEN_DIR;

    private static final MinecraftVersion DEFAULT_VERSION = MinecraftVersion._1_13_2;
    private static final CachedDataSource<BurgerReader> BURGER = new CachedDataSource<>();
    private static final CachedDataSource<WikiReader> WIKI = new CachedDataSource<>();

    private static final Map<GeneratorType, GenRunnable> GENERATORS = new HashMap<GeneratorType, GenRunnable>() {{
        put(GeneratorType.MATERIALS, Bootstrap::genMaterials);
        put(GeneratorType.SOUNDS, Bootstrap::genSounds);
        put(GeneratorType.PARTICLES, Bootstrap::genParticles);
        put(GeneratorType.PACKETS, Bootstrap::genPackets);
        put(GeneratorType.ALL, () -> {
            Bootstrap.genMaterials();
            Bootstrap.genSounds();
            Bootstrap.genParticles();
            Bootstrap.genPackets();
        });
    }};

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        OptionSpec<GeneratorType> specType = parser.accepts("type")
                                               .withRequiredArg()
                                               .ofType(GeneratorType.class)
                                               .withValuesConvertedBy(new EnumConverter<GeneratorType>(GeneratorType.class) {})
                                               .required();
        OptionSpec<MinecraftVersion> specVersion = parser.accepts("version")
                                                         .withRequiredArg()
                                                         .ofType(MinecraftVersion.class)
                                                         .withValuesConvertedBy(new EnumConverter<MinecraftVersion>(MinecraftVersion.class) {
                                                             @Override
                                                             public MinecraftVersion convert(String value) {
                                                                 return super.convert("_".concat(value.replace('.', '_')));
                                                             }
                                                         })
                                                         .defaultsTo(DEFAULT_VERSION);
        OptionSet options = parser.parse(args);
        GeneratorType type = options.valueOf(specType);
        MinecraftVersion version = options.valueOf(specVersion);

        System.out.println("Generator: " + type + ", version: " + version);

        FileUtil.deleteRecursive(VALID_DIR);
        FileUtil.deleteRecursive(BROKEN_DIR);

        BURGER.setHandle(() -> new BurgerReader(version));
        // TODO workaround please
        WIKI.setHandle(() -> new WikiReader(version.getLink()));
        GENERATORS.get(type).run();
    }

    private static void genMaterials() throws Exception {
        String generated = new MaterialGenerator(BURGER.get()).generate();
        FileUtil.writeFile(VALID_DIR.resolve(MaterialGenerator.CLASS_NAME + ".java"), generated);
    }

    private static void genParticles() throws Exception {
        //String generated = new ParticleGenerator(new WikiReader(new File("proto.html")).getParticles()).generate();
        String generated = new ParticleGenerator(WIKI.get().getParticles()).generate();
        FileUtil.writeFile(VALID_DIR.resolve(ParticleGenerator.CLASS_NAME + ".java"), generated);
    }

    private static void genSounds() throws Exception {
        String generated = new SoundGenerator(BURGER.get().getSounds()).generate();
        FileUtil.writeFile(VALID_DIR.resolve(SoundGenerator.CLASS_NAME + ".java"), generated);
    }

    private static void genPackets() throws Exception {
        List<WikiPacketInfo> packets = WIKI.get().getPackets();
        for (WikiPacketInfo info : packets) {
            String generated = new PacketGenerator(info).generate();
            Path protoDir = (info.isBroken() ? BROKEN_DIR : VALID_DIR).resolve(info.getProtocol().name().toLowerCase());
            Path finalDir = protoDir.resolve(info.getSender().name().toLowerCase());
            FileUtil.writeFile(finalDir.resolve(info.getStandardClassName() + ".java"), generated);
        }
    }

    static {
        Path workingDir = Paths.get(System.getProperty("user.dir"));
        VALID_DIR = workingDir.resolve("gen");
        BROKEN_DIR = workingDir.resolve("gen-broken");
    }

    private interface GenRunnable {
        void run() throws Exception;
    }
}
