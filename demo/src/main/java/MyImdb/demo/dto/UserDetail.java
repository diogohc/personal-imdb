package MyImdb.demo.dto;

import MyImdb.demo.model.Role;

public record UserDetail(int userId, String username, Role role) {
}
