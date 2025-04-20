package MyImdb.demo.auth;

import MyImdb.demo.model.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthenticationResponse {
    private String response;
    private String token;
}
