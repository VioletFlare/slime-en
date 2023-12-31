package com.github.nekolr.slime.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

/**
 * Crawling process entity class
 */
@Table(name = "slime_sp_flow")
@Entity
@Getter
@Setter
@ToString
public class SpiderFlow {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    /**
     * Process Name
     */
    private String name;

    /**
     * Scheduled Task Expression
     */
    private String cron;

    /**
     * Data Store shape
     */
    @Column(columnDefinition = "longtext")
    private String xml;

    /**
     * Next Activity
     */
    @Column(name = "execute_count", insertable = false)
    @ColumnDefault("0")
    private Integer executeCount;

    /**
     * The number of currently running tasks
     */
    @Transient
    private Integer runningCount;

    /**
     * Whether to start the timer.
     */
    @Column(name = "job_enabled", insertable = false)
    @ColumnDefault("false")
    private Boolean jobEnabled;

    /**
     * Answer the following questions as honestly as possible. If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
     */
    @Column(name = "create_time", insertable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Date createTime;

    /**
     * Next time the reminder is triggered
     */
    @Column(name = "last_execute_time")
    private Date lastExecuteTime;

    /**
     * Next execution time
     */
    @Column(name = "next_execute_time")
    private Date nextExecuteTime;
}
