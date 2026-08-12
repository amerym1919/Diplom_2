package stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.OrderClient;
import stellarburgers.client.UserClient;
import stellarburgers.generator.UserGenerator;
import stellarburgers.model.User;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreateTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private String accessToken;

    private static final String INVALID_INGREDIENT_HASH =
            "invalid_ingredient_hash";

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        accessToken = null;
    }

    @Test
    @Story("Создание заказа с авторизацией")
    @Description("Авторизованный пользователь создаёт заказ с ингредиентами")
    public void createOrderWithAuthorizationAndIngredientsReturnsSuccess() {
        createUserAndGetToken();

        List<String> ingredients = orderClient.getIngredientIds();

        Response response = orderClient.createOrder(
                accessToken,
                ingredients
        );

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Story("Создание заказа с авторизацией")
    @Description("Авторизованный пользователь создаёт заказ без ингредиентов")
    public void createOrderWithAuthorizationWithoutIngredientsReturnsError() {
        createUserAndGetToken();

        Response response = orderClient.createOrder(
                accessToken,
                Collections.emptyList()
        );

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("Ingredient ids must be provided")
                );
    }

    @Test
    @Story("Создание заказа с авторизацией")
    @Description("Авторизованный пользователь отправляет неверный хеш ингредиента")
    public void createOrderWithAuthorizationAndInvalidIngredientHashReturnsError() {
        createUserAndGetToken();

        Response response = orderClient.createOrder(
                accessToken,
                Collections.singletonList(INVALID_INGREDIENT_HASH)
        );

        response.then()
                .statusCode(500);

        org.junit.Assert.assertFalse(
                response.getBody().asString().isEmpty()
        );
    }

    @Test
    @Story("Создание заказа без авторизации")
    @Description("Неавторизованный пользователь создаёт заказ с ингредиентами")
    public void createOrderWithoutAuthorizationAndWithIngredientsReturnsSuccess() {
        List<String> ingredients = orderClient.getIngredientIds();

        Response response = orderClient.createOrder(
                null,
                ingredients
        );

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Story("Создание заказа без авторизации")
    @Description("Неавторизованный пользователь создаёт заказ без ингредиентов")
    public void createOrderWithoutAuthorizationAndWithoutIngredientsReturnsError() {
        Response response = orderClient.createOrder(
                null,
                Collections.emptyList()
        );

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("Ingredient ids must be provided")
                );
    }

    @Test
    @Story("Создание заказа без авторизации")
    @Description("Неавторизованный пользователь отправляет неверный хеш ингредиента")
    public void createOrderWithoutAuthorizationAndInvalidIngredientHashReturnsError() {
        Response response = orderClient.createOrder(
                null,
                Collections.singletonList(INVALID_INGREDIENT_HASH)
        );

        response.then()
                .statusCode(500);

        org.junit.Assert.assertFalse(
                response.getBody().asString().isEmpty()
        );
    }

    private void createUserAndGetToken() {
        User user = UserGenerator.getRandomUser();

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(200);

        accessToken = response.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}