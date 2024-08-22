package com.example.authenticationauthorization.dto;

import lombok.Data;

@Data
public class PermissionResponseDTO {
    private Long permissionId;
    private String namePermission;
    public PermissionResponseDTO(Long permissionId, String namePermission) {
        this.permissionId=permissionId;
        this.namePermission=namePermission;

    }
//
//    public Long getPermissionId() {
//        return permissionId;
//    }
//
//    public void setPermissionId(Long permissionId) {
//        this.permissionId = permissionId;
//    }
//
//    public String getNamePermission() {
//        return namePermission;
//    }
//
//    public void setNamePermission(String namePermission) {
//        this.namePermission = namePermission;
//    }


}
