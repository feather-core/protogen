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

import org.feathercore.protogen.burger.BurgerSound;

import java.util.List;

/**
 * @author xtrafrancyz
 */
public class SoundGenerator extends ClassGenerator {
    public static final String CLASS_NAME = "MinecraftSound";
    
    private List<BurgerSound> sounds;

    public SoundGenerator(List<BurgerSound> sounds) {
        this.sounds = sounds;
    }

    @Override
    protected void generateClass() {
        sb.append("package org.feathercore.shared.sound;")
          .append(LF).append(LF);

        sb.append("public enum ").append(CLASS_NAME).append(" implements Sound {").append(LF);
        appendSounds();

        sb.append(LF);

        // Instance fields
        sb.append(T1).append("private final String name;").append(LF);
        sb.append(T1).append("private final int nativeId;").append(LF);

        sb.append(LF);

        // Constructor
        sb.append(T1).append(CLASS_NAME).append("(String name, int nativeId) {").append(LF);
        sb.append(T2).append("this.name = name;").append(LF);
        sb.append(T2).append("this.nativeId = nativeId;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        // Methods
        sb.append(T1).append("@Override").append(LF);
        sb.append(T1).append("public String getName() {").append(LF);
        sb.append(T2).append("return this.name;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append(T1).append("@Override").append(LF);
        sb.append(T1).append("public int getNativeId() {").append(LF);
        sb.append(T2).append("return this.nativeId;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append(T1).append("@Override").append(LF);
        sb.append(T1).append("public boolean isNative() {").append(LF);
        sb.append(T2).append("return true;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append("}").append(LF);
    }

    private void appendSounds() {
        for (int i = 0; i < sounds.size(); i++) {
            BurgerSound sound = sounds.get(i);
            sb.append(T1).append(sound.getEnumName())
              .append("(\"").append(sound.getName()).append("\", ")
              .append(sound.getId()).append(")");

            if (i == sounds.size() - 1) {
                sb.append(";");
            } else {
                sb.append(",");
            }
            sb.append(LF);
        }
    }
}
