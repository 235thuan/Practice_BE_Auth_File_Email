package com.example.authenticationauthorization.mapper;

import com.example.authenticationauthorization.dto.FileResponseDTO;
import com.example.authenticationauthorization.dto.UserResponseDTO;
import com.example.authenticationauthorization.model.StoredFile;
import com.example.authenticationauthorization.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface FileResponseDTOMapper {
    FileResponseDTOMapper INSTANCE = Mappers.getMapper(FileResponseDTOMapper.class);

    @Mapping(target = "fileId", source = "storedFile.id")
    FileResponseDTO toFileResponseDTO(StoredFile storedFile);


}
