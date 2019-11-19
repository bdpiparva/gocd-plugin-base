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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Getter
@Builder
@Accessors(fluent = true)
public class Revision {
    @Expose
    @SerializedName("revision")
    private String revision;
    @Expose
    @SerializedName("timestamp")
    private Date timestamp;
    @Expose
    @SerializedName("user")
    private String user;
    @Expose
    @SerializedName("revision_comment")
    private String revisionComment;
    @Expose
    @SerializedName("data")
    private ScmData data;
    @Expose
    @SerializedName("modified_files")
    private List<ModifiedFile> modifiedFiles;
}
