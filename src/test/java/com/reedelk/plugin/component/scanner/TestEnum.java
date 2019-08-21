package com.reedelk.plugin.component.scanner;

import com.reedelk.runtime.api.annotation.Default;
import com.reedelk.runtime.api.annotation.DisplayName;

@Default("VALUE1")
public enum TestEnum {
    @DisplayName("Value 1")
    VALUE1,
    VALUE2,
    @DisplayName("Value 3")
    VALUE3
}
