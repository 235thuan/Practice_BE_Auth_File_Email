package com.example.authenticationauthorization.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "roles")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    @Column(nullable = false, unique = true,name="role_name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )

    private Set<Permission> permissions;


    public String getName() {
        return name;
    }
    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Long getRoleId() {
        return roleId;
    }
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    public void setName(String name) {
        this.name = name;
    }
}
