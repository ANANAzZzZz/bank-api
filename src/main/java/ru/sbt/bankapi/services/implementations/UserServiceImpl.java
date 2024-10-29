package ru.sbt.bankapi.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbt.bankapi.dto.UserRequestDto;
import ru.sbt.bankapi.dto.UserResponseDto;
import ru.sbt.bankapi.models.User;
import ru.sbt.bankapi.repositories.UserRepository;
import ru.sbt.bankapi.services.interfaces.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Получает все пользователей из базы данных.
     *
     * @return список всех пользователей в формате UserResponseDto.
     * Каждый элемент списка представляет собой объект UserResponseDto,
     * который содержит идентификатор пользователя, имя, фамилию, адрес электронной почты и роль.
     */
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().filter(user -> !user.getIs_deleted()).map(user -> new UserResponseDto(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getRole()
        )).collect(Collectors.toList());
    }

    /**
     * Сохраняет нового пользователя в базе данных.
     *
     * @param userDto объект UserRequestDto, содержащий информацию о новом пользователе.
     *                Должен содержать имя, фамилию, пароль, адрес электронной почты и роль.
     * @return опциональный объект UserResponseDto, содержащий информацию о сохраненном пользователе.
     * Если пользователь с таким адресом электронной почты уже существует, возвращается пустой опционал.
     */
    @Override
    @Transactional
    public Optional<UserResponseDto> saveUser(UserRequestDto userDto) {
        Optional<User> userOptional = userRepository.findByEmail(userDto.email());

        if (userOptional.isPresent()) {
            return Optional.empty();
        }

        User user = userRepository.save(new User(
            userDto.firstname(),
            userDto.lastname(),
            userDto.password(),
            userDto.email()
        ));

        return Optional.of(new UserResponseDto(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getRole()
        ));
    }

    /**
     * Удаляет пользователя из базы данных.
     *
     * @param userId идентификатор пользователя, которого нужно удалить.
     * @return ответный объект ResponseEntity с кодом состояния 200 и сообщением "Пользователь удален",
     * если пользователь успешно удален.
     * Если пользователь с таким идентификатором не найден, возвращается ответный объект с кодом состояния 404 и сообщением "Пользователь не найден".
     */
    @Override
    @Transactional
    public ResponseEntity<String> deleteUser(Long userId) {
        Optional<User> user = findById(userId);

        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        user.get().setIs_deleted(true);
        userRepository.save(user.get());

        return ResponseEntity.status(200).body("Пользователь удален");
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно найти.
     * @return опциональный объект UserRequestDto, содержащий информацию о найденном пользователе.
     * Если пользователь с таким идентификатором не найден, возвращается пустой опционал.
     */
    @Override
    public Optional<UserRequestDto> findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty() || user.get().getIs_deleted()) {
            return Optional.empty();
        }

        return Optional.of(new UserRequestDto(
            user.get().getId(),
            user.get().getFirstname(),
            user.get().getLastname(),
            user.get().getPassword(),
            user.get().getEmail(),
            user.get().getRole()
        ));
    }

    /**
     * Находит пользователя по его адресу электронной почты.
     *
     * @param id id пользователя, которого нужно найти.
     * @return опциональный объект User, содержащий информацию о найденном пользователе.
     * Если пользователь с таким адресом электронной почты не найден, возвращается пустой опционал.
     */
    @Override
    public Optional<User> findById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty() || user.get().getIs_deleted()) {
            return Optional.empty();
        }
        return user;
    }


    /**
     * Обновляет информацию о пользователе в базе данных.
     *
     * @param userDto объект UserRequestDto, содержащий новую информацию о пользователе.
     * Должен содержать идентификатор пользователя, имя, фамилию, пароль, адрес электронной почты и роль.
     * @return опциональный объект UserResponseDto, содержащий обновленную информацию о пользователе.
     * Если пользователь с таким идентификатором не найден, возвращается пустой опционал.
     */
    @Override
    @Transactional
    public Optional<UserResponseDto> updateUser(UserRequestDto userDto) {
        Optional<User> userOptional = findById(userDto.id());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        user.setFirstname(userDto.firstname());
        user.setLastname(userDto.lastname());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRole(userDto.role());

        userRepository.save(user);

        UserResponseDto result = new UserResponseDto(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getRole()
        );

        return Optional.of(result);
    }
}
