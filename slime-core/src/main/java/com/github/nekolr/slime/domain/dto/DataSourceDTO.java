package com.github.nekolr.slime.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@ToString
public class DataSourceDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * Data source name
     */
    @NotBlank(message = "The name of the data source cannot be empty", groups = Save.class)
    private String name;

    /**
     * 驱动类
     */
    @NotBlank(message = "The full class name of the driver cannot be empty.", groups = {Test.class, Save.class})
    private String driverClassName;

    /**
     * JDBC URL
     */
    @NotBlank(message = "The connection address for the database cannot be empty", groups = {Test.class, Save.class})
    private String jdbcUrl;

    /**
     * User Name
     */
    private String username;

    /**
     * Password
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * Answer the following questions as honestly as possible. If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
     */
    private Date createTime;


    public interface Test {
    }

    public interface Save {
    }
}
