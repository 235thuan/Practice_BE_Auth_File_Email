package com.example.authenticationauthorization.service;

import com.example.authenticationauthorization.configuration.exception.RunTimeException.PermissionNotFoundException;
import com.example.authenticationauthorization.dto.RoleRequestDTO;
import com.example.authenticationauthorization.dto.RoleResponseForCreateDTO;
import com.example.authenticationauthorization.mapper.RoleForCreateMapper;
import com.example.authenticationauthorization.model.Permission;
import com.example.authenticationauthorization.model.Role;
import com.example.authenticationauthorization.repository.PermisionRepository;
import com.example.authenticationauthorization.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.stream.Collectors;

@Service
public class RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);


    private final RoleRepository roleRepository;

    private final PermisionRepository permisionRepository;

    public RoleService(RoleRepository roleRepository, PermisionRepository permisionRepository) {
        this.roleRepository = roleRepository;
        this.permisionRepository = permisionRepository;
    }

    private final RoleForCreateMapper roleMapper = RoleForCreateMapper.INSTANCE;


    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public Set<Role> getDefaultRole() {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role 'USER' not found"));
        return Collections.singleton(userRole);
    }
    //tao moi
    public RoleResponseForCreateDTO createRole(RoleRequestDTO roleRequestDTO) {

        Optional<Role> existingRole = roleRepository.findByName(roleRequestDTO.getName());
        if (existingRole.isPresent()) {
            // If the role already exists, return it or handle accordingly
            return roleMapper.mapRoleToRoleResponseForCreateDTO(existingRole.get());
        } else {
            // Check if the role is allowed to be created (only "USER" is allowed)
            if (!"USER".equalsIgnoreCase(roleRequestDTO.getName())) {
                throw new IllegalArgumentException("Only the 'USER' role can be created.");
            }

            // Map DTO to Role entity
            Role role = roleMapper.mapRoleRequestDTOToRole(roleRequestDTO);

            // Validate permissions
            Set<Permission> permissions = roleRequestDTO.getPermissionId().stream()
                    .map(permisionRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            if (permissions.size() != roleRequestDTO.getPermissionId().size()) {
                throw new PermissionNotFoundException("One or more permissions not found");
            }

            // Set permissions and save role
            role.setPermissions(permissions);
            Role savedRole = roleRepository.save(role);

            return roleMapper.mapRoleToRoleResponseForCreateDTO(savedRole);
        }

    }
}
