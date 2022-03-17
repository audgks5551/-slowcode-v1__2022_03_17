package itseasy.mark.service;

import itseasy.mark.api.dto.UserDto;
import itseasy.mark.entity.UserEntity;
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
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return null;
        }

        UserEntity userEntity = mapper.map(userDto, UserEntity.class); // UserDto -> UserEntity
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        UserEntity savedUserEntity = userRepository.save(userEntity); // UserEntity -> DB

        return mapper.map(savedUserEntity, UserDto.class); // userEntity -> UserDto
    }
}
