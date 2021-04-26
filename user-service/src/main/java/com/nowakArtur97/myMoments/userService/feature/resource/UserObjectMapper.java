package com.nowakArtur97.myMoments.userService.feature.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserObjectMapper {

    private final ObjectMapper objectMapper;

    public <T extends UserDTO> UserDTO getUserDTOFromString(String userAsString, Class<T> clazz) {

        if (userAsString == null) {
            return returnDefaultValue(clazz);
        }

        try {
            return objectMapper.readValue(userAsString, clazz);

        } catch (JsonProcessingException e) {
            return returnDefaultValue(clazz);
        }
    }

    private <T extends UserDTO> UserDTO returnDefaultValue(Class<T> clazz) {
        if (clazz == UserRegistrationDTO.class) {
            return new UserRegistrationDTO();
        } else {
            return new UserUpdateDTO();
        }
    }
}
