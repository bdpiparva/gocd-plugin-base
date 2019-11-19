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

package cd.go.plugin.base.dispatcher.scm;

import cd.go.plugin.base.GsonTransformer;
import cd.go.plugin.base.dispatcher.RequestDispatcher;
import cd.go.plugin.base.executors.scm.*;
import cd.go.plugin.base.executors.scm.model.*;
import cd.go.plugin.base.executors.scm.request.CheckConnectionRequest;
import cd.go.plugin.base.executors.scm.request.CheckoutRequest;
import cd.go.plugin.base.executors.scm.request.LatestRevisionRequest;
import cd.go.plugin.base.executors.scm.request.LatestRevisionSinceRequest;
import cd.go.plugin.base.test_helper.annotations.JsonSource;
import cd.go.plugin.base.validation.ValidationResult;
import cd.go.plugin.base.validation.Validator;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;

import static cd.go.plugin.base.GsonTransformer.asMap;
import static cd.go.plugin.base.ResourceReader.readResource;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class ScmBuilderV1Test {
    @Mock
    private GoPluginApiRequest request;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Nested
    class ScmConfig {
        @ParameterizedTest
        @JsonSource(jsonFiles = "/scm/scm-configuration-metadata.json")
        void shouldSupportGetScmConfiguration(String expectedJson) throws Exception {
            when(request.requestName()).thenReturn(ScmBuilderV1.REQUEST_GET_SCM_CONFIGURATION);
            final RequestDispatcher dispatcher = new ScmBuilderV1()
                    .scmConfig(DummyScmConfig.class, "/dummy-template.html", "GitPR")
                    .build();

            final GoPluginApiResponse response = dispatcher.dispatch(request);

            assertThat(response.responseCode()).isEqualTo(200);
            JSONAssert.assertEquals(expectedJson, response.responseBody(), true);
        }

        @Test
        void shouldSupportGetScmConfigurationView() throws Exception {
            when(request.requestName()).thenReturn(ScmBuilderV1.REQUEST_GET_SCM_VIEW);
            final RequestDispatcher dispatcher = new ScmBuilderV1()
                    .scmConfig(DummyScmConfig.class, "/dummy-template.html", "GitPR")
                    .build();

            final GoPluginApiResponse response = dispatcher.dispatch(request);

            Map<String, Object> responseBody = asMap(response.responseBody());

            assertThat(response.responseCode()).isEqualTo(200);
            assertThat(responseBody)
                    .hasSize(2)
                    .containsEntry("template", readResource("/dummy-template.html"))
                    .containsEntry("displayValue", "GitPR");
        }

        @ParameterizedTest
        @JsonSource(jsonFiles = "/scm/scm-validation-error.json")
        void shouldSupportDefaultValidation(String errorJson) throws Exception {
            when(request.requestName()).thenReturn(ScmBuilderV1.REQUEST_VALIDATE_SCM_CONFIGURATION);

            final RequestDispatcher dispatcher = new ScmBuilderV1()
                    .scmConfig(DummyScmConfig.class, "/dummy-template.html", "GitPR")
                    .build();

            final GoPluginApiResponse response = dispatcher.dispatch(request);

            assertThat(response.responseCode()).isEqualTo(200);
            JSONAssert.assertEquals(errorJson, response.responseBody(), true);
        }

        @Test
        void shouldSupportCustomValidation() throws Exception {
            when(request.requestName()).thenReturn(ScmBuilderV1.REQUEST_VALIDATE_SCM_CONFIGURATION);
            final Validator validator = mock(Validator.class);
            when(validator.validate(any())).thenReturn(new ValidationResult());

            final RequestDispatcher dispatcher = new ScmBuilderV1()
                    .scmConfig(DummyScmConfig.class, "/dummy-template.html", "GitPR", validator)
                    .build();

            final GoPluginApiResponse response = dispatcher.dispatch(request);

            assertThat(response.responseCode()).isEqualTo(200);
            verify(validator).validate(any());
        }
    }

    @ParameterizedTest
    @JsonSource(jsonFiles = "/scm/valid-scm-config.json")
    void shouldSupportCheckConnection(String scmConfig) throws Exception {
        when(request.requestName()).thenReturn(ScmBuilderV1.REQUEST_CHECK_CONNECTION);
        when(request.requestBody()).thenReturn(scmConfig);

        final RequestDispatcher dispatcher = new ScmBuilderV1()
                .checkConnection(new DummyCheckConnectionExecutor())
                .build();

        final GoPluginApiResponse response = dispatcher.dispatch(request);

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(GsonTransformer.asMap(response.responseBody()))
                .containsEntry("status", "success")
                .containsEntry("messages", singletonList("Done"));
    }

    @ParameterizedTest
    @JsonSource(jsonFiles = {
            "/scm/valid-scm-config.json",
            "/scm/latest-revision-response.json",
    })
    void shouldGetLatestRevision(String scmConfig, String expectedResponse) throws Exception {
        when(request.requestName()).thenReturn(ScmBuilderV1.REQUEST_LATEST_REVISION);
        when(request.requestBody()).thenReturn(scmConfig);

        final RequestDispatcher dispatcher = new ScmBuilderV1()
                .latestRevision(new DummyLatestRevisionExecutor())
                .build();

        final GoPluginApiResponse response = dispatcher.dispatch(request);

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedResponse, response.responseBody(), true);
    }

    @ParameterizedTest
    @JsonSource(jsonFiles = {
            "/scm/latest-revision-since-request.json",
            "/scm/latest-revision-since-response.json"
    })
    void shouldGetLatestRevisionSince(String requestBody, String expectedResponse) throws Exception {
        when(request.requestName()).thenReturn(ScmBuilderV1.REQUEST_LATEST_REVISION_SINCE);
        when(request.requestBody()).thenReturn(requestBody);

        final RequestDispatcher dispatcher = new ScmBuilderV1()
                .latestRevisionSince(new DummyLatestRevisionSinceExecutor())
                .build();

        final GoPluginApiResponse response = dispatcher.dispatch(request);

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedResponse, response.responseBody(), true);
    }

    @ParameterizedTest
    @JsonSource(jsonFiles = {
            "/scm/checkout-request.json",
            "/scm/checkout-response.json"
    })
    void shouldCheckoutToRevision(String requestBody, String expectedResponse) throws Exception {
        when(request.requestName()).thenReturn(ScmBuilderV1.REQUEST_CHECKOUT);
        when(request.requestBody()).thenReturn(requestBody);

        final RequestDispatcher dispatcher = new ScmBuilderV1()
                .checkout(new DummyCheckoutExecutor())
                .build();

        final GoPluginApiResponse response = dispatcher.dispatch(request);

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedResponse, response.responseBody(), true);
    }
}

