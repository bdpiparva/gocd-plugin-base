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

package cd.go.plugin.base.executors.artifact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class FetchArtifactRequest<ArtifactMetadata, FetchArtifactConfig, ArtifactStoreConfig> {
    @Expose
    @SerializedName("artifact_metadata")
    private ArtifactMetadata artifactMetadata;

    @Expose
    @SerializedName("fetch_artifact_configuration")
    private FetchArtifactConfig fetchArtifactConfig;

    @Expose
    @SerializedName("store_configuration")
    private ArtifactStoreConfig artifactStoreConfig;

    @Expose
    @SerializedName("agent_working_directory")
    private String workingDir;
}
