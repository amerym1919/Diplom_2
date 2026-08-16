package stellarburgers.client;

import io.restassured.response.Response;
import stellarburgers.model.User;
import stellarburgers.model.UserCredentials;

public class UserClient extends BaseClient {

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    public Response createUser(User user) {
        return request()
                .body(user)
                .post(REGISTER_PATH);
    }

    public Response loginUser(User user) {
        UserCredentials credentials = new UserCredentials(
                user.getEmail(),
                user.getPassword()
        );

        return request()
                .body(credentials)
                .post(LOGIN_PATH);
    }

    public void deleteUser(String accessToken) {
        if (accessToken != null) {
            request()
                    .header("Authorization", accessToken)
                    .delete(USER_PATH);
        }
    }
}
