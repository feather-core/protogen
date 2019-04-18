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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.feathercore.protogen.util.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * List of all versions: https://pokechu22.github.io/Burger/versions.json
 *
 * @author xtrafrancyz
 */
public class BurgerReader {
    public static final String STANDARD_URL = "https://pokechu22.github.io/Burger/1.13.2.json";

    private List<BurgerSound> sounds;

    public BurgerReader() throws IOException {
        parse(NetworkUtil.get(STANDARD_URL));
    }

    public BurgerReader(File file) throws IOException {
        parse(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
    }

    private void parse(String str) {
        JsonObject root = new JsonParser()
                .parse(str)
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject();

        this.sounds = new ArrayList<>();
        JsonObject sounds = root.getAsJsonObject("sounds");
        for (Map.Entry<String, JsonElement> entry : sounds.entrySet()) {
            JsonObject sound = entry.getValue().getAsJsonObject();
            this.sounds.add(new BurgerSound(entry.getKey(), sound.get("id").getAsInt()));
        }
    }

    public List<BurgerSound> getSounds() {
        return sounds;
    }
}
