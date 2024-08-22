package com.example.authenticationauthorization.controller;


import com.example.authenticationauthorization.model.Permission;
import com.example.authenticationauthorization.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permission")
public class PermissionsController {
    @Autowired
    private PermissionService permissionService;

    @PostMapping
    public ResponseEntity<Permission> create(@RequestBody Permission permission){
        try {
            return ResponseEntity.ok(permissionService.createPermission(permission));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
