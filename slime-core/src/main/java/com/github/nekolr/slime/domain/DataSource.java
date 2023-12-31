package com.github.nekolr.slime.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

/**
 * Class Inheritance
 */
@Table(name = "slime_sp_database")
@Entity
@Getter
@Setter
@ToString
public class DataSource {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    /**
     * Data source name
     */
    private String name;

    /**
     * 驱动类
     */
    @Column(name = "driver_class_name")
    private String driverClassName;

    /**
     * JDBC URL
     */
    @Column(name = "jdbc_url")
    private String jdbcUrl;

    /**
     * User Name
     */
    private String username;

    /**
     * Password
     */
    private String password;

    /**
     * Answer the following questions as honestly as possible. If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
     */
    @Column(name = "create_time", insertable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Date createTime;
}
