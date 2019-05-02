package com.github.bdpiparva.plugin.base.dispatcher.notification;

import com.github.bdpiparva.plugin.base.dispatcher.RequestDispatcher;
import com.github.bdpiparva.plugin.base.executors.notification.AgentStatusNotificationExecutor;
import com.github.bdpiparva.plugin.base.executors.notification.StageStatusNotificationExecutor;
import com.github.bdpiparva.plugin.base.executors.notification.models.AgentStatusRequest;
import com.github.bdpiparva.plugin.base.executors.notification.models.StageStatusRequest;
import com.github.bdpiparva.plugin.base.validation.ValidationError;
import com.github.bdpiparva.plugin.base.validation.ValidationResult;
import com.github.bdpiparva.plugin.base.validation.Validator;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.github.bdpiparva.plugin.base.GsonTransformer.fromJson;
import static com.github.bdpiparva.plugin.base.dispatcher.notification.NotificationBuilderV4.*;
import static com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class NotificationBuilderV4Test {
    @Mock
    private GoPluginApiRequest request;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void shouldSupportGetPluginSettingsMetadata() throws UnhandledRequestTypeException {
        when(request.requestName()).thenReturn(REQUEST_GET_PLUGIN_SETTINGS_METADATA);
        RequestDispatcher requestDispatcher = new NotificationBuilderV4()
                .pluginSettings(NotificationConfig.class)
                .build();

        GoPluginApiResponse response = requestDispatcher.dispatch(request);

        assertThat(response.responseCode()).isEqualTo(200);
    }

    @Test
    void shouldAddDefaultValidator() throws UnhandledRequestTypeException {
        when(request.requestName()).thenReturn(REQUEST_VALIDATE_PLUGIN_SETTINGS);
        when(request.requestBody()).thenReturn("{\"plugin-settings\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}");
        RequestDispatcher requestDispatcher = new NotificationBuilderV4()
                .pluginSettings(NotificationConfig.class)
                .build();
        final Type type = new TypeToken<ArrayList<ValidationError>>() {
        }.getType();

        GoPluginApiResponse response = requestDispatcher.dispatch(request);
        ArrayList<ValidationError> errors = fromJson(response.responseBody(), type);

        assertThat(response.responseCode()).isEqualTo(412);
        assertThat(errors.size()).isEqualTo(2);
    }

    @Test
    void shouldNotAddDefaultValidator() throws UnhandledRequestTypeException {
        when(request.requestName()).thenReturn(REQUEST_VALIDATE_PLUGIN_SETTINGS);
        when(request.requestBody()).thenReturn("{\"key\":\"value\"}");
        RequestDispatcher requestDispatcher = new NotificationBuilderV4()
                .pluginSettings(NotificationConfig.class, false)
                .build();

        final Type type = new TypeToken<ArrayList<ValidationError>>() {
        }.getType();

        GoPluginApiResponse response = requestDispatcher.dispatch(request);
        ArrayList<ValidationError> errors = fromJson(response.responseBody(), type);

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(errors.size()).isEqualTo(0);
    }

    @Test
    void shouldSupportGetPluginSettingsView() throws UnhandledRequestTypeException {
        when(request.requestName()).thenReturn(REQUEST_GET_PLUGIN_SETTINGS_VIEW);
        RequestDispatcher requestDispatcher = new NotificationBuilderV4()
                .pluginSettingsView("/dummy-template.html")
                .build();

        GoPluginApiResponse response = requestDispatcher.dispatch(request);
        assertThat(response.responseCode()).isEqualTo(200);
    }

    @Test
    void shouldSupportValidatePluginSettings() throws UnhandledRequestTypeException {
        when(request.requestName()).thenReturn(REQUEST_VALIDATE_PLUGIN_SETTINGS);
        when(request.requestBody()).thenReturn("{\"plugin-settings\":{\"key-one\":{\"value\":\"value-one\"},\"key-two\":{\"value\":\"value-two\"}}}");
        Validator validator = mock(Validator.class);
        when(validator.validate(anyMap())).thenReturn(new ValidationResult());

        RequestDispatcher requestDispatcher = new NotificationBuilderV4()
                .validatePluginSettings(validator)
                .build();

        GoPluginApiResponse response = requestDispatcher.dispatch(request);
        assertThat(response.responseCode()).isEqualTo(200);
        verify(validator).validate(any());
    }

    @Nested
    class notificationInterestedIn {
        @BeforeEach
        void setUp() {
            when(request.requestName()).thenReturn(REQUEST_NOTIFICATIONS_INTERESTED_IN);
            when(request.requestBody()).thenReturn("{}");
        }

        @Test
        void shouldSupportNotificationInterestedIn() throws UnhandledRequestTypeException {
            RequestDispatcher requestDispatcher = new NotificationBuilderV4()
                    .notificationInterestedIn(NotificationType.AGENT_STATUS)
                    .build();

            GoPluginApiResponse response = requestDispatcher.dispatch(request);

            assertThat(response.responseCode()).isEqualTo(200);
            assertThat(response.responseBody()).isEqualTo("[\"agent-status\"]");
        }

        @Test
        void shouldErrorOutIfNoNotificationTypeIsSpecified() {
            assertThatCode(() -> new NotificationBuilderV4().notificationInterestedIn())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Provide at least one notification type!");
        }
    }

    @Test
    void shouldSupportStageStatusNotificationExecutor() throws UnhandledRequestTypeException {
        when(request.requestName()).thenReturn(REQUEST_STAGE_STATUS);
        when(request.requestBody()).thenReturn("{}");
        RequestDispatcher requestDispatcher = new NotificationBuilderV4()
                .stageStatus(new StageStatusNotificationExecutor() {
                    @Override
                    protected GoPluginApiResponse execute(StageStatusRequest stageStatusRequest) {
                        return success("stage-status-response");
                    }
                })
                .build();

        GoPluginApiResponse response = requestDispatcher.dispatch(request);

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("stage-status-response");
    }

    @Test
    void shouldSupportAgentStatusNotificationExecutor() throws UnhandledRequestTypeException {
        when(request.requestName()).thenReturn(REQUEST_AGENT_STATUS);
        when(request.requestBody()).thenReturn("{}");
        RequestDispatcher requestDispatcher = new NotificationBuilderV4()
                .agentStatus(new AgentStatusNotificationExecutor() {
                    @Override
                    protected GoPluginApiResponse execute(AgentStatusRequest stageStatusRequest) {
                        return success("agent-status-response");
                    }
                })
                .build();

        GoPluginApiResponse response = requestDispatcher.dispatch(request);

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("agent-status-response");
    }
}

class NotificationConfig {

}