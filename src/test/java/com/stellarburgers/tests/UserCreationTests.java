package com.stellarburgers.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserCreationTests extends BaseTest {

    @Test
    @Step("Тест: создание уникального пользователя")
    public void createUniqueUserSuccess() {
        skipSetUp = true;
        testEmail = generateRandomEmail();
        testName = generateRandomName();
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}", testEmail, testPassword, testName))
                .post("/auth/register");

        authToken = response.jsonPath().getString("accessToken");
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testEmail));

    }

    @Test
    @Step("Тест: создание уже зарегистрированного пользователя")
    public void createExistingUserFailure() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}", testEmail, testPassword, testName))
                .post("/auth/register");

        response.then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));

    }

    @Test
    @Step("Тест: создание пользователя без имени")
    public void createUserWithoutNameFailure() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", testEmail, testPassword))
                .post("/auth/register");

        response.then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @Step("Тест: создание пользователя без электронной почты")
    public void createUserWithoutEmailFailure() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"name\": \"%s\", \"password\": \"%s\"}", testName, testPassword))
                .post("/auth/register");

        response.then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @Step("Тест: создание пользователя без пароля")
    public void createUserWithoutPasswordFailure() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"name\": \"%s\"}", testEmail, testName))
                .post("/auth/register");

        response.then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

    }
}
