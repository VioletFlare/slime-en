package com.github.nekolr.slime.constant;

import lombok.Getter;

/**
 * Output type
 */
public enum OutputType {

    DATABASE("output-database"),
    CSV("output-csv");

    @Getter
    private String variableName;

    OutputType(String variableName) {
        this.variableName = variableName;
    }
}
