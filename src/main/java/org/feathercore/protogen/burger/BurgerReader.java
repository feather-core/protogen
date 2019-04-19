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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.feathercore.protogen.util.NetworkUtil;

import java.io.IOException;
import java.util.*;

/**
 * List of all versions: https://pokechu22.github.io/Burger/versions.json
 *
 * @author xtrafrancyz
 */
public class BurgerReader {

    public static final String URL = "https://pokechu22.github.io/Burger/%s.json";

    private List<BurgerSound> sounds;
    private List<BurgerItem> items;
    private Map<String, BurgerBlock> blocks;

    public BurgerReader(String version) throws IOException {
        parse(NetworkUtil.get(String.format(URL, version)));
    }

    private void parse(String str) {
        JsonObject root = new JsonParser()
                .parse(str)
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject();

        // Sounds
        this.sounds = new ArrayList<>();
        JsonObject sounds = root.getAsJsonObject("sounds");
        for (Map.Entry<String, JsonElement> entry : sounds.entrySet()) {
            JsonObject sound = entry.getValue().getAsJsonObject();
            this.sounds.add(new BurgerSound(entry.getKey(), sound.get("id").getAsInt()));
        }

        // Items
        this.items = new ArrayList<>();
        JsonObject items = root.getAsJsonObject("items").getAsJsonObject("item");
        for (Map.Entry<String, JsonElement> entry : items.entrySet()) {
            this.items.add(new BurgerItem(entry.getValue().getAsJsonObject()));
        }
        this.items.sort(Comparator.comparing(BurgerItem::getId));

        // Blocks
        this.blocks = new LinkedHashMap<>();
        JsonObject blocks = root.getAsJsonObject("blocks").getAsJsonObject("block");
        Map<String, BurgerBlock> tempBlocks = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : blocks.entrySet()) {
            tempBlocks.put(entry.getKey(), new BurgerBlock(entry.getValue().getAsJsonObject()));
        }
        JsonArray blocksOrder = root.getAsJsonObject("blocks").getAsJsonArray("ordered_blocks");
        for (JsonElement element : blocksOrder) {
            BurgerBlock block = tempBlocks.get(element.getAsString());
            if (block == null) {
                throw new IllegalStateException("Block " + element.getAsString() + " is not found (order)");
            }
            this.blocks.put(element.getAsString(), block);
        }
    }

    public List<BurgerSound> getSounds() {
        return sounds;
    }

    public List<BurgerItem> getItems() {
        return items;
    }

    public Map<String, BurgerBlock> getBlocks() {
        return blocks;
    }
}
