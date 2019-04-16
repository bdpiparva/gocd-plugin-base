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

package com.github.bdpiparva.plugin.base.executors;

import com.github.bdpiparva.plugin.base.GsonTransformer;
import com.github.bdpiparva.plugin.base.metadata.MetadataExtractor;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class MetadataExecutor implements Executor {
    private final String key;
    private final Class<?> metadataClass;
    private final MetadataExtractor metadataExtractor;

    public MetadataExecutor(String key, Class<?> metadataClass) {
        this.key = key;
        this.metadataClass = metadataClass;
        metadataExtractor = new MetadataExtractor();
    }

    @Override
    public GoPluginApiResponse execute(GoPluginApiRequest request) {
        return DefaultGoPluginApiResponse.success(GsonTransformer.toJson(metadataExtractor.forClass(metadataClass)));
    }
}
