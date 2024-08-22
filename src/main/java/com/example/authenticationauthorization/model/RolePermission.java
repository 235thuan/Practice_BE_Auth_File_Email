package com.example.authenticationauthorization.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;



@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "role_permissions")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_permission_id")
    private Long rolePermissionId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "permission_id")
    private Long permissionId;

//    public Long getRolePermissionId() {
//        return rolePermissionId;
//    }
//
//    public Long getPermissionId() {
//        return permissionId;
//    }
}
