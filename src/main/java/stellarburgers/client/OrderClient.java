package stellarburgers.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.stream.Collectors;

public class OrderClient extends BaseClient {

    private static final String INGREDIENTS_PATH = "/api/ingredients";
    private static final String ORDERS_PATH = "/api/orders";

    public Response getIngredients() {
        return request()
                .get(INGREDIENTS_PATH);
    }

    public List<String> getIngredientIds() {
        return getIngredients()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data._id");
    }

    public Response createOrder(String accessToken, List<String> ingredients) {
        RequestSpecification specification = request();

        if (accessToken != null) {
            specification.header("Authorization", accessToken);
        }

        String ingredientList = ingredients.stream()
                .map(ingredient -> "\"" + ingredient + "\"")
                .collect(Collectors.joining(","));

        String json = "{\"ingredients\":[" + ingredientList + "]}";

        return specification
                .body(json)
                .post(ORDERS_PATH);
    }
}