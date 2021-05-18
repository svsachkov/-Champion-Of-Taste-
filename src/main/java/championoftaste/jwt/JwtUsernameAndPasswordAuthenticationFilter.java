package championoftaste.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;

import lombok.AllArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Фильтр, получающий и проверяющий данные пользователя (логин, пароль).
 * Создающий токен и отправляющий его пользователю.
 */
@AllArgsConstructor // создает конструктор с 1 параметром для каждого поля класса
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    /**
     * Выполняет фактическую аутентификацию (send credentials -> validates credentials).
     *
     * @param request  запрос пользователя
     * @param response ответ
     * @throws AuthenticationException ошибка аутентификации
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            // Запрос, который проверяет правильность имени пользователя и пароля.
            UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),    // principal
                    authenticationRequest.getPassword()     // credentials
            );

            // AuthenticationManager проверит существует ли такое имя пользователя, а затем проверит верен ли пароль.
            // Если всё в порядке, запрос будет аутентифицирован.
            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Создаёт токен и отправляет его пользователю (вызывается только когда метод attemptAuthentication успешен).
     *
     * @param request    запрос
     * @param response   ответ
     * @param chain      цепочка вызовов фильтров
     * @param authResult результат аутентификации
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        // Создаём токен.
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(secretKey)
                .compact();

        // Отправляем токен пользователю.
        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);
        response.addHeader(
                "Role",
                authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse(null)
        );
    }
}
