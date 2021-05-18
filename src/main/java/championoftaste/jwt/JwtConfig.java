package championoftaste.jwt;

import com.google.common.net.HttpHeaders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter             // создаёт дефолтные геттеры автоматически для каждого поля
@Setter             // создаёт дефолтные сеттеры автоматически для каждого поля
@NoArgsConstructor  // создаёт конструктор без параметров
@Component
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {

    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfterDays;

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }
}
