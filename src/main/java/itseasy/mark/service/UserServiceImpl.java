package itseasy.mark.service;

import itseasy.mark.api.dto.UserDto;
import itseasy.mark.entity.UserEntity;
import itseasy.mark.oauth.entity.ProviderType;
import itseasy.mark.oauth.entity.RoleType;
import itseasy.mark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    /**
     * 유저 생성
     */
    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findOptionalByUsername(userDto.getUsername()).isPresent()) {
            /**
             * TODO 중복 아이디 예외처리 필요
             */
            return null;
        }

        UserEntity userEntity = mapper.map(userDto, UserEntity.class); // UserDto -> UserEntity
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        userEntity.setProviderType(ProviderType.LOCAL);
        userEntity.setRoleType(RoleType.USER);
        UserEntity savedUserEntity = userRepository.save(userEntity); // UserEntity -> DB

        return mapper.map(savedUserEntity, UserDto.class); // userEntity -> UserDto
    }
}
