package ru.sobse.cloud_storage.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserDTO {
    private String login;
    private String password;
}
