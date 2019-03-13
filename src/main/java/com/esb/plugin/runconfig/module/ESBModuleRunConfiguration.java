package com.esb.plugin.runconfig.module;

import com.esb.internal.rest.api.InternalAPI;
import com.esb.internal.rest.api.module.v1.ModulePOSTReq;
import com.esb.plugin.service.runtime.ESBRuntimeService;
import com.esb.plugin.utils.ESBNotification;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import com.intellij.notification.NotificationsManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.plexus.util.StringInputStream;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ESBModuleRunConfiguration extends RunConfigurationBase implements RunConfigurationWithSuppressedDefaultRunAction {

    private static final String PREFIX = "ESBModuleRunConfiguration-";
    private static final String RUNTIME_HOME_DIRECTORY = PREFIX + "HomeDirectory";
    private static final String MODULE_NAME = PREFIX + "ModuleName";

    private String runtimePath;
    private String moduleName;

    protected ESBModuleRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new ESBModuleRunConfigurationSettings(getProject());
    }

    @Override
    public boolean canRunOn(@NotNull ExecutionTarget target) {
        return true;
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        // If executor is undeploy, then undeploy, otherwise deploy
        String module = getModule();
        Module moduleByName = ModuleManager.getInstance(getProject()).findModuleByName(module);
        ESBRuntimeService runtimeService = ServiceManager.getService(ESBRuntimeService.class);
        return (executor1, runner) -> {

            String moduleBaseDir = moduleByName.getModuleFile().getParent().getPath();
            String url = "http://localhost:9988/module";

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/json");

            BasicHttpEntity entity = new BasicHttpEntity();

            ModulePOSTReq req = new ModulePOSTReq();
            req.setModuleFilePath("file:/Users/lorenzo/Desktop/esb-project/modules/flow-internal-tests/target/flow-internal-tests-1.0.0-SNAPSHOT.jar");
            String json = InternalAPI.Module.V1.POST.Req.serialize(req);
            entity.setContentLength(json.getBytes().length);
            entity.setContent(new ByteArrayInputStream(json.getBytes()));
            post.setEntity(entity);
            HttpResponse response;
            try {
                response = client.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = null;
            try {
                rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            StringBuffer result = new StringBuffer();
            String line = "";
            while (true) {
                try {
                    if ((line = rd.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result.append(line);
            }

            ToolWindowManager
                    .getInstance(getProject())
                    .getToolWindow(ToolWindowId.RUN).show(null);


            ESBNotification.notifyInfo(result.toString(), getProject());
            return null;
        };
    }


    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        runtimePath = JDOMExternalizerUtil.readField(element, RUNTIME_HOME_DIRECTORY);
        moduleName = JDOMExternalizerUtil.readField(element, MODULE_NAME);
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, RUNTIME_HOME_DIRECTORY, runtimePath);
        JDOMExternalizerUtil.writeField(element, MODULE_NAME, moduleName);
    }


    public String getRuntimePath() {
        return runtimePath;
    }

    public void setRuntimePath(String runtimePath) {
        this.runtimePath = runtimePath;
    }

    public void setModule(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModule() {
        return moduleName;
    }
}
