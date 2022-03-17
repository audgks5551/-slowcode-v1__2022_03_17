package itseasy.mark.api.vo;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestUser {
    @NotNull(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "password cannot be null")
    private String password;
}
