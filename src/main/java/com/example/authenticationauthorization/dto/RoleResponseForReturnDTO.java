package com.example.authenticationauthorization.dto;



import java.util.Set;


public class RoleResponseForReturnDTO {
    private String name;
    private Set<String> permissions;

    public RoleResponseForReturnDTO(String name, Set<String> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public RoleResponseForReturnDTO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
