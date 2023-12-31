package com.github.nekolr.slime.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;


/**
 * Global Variable Class
 */
@Table(name = "slime_sp_variable")
@Entity
@Getter
@Setter
@ToString
public class Variable {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    /**
     * 变量名称
     */
    private String name;

    /**
     * 变量值
     */
    private String val;

    /**
     * Description
     */
    private String description;

    /**
     * Answer the following questions as honestly as possible. If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
     */
    @Column(name = "create_time", insertable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Date createTime;
}
