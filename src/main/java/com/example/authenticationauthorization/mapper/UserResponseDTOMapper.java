package com.example.authenticationauthorization.mapper;


import com.example.authenticationauthorization.dto.UserResponseDTO;
import com.example.authenticationauthorization.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserResponseDTOMapper {
    UserResponseDTOMapper INSTANCE = Mappers.getMapper(UserResponseDTOMapper.class);

    UserResponseDTO toUserResponseDTO(User user);

    User toUser(UserResponseDTO userResponseDTO);
}
