package com.stellarburgers.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class UserDataUpdateTests extends BaseTest {


    @Test
    @Step("Тест: изменение почты и пароля с авторизацией")
    public void updateUserEmailAndNameWithAuthorizationSuccess() {
        loginTestUser();
        String updatedName = "Updated Test User";
        String updatedEmail = "updated" + testEmail;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body(String.format("{\"email\": \"%s\", \"name\": \"%s\"}", updatedEmail, updatedName))
                .patch("auth/user");

        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedEmail))
                .body("user.name", equalTo(updatedName));
    }

    @Test
    @Step("Тест: изменение только поля электронной почты с авторизацией")
    public void updateUserEmailWithAuthorizationFailure() {
        loginTestUser();
        String updatedEmail = "updated" + testEmail;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body(String.format("{\"email\": \"%s\",}", updatedEmail))
                .patch("auth/user");

        response.then().statusCode(400);

    }

    @Test
    @Step("Тест: попытка изменения данных без авторизации")
    public void updateUserWithoutAuthorizationFailure() {
        String updatedName = "Unauthorized User";
        String updatedEmail = "unauthorized" + testEmail;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"name\": \"%s\"}", updatedEmail, updatedName))
                .patch("auth/user");

        response.then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
