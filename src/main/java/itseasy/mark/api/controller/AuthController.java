package itseasy.mark.api.controller;

import itseasy.mark.api.dto.UserDto;
import itseasy.mark.api.vo.RequestUser;
import itseasy.mark.api.vo.ResponseDTO;
import itseasy.mark.api.vo.ResponseUser;
import itseasy.mark.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ModelMapper mapper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody RequestUser user) {
        UserDto userDto = mapper.map(user, UserDto.class); // RequestUser -> UserDto
        UserDto savedUserDto = userService.createUser(userDto); // 유저 생성 서비스에 전달

        ResponseUser responseUser = mapper.map(savedUserDto, ResponseUser.class); // UserDto -> ResponseUser

        return ResponseEntity.status(CREATED).body(
                ResponseDTO.put(responseUser, null)
        );
    }

    @PostMapping("/login")
    public String login(@RequestBody RequestUser user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );

        System.out.println("authentication.getName() = " + authentication.getName());
        System.out.println("authentication.getDetails() = " + authentication.getDetails());
        System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());
        System.out.println("authentication.getCredentials() = " + authentication.getCredentials());
        System.out.println("authentication.getAuthorities() = " + authentication.getAuthorities());

        return "ok";
    }
}
