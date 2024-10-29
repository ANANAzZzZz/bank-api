package ru.sbt.bankapi.services.interfaces;

import org.springframework.http.ResponseEntity;
import ru.sbt.bankapi.dto.UserRequestDto;
import ru.sbt.bankapi.dto.UserResponseDto;
import ru.sbt.bankapi.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    Optional<UserResponseDto> saveUser(UserRequestDto userDto);

    ResponseEntity<String> deleteUser(Long userId);

    Optional<UserRequestDto> findUserById(Long id);

    Optional<User> findById(Long id);

    Optional<UserResponseDto> updateUser(UserRequestDto userDto);
}
