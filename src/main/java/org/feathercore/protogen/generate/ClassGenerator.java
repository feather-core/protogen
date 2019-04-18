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

/**
 * @author xtrafrancyz
 */
public abstract class ClassGenerator {
    protected static final String LF = "\n";
    protected static final String T1 = "    ";
    protected static final String T2 = T1 + T1;
    protected static final String T3 = T2 + T1;

    protected StringBuilder sb;

    public String generate() {
        sb = new StringBuilder();
        
        appendCopyright();
        sb.append(LF).append(LF);
        
        generateClass();
        
        return sb.toString();
    }

    protected abstract void generateClass();

    protected void appendCopyright() {
        sb.append("/*\n"
                + " * Copyright 2019 Feather Core\n"
                + " *\n"
                + " * Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                + " * you may not use this file except in compliance with the License.\n"
                + " * You may obtain a copy of the License at\n"
                + " *\n"
                + " *     http://www.apache.org/licenses/LICENSE-2.0\n"
                + " *\n"
                + " * Unless required by applicable law or agreed to in writing, software\n"
                + " * distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                + " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                + " * See the License for the specific language governing permissions and\n"
                + " * limitations under the License.\n"
                + " */");
    }
}
