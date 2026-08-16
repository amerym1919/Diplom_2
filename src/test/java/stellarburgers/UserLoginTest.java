package stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.generator.UserGenerator;
import stellarburgers.model.User;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Stellar Burgers API")
@Feature("Логин пользователя")
public class UserLoginTest {

    private final UserClient userClient = new UserClient();

    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserGenerator.getRandomUser();

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(SC_OK);

        accessToken = response.path("accessToken");
    }

    @Test
    @Story("Логин существующего пользователя")
    @Description("Успешный вход с правильным email и паролем")
    public void loginExistingUserReturnsSuccess() {
        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("accessToken", notNullValue());
    }

    @Test
    @Story("Логин с неверным email")
    @Description("Вход с неправильным email и правильным паролем")
    public void loginWithIncorrectEmailReturnsError() {
        User incorrectUser = new User(
                "wrong_" + user.getEmail(),
                user.getPassword(),
                user.getName()
        );

        userClient.loginUser(incorrectUser)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("email or password are incorrect")
                );
    }

    @Test
    @Story("Логин с неверным паролем")
    @Description("Вход с правильным email и неправильным паролем")
    public void loginWithIncorrectPasswordReturnsError() {
        User incorrectUser = new User(
                user.getEmail(),
                "wrongPassword",
                user.getName()
        );

        userClient.loginUser(incorrectUser)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("email or password are incorrect")
                );
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }
}
