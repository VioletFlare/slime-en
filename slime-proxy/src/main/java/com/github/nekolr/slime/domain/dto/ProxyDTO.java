package com.github.nekolr.slime.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
public class ProxyDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * IP Address
     */
    @NotBlank(message = "代理的 IP Address cannot be empty")
    private String ip;

    /**
     * Port number
     */
    @NotBlank(message = "The port number to proxy secure HTTP through is not set.")
    private Integer port;

    /**
     * Type
     */
    @NotBlank(message = "The type of the assistant page")
    private String type;

    /**
     * Is it a secret
     */
    private Boolean anonymous;

    /**
     * Certificate expiry
     */
    private Date validTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyDTO proxyDTO = (ProxyDTO) o;
        return Objects.equals(id, proxyDTO.id) && Objects.equals(ip, proxyDTO.ip) && Objects.equals(port, proxyDTO.port) && Objects.equals(type, proxyDTO.type) && Objects.equals(anonymous, proxyDTO.anonymous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, port, type, anonymous);
    }
}