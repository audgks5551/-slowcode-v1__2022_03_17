package itseasy.mark.api.controller;

import io.jsonwebtoken.Claims;
import itseasy.mark.api.dto.UserDto;
import itseasy.mark.api.vo.RequestUser;
import itseasy.mark.api.vo.ResponseDTO;
import itseasy.mark.api.vo.ResponseUser;
import itseasy.mark.config.properties.AppProperties;
import itseasy.mark.oauth.entity.RoleType;
import itseasy.mark.oauth.entity.UserPrincipal;
import itseasy.mark.service.UserService;
import itseasy.mark.token.AuthToken;
import itseasy.mark.token.AuthTokenProvider;
import itseasy.mark.token.UserRefreshToken;
import itseasy.mark.token.UserRefreshTokenRepository;
import itseasy.mark.utils.CookieUtil;
import itseasy.mark.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ModelMapper mapper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

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
    public ResponseEntity<ResponseDTO> login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody RequestUser user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );

        String username = user.getUsername();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                username,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUsername(username);
        if (userRefreshToken == null) {
            // 없는 경우 새로 등록
            userRefreshToken = new UserRefreshToken(username, refreshToken.getToken());
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
        } else {
            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(refreshToken.getToken());
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDTO.put(accessToken.getToken(), null)
        );
    }

    @GetMapping("/refresh")
    public ResponseEntity<ResponseDTO> refreshToken (HttpServletRequest request, HttpServletResponse response) {
        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
        if (!authToken.validate()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.put("", "")
            );
        }

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.put("", "")
            );
        }

        String userId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // refresh token
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        if (authRefreshToken.validate()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.put("", "")
            );
        }

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUsernameAndRefreshToken(userId, refreshToken);
        if (userRefreshToken == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDTO.put("", "")
            );
        }

        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                userId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(authRefreshToken.getToken());

            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDTO.put(newAccessToken.getToken(), null)
        );
    }
}
