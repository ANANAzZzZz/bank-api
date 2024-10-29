package ru.sbt.bankapi.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbt.bankapi.dto.rq.AuthenticationRequest;
import ru.sbt.bankapi.dto.rq.RegisterRequest;
import ru.sbt.bankapi.dto.rs.AuthenticationResponse;
import ru.sbt.bankapi.models.Role;
import ru.sbt.bankapi.models.User;
import ru.sbt.bankapi.repositories.UserRepository;
import ru.sbt.bankapi.services.interfaces.AuthenticationService;
import ru.sbt.bankapi.services.interfaces.JwtService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtServiceImpl;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрирует нового пользователя.
     * Если пользователь с указанным адресом электронной почты уже зарегистрирован, возвращает {@link Optional#empty()}.
     *
     * @param request объект {@link RegisterRequest} с информацией о новом пользователе
     * @return {@link Optional} с {@link AuthenticationResponse}, если пользователь успешно зарегистрирован, иначе {@link Optional#empty()}
     */
    @Override
    @Transactional
    public Optional<AuthenticationResponse> register(RegisterRequest request) {
        Optional<User> userResult = userRepository.findByEmail(request.getEmail());
        if (userResult.isPresent()) {
            return Optional.empty();
        }

        var user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        userRepository.save(user);

        var jwtToken = jwtServiceImpl.generateToken(user);
        return Optional.of(AuthenticationResponse.builder()
            .token(jwtToken)
            .id(Math.toIntExact(user.getId()))
            .email(user.getEmail())
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .email(user.getEmail())
            .role(user.getRole())
            .build());
    }

    /**
     * Метод authenticate выполняет аутентификацию пользователя на основе предоставленных учетных данных.
     * Он сначала ищет пользователя в базе данных по электронной почте. Если пользователь не найден,
     * метод возвращает пустой Optional.
     * Если пользователь найден, метод выполняет аутентификацию с помощью authenticationManager.
     * Если аутентификация не удается, метод также возвращает пустой Optional.
     * Если аутентификация успешна, метод генерирует JWT-токен с помощью jwtService и создает объект
     * AuthenticationResponse с информацией о пользователе и токеном.
     * Метод возвращает Optional, содержащий объект AuthenticationResponse или пустой Optional,
     * если аутентификация не удалась.
     *
     * @param request объект AuthenticationRequest, содержащий электронную почту и пароль пользователя
     * @return Optional, содержащий объект AuthenticationResponse или пустой Optional, если аутентификация не удалась
     */
    @Override
    public Optional<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty() || user.get().getIs_deleted()) {
            return Optional.empty();
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var jwtToken = jwtServiceImpl.generateToken(user.get());

        return Optional.of(AuthenticationResponse.builder()
            .token(jwtToken)
            .id(Math.toIntExact(user.get().getId()))
            .email(user.get().getEmail())
            .firstname(user.get().getFirstname())
            .lastname(user.get().getLastname())
            .email(user.get().getEmail())
            .role(user.get().getRole())
            .build());
    }
}
