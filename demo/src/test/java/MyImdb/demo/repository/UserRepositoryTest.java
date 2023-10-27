package MyImdb.demo.repository;

import MyImdb.demo.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void itShouldFindUserByUsername() {
        //Arrange
        String username = "admin";
        User user = new User(username, "admin", null);
        userRepository.save(user);

        //Act
        Optional<User> optUser = userRepository.findByUsername(user.getUsername());


        //Assert
        Assertions.assertThat(optUser.isPresent());
        assertEquals(username, optUser.get().getUsername());
    }
}