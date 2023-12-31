package com.github.nekolr.slime.model;

import lombok.Getter;
import lombok.Setter;


/**
 * 3D
 */
@Getter
@Setter
public class Shape {

    /**
     * Graphical Name
     */
    private String name;

    /**
     * 3D-depth:
     */
    private String label;

    /**
     * 3D-depth:
     */
    private String title;

    /**
     * The link address of the graphic（可以是 BASE64 Encoded Image）
     */
    private String image;

    /**
     * Description
     */
    private String desc;
}
