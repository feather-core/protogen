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

import com.google.gson.JsonObject;

/**
 * @author xtrafrancyz
 */
public class BurgerBlock {
    private String textId;
    private int id;
    private float hardness;
    private float resistance;

    public BurgerBlock(JsonObject json) {
        this.textId = json.get("text_id").getAsString();
        this.id = json.get("numeric_id").getAsInt();
        if (json.has("hardness")) {
            this.hardness = json.get("hardness").getAsFloat();
            this.resistance = json.get("resistance").getAsFloat();
        }
    }

    public String getTextId() {
        return textId;
    }

    public int getId() {
        return id;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }
}
