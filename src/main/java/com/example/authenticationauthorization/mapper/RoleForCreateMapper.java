package com.example.authenticationauthorization.mapper;

import com.example.authenticationauthorization.dto.RoleRequestDTO;
import com.example.authenticationauthorization.dto.RoleResponseForCreateDTO;

import com.example.authenticationauthorization.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;



@Mapper(componentModel = "spring")
public interface RoleForCreateMapper {
    RoleForCreateMapper INSTANCE = Mappers.getMapper(RoleForCreateMapper.class);
    RoleResponseForCreateDTO mapRoleToRoleResponseForCreateDTO(Role role);
    Role mapRoleRequestDTOToRole(RoleRequestDTO RoleRequestDTO);
}
