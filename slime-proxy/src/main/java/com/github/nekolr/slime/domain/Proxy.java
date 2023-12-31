package com.github.nekolr.slime.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

/**
 * Proxy entity class
 */
@Table(name = "slime_sp_proxy")
@Entity
@Getter
@Setter
@ToString
public class Proxy {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    /**
     * IP Address
     */
    private String ip;

    /**
     * Port number
     */
    private Integer port;

    /**
     * Type
     */
    private String type;

    /**
     * Is it a secret
     */
    @ColumnDefault("false")
    private Boolean anonymous;

    /**
     * Certificate expiry
     */
    @Column(name = "valid_time", insertable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Date validTime;
}
