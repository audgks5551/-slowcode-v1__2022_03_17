package itseasy.mark.api.controller;

import itseasy.mark.api.dto.UserDto;
import itseasy.mark.api.vo.RequestUser;
import itseasy.mark.api.vo.ResponseDTO;
import itseasy.mark.api.vo.ResponseUser;
import itseasy.mark.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ModelMapper mapper;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody RequestUser user) {
        UserDto userDto = mapper.map(user, UserDto.class); // RequestUser -> UserDto
        UserDto savedUserDto = userService.createUser(userDto); // 유저 생성 서비스에 전달

        ResponseUser responseUser = mapper.map(savedUserDto, ResponseUser.class); // UserDto -> ResponseUser

        return ResponseEntity.status(CREATED).body(
                ResponseDTO.put(responseUser, null)
        );
    }
}
