package itseasy.mark.repository;

import itseasy.mark.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findOptionalByUsername(String username);
    UserEntity findByUsername(String username);
}
