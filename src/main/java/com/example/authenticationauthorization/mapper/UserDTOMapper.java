package com.example.authenticationauthorization.mapper;


import com.example.authenticationauthorization.dto.UserDTO;
import com.example.authenticationauthorization.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
@Component
public interface UserDTOMapper {

    UserDTOMapper INSTANCE = Mappers.getMapper(UserDTOMapper.class);

    //    @Mapping(target = "username",ignore = true)
    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);
}
