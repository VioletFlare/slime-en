package com.github.nekolr.slime.security;

import com.github.nekolr.slime.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Sign the message and return it to the frontend's entity
 */
@Getter
@AllArgsConstructor
public class LoginVo implements Serializable {

    private String token;

    private User user;
}
