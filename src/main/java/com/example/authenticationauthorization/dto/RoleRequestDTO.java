package com.example.authenticationauthorization.dto;



import lombok.Data;

import java.util.Set;
@Data
public class RoleRequestDTO {


    private String name;
    private Set<Long> permissionId;

    public Set<Long> getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Set<Long> permissionId) {
        this.permissionId = permissionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
