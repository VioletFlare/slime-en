package com.github.nekolr.slime.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Crawling task entity
 */
@Table(name = "slime_sp_task")
@Entity
@Getter
@Setter
@ToString
public class SpiderTask {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    /**
     * 流程 ID
     */
    @Column(name = "flow_id")
    private Long flowId;

    /**
     * Start time
     */
    @Column(name = "begin_time")
    private Date beginTime;

    /**
     * End time
     */
    @Column(name = "end_time")
    private Date endTime;

}
