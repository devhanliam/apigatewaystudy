package com.study.gateway.database.persistence;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

@Table("users")
@Getter
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    private String roles;

    public List<? extends GrantedAuthority> getRolesStringToGrant() {
        if (this.roles.isEmpty()) {
            return null;
        }

        return Arrays.stream(this.roles.split("/"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
