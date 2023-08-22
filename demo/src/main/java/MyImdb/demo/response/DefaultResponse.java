package MyImdb.demo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultResponse<T> {
    private HttpStatus status;
    private T data;
    private String errorCode;
    private String errorMessage;

    public DefaultResponse(String errorCode, String status) {
    }
}
