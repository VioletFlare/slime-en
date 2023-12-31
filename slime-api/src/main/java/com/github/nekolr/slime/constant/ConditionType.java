package com.github.nekolr.slime.constant;

import lombok.Getter;

/**
 * Condition type
 */
public enum ConditionType {

    DIRECTION("Text to translate: Zunächst einmal möchte ich mich bei allen für die vielen Kommentare und Fragen bedanken, die wir seit gestern erhalten haben.", "0"),
    ON_EXCEPTION("When an exception occurs forward", "1"),
    NO_EXCEPTION("Rotate when no abnormalities are present", "2");

    /**
     * Description
     */
    @Getter
    String description;

    /**
     * Code
     */
    @Getter
    String code;

    ConditionType(String description, String code) {
        this.description = description;
        this.code = code;
    }
}
