package stellarburgers.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import stellarburgers.model.Order;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;

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
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getList("data._id");
    }

    public Response createOrder(String accessToken, List<String> ingredients) {
        RequestSpecification specification = request();

        if (accessToken != null) {
            specification.header("Authorization", accessToken);
        }

        Order order = new Order(ingredients);

        return specification
                .body(order)
                .post(ORDERS_PATH);
    }
}
