package com.stellarburgers.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class UserLoginTests extends BaseTest {



    @Test
    @Step("Тест: успешный логин под существующим пользователем")
    public void loginWithValidCredentialsSuccess() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", testEmail, testPassword))
                .post("/auth/login");

        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testEmail));

    }

    @Test
    @Step("Тест: логин с неверным паролем")
    public void loginWithInvalidPasswordFailure() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"password\": \"WrongPassword123\"}", testEmail))
                .post("/auth/login");

        response.then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

    }

    @Test
    @Step("Тест: логин с неверным email")
    public void loginWithInvalidEmailFailure() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"invalid@test.com\", \"password\": \"%s\"}", testPassword))
                .post("/auth/login");

        response.then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

    }
}
