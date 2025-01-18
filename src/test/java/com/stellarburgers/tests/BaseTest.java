package com.stellarburgers.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;

import java.util.Random;

public abstract class BaseTest {

    protected String testEmail;
    protected String testPassword = "TestPassword123";
    protected String testName;
    protected String authToken;
    protected boolean shouldDeleteUser = true;
    protected boolean skipSetUp = false;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        if (!skipSetUp) {
            registerTestUser();
        }
    }

    @After
    public void tearDown() {
        if (shouldDeleteUser) {
            deleteTestUser();
        }
    }

    @Step("Генерация случайного email")
    protected String generateRandomEmail() {
        return "test" + System.currentTimeMillis() + "@test.com";
    }

    @Step("Генерация случайного имени")
    protected String generateRandomName() {
        long currentTimeMillis = System.currentTimeMillis();
        String randomLetters = generateRandomString(5);
        return "TestUser" + currentTimeMillis + randomLetters;
    }

    @Step("Генерация случайной строки из букв")
    protected String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
    }

    @Step("Регистрация тестового пользователя")
    protected void registerTestUser() {
        testEmail = generateRandomEmail();
        testName = generateRandomName();


        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}", testEmail, testPassword, testName))
                .post("/auth/register");

        authToken = response.jsonPath().getString("accessToken");
        System.out.println("Auth Token: " + authToken);
        response.then().statusCode(200);

    }

    @Step("Авторизация тестового пользователя")
    protected void loginTestUser() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", testEmail, testPassword))
                .post("/auth/login");

        response.then().statusCode(200);
    }

    @Step("Удаление тестового пользователя")
    protected void deleteTestUser() {
        Response response = RestAssured.given()
                .header("Authorization", authToken)
                .header("Accept", "application/json")
                .log().all()
                .delete("/auth/user");
        response.then().statusCode(202);
        response.then().log().all();
    }
}
