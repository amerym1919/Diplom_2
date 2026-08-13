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
                .statusCode(200);

        accessToken = response.path("accessToken");
    }

    @Test
    @Story("Логин существующего пользователя")
    @Description("Успешный вход с правильным email и паролем")
    public void loginExistingUserReturnsSuccess() {

        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("accessToken", notNullValue());
    }

    @Test
    @Story("Логин с неверными данными")
    @Description("Вход с неправильным email и паролем")
    public void loginWithIncorrectCredentialsReturnsError() {

        User incorrectUser = new User(
                "wrong_" + user.getEmail(),
                "wrongPassword",
                user.getName()
        );

        userClient.loginUser(incorrectUser)
                .then()
                .statusCode(401)
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