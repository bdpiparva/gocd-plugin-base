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

package cd.go.plugin.base.executors.scm;

import cd.go.plugin.base.executors.scm.model.StatusResponse;
import cd.go.plugin.base.executors.scm.request.CheckoutRequest;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static cd.go.plugin.base.GsonTransformer.fromJson;

public abstract class CheckoutExecutor<T> extends ScmExecutor<CheckoutRequest<T>, StatusResponse> {

    @Override
    protected CheckoutRequest<T> parseRequest(String requestBody) {
        Type type = new TypeToken<CheckoutRequest<T>>() {
        }.getType();

        CheckoutRequest<T> request = fromJson(requestBody, type);
        request.setScmConfiguration(parseScmConfiguration(requestBody, getGenericClassType(this)));
        return request;
    }
}
