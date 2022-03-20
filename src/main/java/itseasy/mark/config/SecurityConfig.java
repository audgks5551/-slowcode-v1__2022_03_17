package itseasy.mark.config;

import itseasy.mark.config.properties.CorsProperties;
import itseasy.mark.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import itseasy.mark.oauth.service.CustomOAuth2UserService;
import itseasy.mark.oauth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsProperties corsProperties;
    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling() // 예외처리 기능 작동
                .authenticationEntryPoint(null) // 인증처리 실패시 작동
                .accessDeniedHandler(null) // 인가처리 실패시 작동
        .and()
                .authorizeRequests()
                .antMatchers("/api/v1/auth/signup").permitAll()
                .antMatchers("/api/v1/auth/login").permitAll()
                .anyRequest().authenticated()
        .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
        .and()
                .redirectionEndpoint()
                .baseUri("/*/oauth2/code/*")
        .and()
                .userInfoEndpoint()
                .userService(oAuth2UserService);
    }

    /**
     * Cors 설정
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(","))); // 허용 헤더 정보
        corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(","))); // 허용 메서드
        corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(","))); // 허용 주소
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(corsConfig.getMaxAge());

        log.info("corsConfig.setAllowedHeaders = {}", Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
        log.info("corsConfig.setAllowedMethods = {}", Arrays.asList(corsProperties.getAllowedMethods().split(",")));
        log.info("corsConfig.setAllowedOrigins = {}", Arrays.asList(corsProperties.getAllowedOrigins().split(",")));

        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
        return corsConfigSource;
    }



    /**
     * auth 매니저 설정
     */
    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용
     */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /**
     * UserDetailsService 설정
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
