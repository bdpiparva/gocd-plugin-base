/*
 * Copyright 2019 ThoughtWorks, Inc.
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

package cd.go.plugin.base.executors.scm.model;

import java.util.HashMap;

public class ScmData extends HashMap<String, Object> {
    public static ScmDataBuilder builder() {
        return new ScmDataBuilder();
    }

    public static class ScmDataBuilder {
        private final ScmData scmData;

        private ScmDataBuilder() {
            scmData = new ScmData();
        }

        public ScmDataBuilder add(String key, Object value) {
            scmData.put(key, value);
            return this;
        }

        public ScmData build() {
            return scmData;
        }
    }
}
