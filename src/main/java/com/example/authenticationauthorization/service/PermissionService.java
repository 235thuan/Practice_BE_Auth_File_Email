package com.example.authenticationauthorization.service;

import com.example.authenticationauthorization.model.Permission;
import com.example.authenticationauthorization.repository.PermisionRepository;

import org.springframework.stereotype.Service;



@Service
public class PermissionService {

    private final PermisionRepository permisionRepository;

    public PermissionService(PermisionRepository permisionRepository) {
        this.permisionRepository = permisionRepository;
    }

    public Permission createPermission(Permission permission) {
        return permisionRepository.save(permission);
    }

}
