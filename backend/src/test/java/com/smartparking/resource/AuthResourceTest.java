package com.smartparking.resource;

import com.smartparking.dto.LoginRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AuthResourceTest {
    @Test
    void loginSeedAdmin() {
        given().contentType(ContentType.JSON)
                .body("{\"email\":\"admin@smartparking.local\",\"password\":\"Admin@123\"}")
                .when().post("/api/auth/login")
                .then().statusCode(200)
                .body("token", notNullValue())
                .body("user.roles", hasItem("ADMIN"));
    }

    @Test
    void rejectBadPassword() {
        given().contentType(ContentType.JSON)
                .body("{\"email\":\"admin@smartparking.local\",\"password\":\"wrong-pass\"}")
                .when().post("/api/auth/login")
                .then().statusCode(401);
    }

    @Test
    void layoutRequiresAuth() {
        given().when().get("/api/parking-slots").then().statusCode(401);
    }
}
