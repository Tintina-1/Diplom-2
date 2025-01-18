package com.stellarburgers.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserOrdersTests extends BaseTest {

    @Test
    @Step("Тест: получение всех заказов авторизованным пользователем")
    public void getOrdersWithAuthorizationSuccess() {
        Response response = RestAssured.given()
                .header("Authorization", authToken)
                .get("/orders");

        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @Step("Тест: попытка получения заказов без авторизации")
    public void getUserOrdersWithoutAuthorizationFailure() {
        Response response = RestAssured.given()
                .get("/orders");

        response.then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }


}
