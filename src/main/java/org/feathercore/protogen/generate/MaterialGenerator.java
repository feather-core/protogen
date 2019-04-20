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

import org.feathercore.protogen.burger.BurgerBlock;
import org.feathercore.protogen.burger.BurgerItem;
import org.feathercore.protogen.burger.BurgerReader;

import java.util.List;
import java.util.Map;

/**
 * @author xtrafrancyz
 */
public class MaterialGenerator extends ClassGenerator {
    public static final String CLASS_NAME = "MinecraftMaterial";

    private final List<BurgerItem> items;
    private final Map<String, BurgerBlock> blocks;

    public MaterialGenerator(BurgerReader burger) {
        items = burger.getItems();
        blocks = burger.getBlocks();
    }

    @Override
    protected void generateClass() {
        sb.append("package org.feathercore.shared.item;")
          .append(LF).append(LF);

        sb.append("import lombok.AccessLevel;").append(LF);
        sb.append("import lombok.Getter;").append(LF);
        sb.append("import lombok.RequiredArgsConstructor;").append(LF);
        sb.append("import lombok.experimental.FieldDefaults;").append(LF);

        sb.append(LF);

        sb.append("@RequiredArgsConstructor(access = AccessLevel.PACKAGE)").append(LF);
        sb.append("@Getter").append(LF);
        sb.append("@FieldDefault(level = AccessLevel.PRIVATE, makeFinal = true)").append(LF);
        sb.append("public enum ").append(CLASS_NAME).append(" implements Material {").append(LF);

        appendMaterials();

        sb.append(LF);

        // Instance fields
        sb.append(T1).append("String textId = \"minecraft:\" + name().toLowerCase();").append(LF);
        sb.append(T1).append("@Getter(AccessLevel.NONE) int id;").append(LF);
        sb.append(T1).append("int maxStackSize;").append(LF);
        sb.append(T1).append("boolean block;").append(LF);
        sb.append(T2).append("boolean solid;").append(LF);

        sb.append(LF);

        // Methods
        sb.append(T1).append("@Override").append(LF);
        sb.append(T1).append("public int getNativeId() {").append(LF);
        sb.append(T2).append("return this.id;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append(T1).append("@Override").append(LF);
        sb.append(T1).append("public boolean isNative() {").append(LF);
        sb.append(T2).append("return true;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append("}").append(LF);
    }

    private void appendMaterials() {
        for (int i = 0; i < items.size(); i++) {
            BurgerItem item = this.items.get(i);
            sb.append(T1).append(item.getEnumName())
              .append("(").append(item.getId()).append(", ")
              .append(item.getMaxStackSize()).append(", ")
              .append(blocks.containsKey(item.getTextId())).append(", ")
              .append(item.isSolid())
              .append(")");

            if (i == this.items.size() - 1) {
                sb.append(";");
            } else {
                sb.append(",");
            }
            sb.append(LF);
        }
    }
}
