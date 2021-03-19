package de.codecentric.reedelk.plugin.runconfig.runtime.parameter;

import de.codecentric.reedelk.plugin.runconfig.runtime.parameter.BaseParameterStrategy;
import com.intellij.execution.configurations.ParametersList;
import de.codecentric.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class BaseParameterStrategyTest {

    @Mock
    private RuntimeRunConfiguration runtimeRunConfiguration;

    private TestParameterStrategy strategy;

    @BeforeEach
    void setUp() {
         strategy = new TestParameterStrategy();
    }

    @Test
    void shouldCorrectlyConfigureAdditionalVMOptions() {
        // Given
        String additionalVMOption = "-Dtest.port.overridden=22331";
        doReturn(additionalVMOption).when(runtimeRunConfiguration).getVmOptions();

        ParametersList parametersList = new ParametersList();

        // When
        strategy.apply(parametersList, runtimeRunConfiguration);

        // Then
        List<String> list = parametersList.getList();
        assertThat(list).containsExactly("-Dtest.port.overridden=22331");
    }

    @Test
    void shouldCorrectlyConfigureAdminConsolePort() {
        // Given
        String adminConsolePort = "9911";
        doReturn(adminConsolePort).when(runtimeRunConfiguration).getRuntimePort();

        ParametersList parametersList = new ParametersList();

        // When
        strategy.apply(parametersList, runtimeRunConfiguration);

        // Then
        List<String> list = parametersList.getList();
        assertThat(list).containsExactly("-Dadmin.console.port=9911");
    }

    @Test
    void shouldCorrectlyConfigureAdminConsoleHost() {
        // Given
        String adminConsoleHost = "127.0.0.1";
        doReturn(adminConsoleHost).when(runtimeRunConfiguration).getRuntimeBindAddress();

        ParametersList parametersList = new ParametersList();

        // When
        strategy.apply(parametersList, runtimeRunConfiguration);

        // Then
        List<String> list = parametersList.getList();
        assertThat(list).containsExactly("-Dadmin.console.address=127.0.0.1");
    }

    static class TestParameterStrategy extends BaseParameterStrategy {
    }
}
