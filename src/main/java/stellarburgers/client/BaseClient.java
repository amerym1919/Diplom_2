package stellarburgers.client;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseClient {

    protected static final String BASE_URL =
            "https://stellarburgers.education-services.ru";

    protected RequestSpecification request() {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON);
    }
}