package com.github.nekolr.slime.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Plugin {

    /**
     * Name of the plugin
     */
    private String name;

    /**
     * Plugin URIs
     */
    private String url;
}
