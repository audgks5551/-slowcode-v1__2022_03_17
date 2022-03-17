package itseasy.mark.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String username;
    private String name;
    private String password;

    private String encryptedPwd;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
