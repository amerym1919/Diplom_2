package stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.generator.UserGenerator;
import stellarburgers.model.User;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Stellar Burgers API")
@Feature("Создание пользователя")
public class UserCreateTest {

    private final UserClient userClient = new UserClient();

    private String accessToken;

    @Test
    @Story("Создание уникального пользователя")
    @Description("Проверка успешной регистрации нового пользователя")
    public void createUniqueUserReturnsSuccess() {

        User user = UserGenerator.getRandomUser();

        Response response = userClient.createUser(user);

        accessToken = response.path("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @Story("Повторная регистрация")
    @Description("Нельзя зарегистрировать пользователя повторно")
    public void createExistingUserReturnsError() {

        User user = UserGenerator.getRandomUser();

        Response firstResponse = userClient.createUser(user);

        firstResponse.then()
                .statusCode(200);

        accessToken = firstResponse.path("accessToken");

        Response secondResponse = userClient.createUser(user);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Story("Регистрация без email")
    public void createUserWithoutEmailReturnsError() {

        User generatedUser = UserGenerator.getRandomUser();

        User user = new User(
                null,
                generatedUser.getPassword(),
                generatedUser.getName()
        );

        userClient.createUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("Email, password and name are required fields")
                );
    }

    @Test
    @Story("Регистрация без password")
    public void createUserWithoutPasswordReturnsError() {

        User generatedUser = UserGenerator.getRandomUser();

        User user = new User(
                generatedUser.getEmail(),
                null,
                generatedUser.getName()
        );

        userClient.createUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("Email, password and name are required fields")
                );
    }

    @Test
    @Story("Регистрация без name")
    public void createUserWithoutNameReturnsError() {

        User generatedUser = UserGenerator.getRandomUser();

        User user = new User(
                generatedUser.getEmail(),
                generatedUser.getPassword(),
                null
        );

        userClient.createUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("Email, password and name are required fields")
                );
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }
}