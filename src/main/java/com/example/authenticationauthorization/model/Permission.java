package com.example.authenticationauthorization.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "permissions") // Use name attribute to specify table name
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id") // Use name attribute to specify column name
    private Long permissionId;

    @Column(name="name_permission",nullable = false, unique = true)
    private String namePermission;

    public String getNamePermission() {
        return namePermission;
    }

    public void setNamePermission(String namePermission) {
        this.namePermission = namePermission;
    }

    public Long getPermissionId() {
        return permissionId;
    }
}
