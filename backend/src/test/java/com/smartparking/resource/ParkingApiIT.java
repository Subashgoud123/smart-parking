package com.smartparking.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
class ParkingApiIT {
    private String token() {
        return given().contentType(ContentType.JSON)
                .body("{\"email\":\"admin@smartparking.local\",\"password\":\"Admin@123\"}")
                .when().post("/api/auth/login")
                .then().statusCode(200)
                .extract().path("token");
    }

    @Test
    void dashboardAndSlots() {
        String jwt = token();
        given().auth().oauth2(jwt)
                .when().get("/api/parking-slots")
                .then().statusCode(200)
                .body("size()", greaterThan(0));
        given().auth().oauth2(jwt)
                .when().get("/api/dashboard/statistics")
                .then().statusCode(200)
                .body("totalSlots", greaterThan(0));
    }

    @Test
    void healthLive() {
        given().when().get("/q/health/live").then().statusCode(200);
    }
}
