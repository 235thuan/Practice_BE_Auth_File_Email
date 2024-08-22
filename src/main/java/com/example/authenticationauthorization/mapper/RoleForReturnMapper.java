package com.example.authenticationauthorization.mapper;

import com.example.authenticationauthorization.dto.RoleRequestDTO;
import com.example.authenticationauthorization.dto.RoleResponseForReturnDTO;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleForReturnMapper {
    RoleForReturnMapper INSTANCE = Mappers.getMapper(RoleForReturnMapper.class);
    RoleResponseForReturnDTO mapRoleRequestDTOToRoleResponseForReturnDTO(RoleRequestDTO roleRequestDTO);
}
