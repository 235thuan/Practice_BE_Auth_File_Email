package com.example.authenticationauthorization.controller;

import com.example.authenticationauthorization.dto.RoleRequestDTO;

import com.example.authenticationauthorization.dto.RoleResponseForCreateDTO;
import com.example.authenticationauthorization.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
public class RolesController {
    @Autowired
    private RoleService roleService;


    @PostMapping
    public ResponseEntity<RoleResponseForCreateDTO> create(@RequestBody RoleRequestDTO roleRequestDTO) {
        try {
            RoleResponseForCreateDTO roleResponseDTO = roleService.createRole(roleRequestDTO);
            return ResponseEntity.ok(roleResponseDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
