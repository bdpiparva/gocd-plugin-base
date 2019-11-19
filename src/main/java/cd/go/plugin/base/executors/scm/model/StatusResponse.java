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
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class StatusResponse {
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
    @Expose
    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("messages")
    private String[] messages = {};

    private StatusResponse(String status, String... messages) {
        this.status = status;
        this.messages = messages;
    }

    public static StatusResponse success(String... messages) {
        return new StatusResponse(SUCCESS, messages);
    }

    public static StatusResponse failure(String... messages) {
        return new StatusResponse(FAILURE, messages);
    }

    public boolean isOk() {
        return StringUtils.equals(status, SUCCESS);
    }
}
