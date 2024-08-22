package com.example.authenticationauthorization.dto;

import com.example.authenticationauthorization.model.Permission;
import lombok.Data;

import java.util.Set;

@Data
public class RoleResponseForCreateDTO {
    private String name;
    private Set<Permission> permissions;


    public RoleResponseForCreateDTO() {

    }

    public RoleResponseForCreateDTO(String name, Set<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
