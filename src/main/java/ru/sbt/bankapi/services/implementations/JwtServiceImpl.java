package ru.sbt.bankapi.services.implementations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.sbt.bankapi.models.User;
import ru.sbt.bankapi.repositories.UserRepository;
import ru.sbt.bankapi.services.interfaces.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private static String SECRET_KEY;
    private final UserRepository userRepository;

    /**
     * Метод для установки секретного ключа для JWT-сервиса.
     *
     * @param secretKey секретный ключ, который будет установлен в SECRET_KEY JWT-сервиса
     */
    @Value("${secret.key}")
    public void setSecretKey(String secretKey) {
        JwtServiceImpl.SECRET_KEY = secretKey;
    }

    /**
     * Метод для извлечения имени пользователя из JWT-токена.
     *
     * @param token JWT-токен, из которого нужно извлечь имя пользователя
     * @return имя пользователя, извлеченное из JWT-токена
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Метод для генерации нового JWT-токена для указанного пользователя.
     *
     * @param userDetails пользователь, для которого нужно сгенерировать токен
     * @return новый JWT-токен, сгенерированный с учетом дополнительных утверждений и данных пользователя
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Метод для получения секретного ключа для подписи JWT-токена.
     *
     * @return секретный ключ для подписи JWT-токена
     */
    @Override
    public String generateToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails
    ) {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Метод для проверки, не истек ли JWT-токен.
     *
     * @param token JWT-токен, который нужно проверить
     * @return true, если токен не истек, false в противном случае
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Проверяет, истек ли токен.
     *
     * @param token - токен, который нужно проверить.
     * @return true, если токен истек, false в противном случае.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения токена
     *
     * @param token - токен, из которого нужно извлечь дату.
     * @return дата истечения токена
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает все утверждения из токена.
     *
     * @param token - токен, из которого нужно извлечь все утверждения.
     * @return объект Claims, содержащий все утверждения из токена.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Метод extractAllClaims используется для извлечения всех утверждений из JWT токена.
     *
     * @param token - строка, представляющая JWT токен.
     * @return Claims - объект, содержащий все утверждения из токена.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Метод getSignInKey используется для получения ключа подписи JWT токена.
     *
     * @return Key - объект, представляющий ключ подписи.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Этот метод используется для поиска пользователя по его адресу электронной почты для авторизации.
     *
     * @param email Адрес электронной почты пользователя, которого нужно найти.
     * @return Опциональное значение, содержащее пользователя, если он найден, или пустое значение, если он не найден.
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
