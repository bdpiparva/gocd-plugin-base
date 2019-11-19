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

package scm;

import cd.go.plugin.base.dispatcher.BaseBuilder;
import cd.go.plugin.base.dispatcher.RequestDispatcher;
import cd.go.plugin.base.executors.scm.CheckConnectionExecutor;
import cd.go.plugin.base.executors.scm.CheckoutExecutor;
import cd.go.plugin.base.executors.scm.LatestRevisionExecutor;
import cd.go.plugin.base.executors.scm.LatestRevisionSinceExecutor;
import cd.go.plugin.base.executors.scm.model.LatestRevisionResponse;
import cd.go.plugin.base.executors.scm.model.LatestRevisionSinceResponse;
import cd.go.plugin.base.executors.scm.model.StatusResponse;
import cd.go.plugin.base.executors.scm.request.CheckConnectionRequest;
import cd.go.plugin.base.executors.scm.request.CheckoutRequest;
import cd.go.plugin.base.executors.scm.request.LatestRevisionRequest;
import cd.go.plugin.base.executors.scm.request.LatestRevisionSinceRequest;
import cd.go.plugin.base.validation.ValidationResult;
import cd.go.plugin.base.validation.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Map;

import static java.util.Collections.singletonList;

@Extension
public class ScmPlugin implements GoPlugin {
    private RequestDispatcher requestDispatcher;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        requestDispatcher = BaseBuilder.forScm()
                .v1()
                .scmConfig(ScmConfig.class, "/scm-config-view.html", "Git", new UrlValidator())
                .checkConnection(new ExampleCheckConnectionExecutor())
                .latestRevision(new ExampleLatestRevisionExecutor())
                .latestRevisionSince(new ExampleLatestRevisionSinceExecutor())
                .checkout(new ExampleCheckoutExecutor())
                .build();
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        try {
            return requestDispatcher.dispatch(request);
        } catch (Exception e) {
            // Implement Me!!
            throw new RuntimeException(e);
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("scm", singletonList("1.0"));
    }

    private static class ExampleLatestRevisionExecutor extends LatestRevisionExecutor<ScmConfig> {

        @Override
        protected LatestRevisionResponse execute(LatestRevisionRequest<ScmConfig> scmConfig) throws Exception {
            return null;
        }
    }

    private static class ExampleLatestRevisionSinceExecutor extends LatestRevisionSinceExecutor<ScmConfig> {
        @Override
        protected LatestRevisionSinceResponse execute(LatestRevisionSinceRequest<ScmConfig> request) throws Exception {
            return null;
        }
    }

    private static class ExampleCheckoutExecutor extends CheckoutExecutor<ScmConfig> {
        @Override
        protected StatusResponse execute(CheckoutRequest<ScmConfig> request) throws Exception {
            return null;
        }
    }
}

class ScmConfig {
    @Expose
    @SerializedName("scm_url")
    private String url;

    @Expose
    @SerializedName("username")
    private String username;

    @Expose
    @SerializedName("password")
    private String password;
}

class UrlValidator implements Validator {
    @Override
    public ValidationResult validate(Map<String, String> requestBody) {
        // Implement Me!!
        return null;
    }
}


class ExampleCheckConnectionExecutor extends CheckConnectionExecutor<ScmConfig> {
    @Override
    protected StatusResponse execute(CheckConnectionRequest<ScmConfig> scmConfig) throws Exception {
        // Implement Me!!
        return null;
    }
}
