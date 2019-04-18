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

import org.feathercore.protogen.wiki.WikiParticle;

import java.util.List;

/**
 * @author xtrafrancyz
 */
public class ParticleGenerator extends ClassGenerator {
    private List<WikiParticle> particles;

    public ParticleGenerator(List<WikiParticle> particles) {
        this.particles = particles;
    }

    @Override
    protected void generateClass() {
        sb.append("package org.feathercore.protocol.particle;")
          .append(LF).append(LF);

        sb.append("public enum Particle {").append(LF);
        appendParticles();

        sb.append(LF);

        // Instance fields
        sb.append(T1).append("private final String name;").append(LF);
        sb.append(T1).append("private final int id;").append(LF);
        sb.append(T1).append("private final boolean complex;").append(LF);

        sb.append(LF);

        // Constructor
        sb.append(T1).append("Particle(String name, int id) {").append(LF);
        sb.append(T2).append("this(name, id, false);").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append(T1).append("Particle(String name, int id, boolean complex) {").append(LF);
        sb.append(T2).append("this.name = name;").append(LF);
        sb.append(T2).append("this.id = id;").append(LF);
        sb.append(T2).append("this.complex = complex;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        // Methods
        sb.append(T1).append("public String getName() {").append(LF);
        sb.append(T2).append("return this.name;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append(T1).append("public int getId() {").append(LF);
        sb.append(T2).append("return this.id;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append(T1).append("public boolean isComplex() {").append(LF);
        sb.append(T2).append("return this.complex;").append(LF);
        sb.append(T1).append("}").append(LF);

        sb.append(LF);

        sb.append("}").append(LF);
    }

    private void appendParticles() {
        for (int i = 0; i < particles.size(); i++) {
            WikiParticle particles = this.particles.get(i);
            sb.append(T1).append(particles.getEnumName())
              .append("(\"").append(particles.getName()).append("\", ")
              .append(particles.getId());

            if (particles.isComplex()) {
                sb.append(", true");
            }
            sb.append(")");

            if (i == this.particles.size() - 1) {
                sb.append(";");
            } else {
                sb.append(",");
            }
            sb.append(LF);
        }
    }
}
