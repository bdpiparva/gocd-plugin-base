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

package com.github.bdpiparva.plugin.base.dispatcher;

import com.github.bdpiparva.plugin.base.executors.IconRequestExecutor;
import com.github.bdpiparva.plugin.base.executors.MetadataExecutor;
import com.github.bdpiparva.plugin.base.executors.ValidationExecutor;
import com.github.bdpiparva.plugin.base.executors.ViewRequestExecutor;
import com.github.bdpiparva.plugin.base.validation.DefaultValidator;
import com.github.bdpiparva.plugin.base.validation.Validator;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;

public class SecretPluginRequestDispatcherBuilder extends RequestDispatcherBuilder<SecretPluginRequestDispatcherBuilder> {
    protected static final String REQUEST_SECRETS_LOOKUP = "go.cd.secrets.secrets-lookup";
    protected static final String REQUEST_GET_CONFIG_METADATA = "go.cd.secrets.secrets-config.get-metadata";
    protected static final String REQUEST_GET_CONFIG_VIEW = "go.cd.secrets.secrets-config.get-view";
    protected static final String REQUEST_VALIDATE_CONFIG = "go.cd.secrets.secrets-config.validate";
    protected static final String REQUEST_VERIFY_CONNECTION = "go.cd.secrets.secrets-config.verify-connection";

    SecretPluginRequestDispatcherBuilder(GoApplicationAccessor accessor) {
        super(accessor);
    }

    public SecretPluginRequestDispatcherBuilder icon(String path, String contentType) {
        return register(REQUEST_GET_ICON, new IconRequestExecutor(path, contentType));
    }

    public SecretPluginRequestDispatcherBuilder configMetadata(Class<?> configClass) {
        register(REQUEST_VALIDATE_CONFIG, new ValidationExecutor(new DefaultValidator(configClass)));
        return register(REQUEST_GET_CONFIG_METADATA, new MetadataExecutor("", configClass));
    }

    public SecretPluginRequestDispatcherBuilder configView(String path) {
        return register(REQUEST_GET_CONFIG_VIEW, new ViewRequestExecutor(path));
    }

    public SecretPluginRequestDispatcherBuilder validateSecretConfig(Validator... validators) {
        if (dispatcherRegistry.containsKey(REQUEST_VALIDATE_CONFIG)) {
            ((ValidationExecutor) dispatcherRegistry.get(REQUEST_VALIDATE_CONFIG)).addAll(validators);
            return this;
        }

        return register(REQUEST_VALIDATE_CONFIG, new ValidationExecutor(validators));
    }

    public SecretPluginRequestDispatcherBuilder lookup(LookupExecutor executor) {
        return register(REQUEST_SECRETS_LOOKUP, executor);
    }
}