class DummyCheckoutExecutor extends CheckoutExecutor<DummyScmConfig> {
    @Override
    protected StatusResponse execute(CheckoutRequest<DummyScmConfig> request) throws Exception {
        return StatusResponse.success("Successfully checked out to SCM revision provided");
    }
}

class DummyLatestRevisionSinceExecutor extends LatestRevisionSinceExecutor<DummyScmConfig> {
    @Override
    protected LatestRevisionSinceResponse execute(LatestRevisionSinceRequest<DummyScmConfig> request) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        ModifiedFile file = ModifiedFile.builder().action("added").fileName("readme.md").build();

        return LatestRevisionSinceResponse.builder()
                .revisions(Collections.singletonList(
                        Revision.builder()
                                .revision("revision-1")
                                .modifiedFiles(singletonList(file))
                                .revisionComment("comment")
                                .user("Sheldon Cooper")
                                .data(ScmData.builder().add("Foo", "bar").build())
                                .timestamp(sdf.parse("2019-11-18T10:53:34.490Z"))
                                .build()
                ))
                .scmData(ScmData.builder().add("Bar", "Baz").build())
                .build();
    }
}

class DummyLatestRevisionExecutor extends LatestRevisionExecutor<DummyScmConfig> {
    @Override
    protected LatestRevisionResponse execute(LatestRevisionRequest<DummyScmConfig> request) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        ModifiedFile file = ModifiedFile.builder().action("added").fileName("readme.md").build();

        return LatestRevisionResponse.builder()
                .revision(Revision.builder()
                        .revision("revision-1")
                        .modifiedFiles(singletonList(file))
                        .revisionComment("comment")
                        .user("Sheldon Cooper")
                        .data(ScmData.builder().add("Foo", "bar").build())
                        .timestamp(sdf.parse("2019-11-18T10:53:34.490Z"))
                        .build()
                )
                .scmData(ScmData.builder().add("Bar", "Baz").build())
                .build();
    }
}

class DummyCheckConnectionExecutor extends CheckConnectionExecutor<DummyScmConfig> {
    @Override
    protected StatusResponse execute(CheckConnectionRequest<DummyScmConfig> request) {
        assertThat(request.getScmConfiguration().url).isEqualTo("http://localhost.com");
        assertThat(request.getScmConfiguration().username).isEqualTo("user");
        assertThat(request.getScmConfiguration().password).isEqualTo("password");
        return StatusResponse.success("Done");
    }
}
