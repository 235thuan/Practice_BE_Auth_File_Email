package com.example.authenticationauthorization.repository;

import com.example.authenticationauthorization.model.Permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermisionRepository  extends JpaRepository<Permission,Long> {
    List<Permission> findAllById(Iterable<Long> permissionId);
}
