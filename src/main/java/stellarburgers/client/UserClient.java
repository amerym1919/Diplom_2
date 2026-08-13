package stellarburgers.client;

import io.restassured.response.Response;
import stellarburgers.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserClient extends BaseClient {

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    public Response createUser(User user) {
        return request()
                .body(createUserJson(user))
                .post(REGISTER_PATH);
    }

    public Response loginUser(User user) {
        String json = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                user.getEmail(),
                user.getPassword()
        );

        return request()
                .body(json)
                .post(LOGIN_PATH);
    }

    public void deleteUser(String accessToken) {
        if (accessToken != null) {
            request()
                    .header("Authorization", accessToken)
                    .delete(USER_PATH);
        }
    }

    private String createUserJson(User user) {

        List<String> fields = new ArrayList<>();

        if (user.getEmail() != null) {
            fields.add("\"email\":\"" + user.getEmail() + "\"");
        }

        if (user.getPassword() != null) {
            fields.add("\"password\":\"" + user.getPassword() + "\"");
        }

        if (user.getName() != null) {
            fields.add("\"name\":\"" + user.getName() + "\"");
        }

        return "{" + String.join(",", fields) + "}";
    }
}