package com.stellarburgers.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OrderCreationTests extends BaseTest {

    @Test
    @Step("Тест: создание заказа с авторизацией")
    public void createOrderWithAuthorizationSuccess() {
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f"};

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body(String.format("{\"ingredients\": [\"%s\", \"%s\"]}", ingredients[0], ingredients[1]))
                .post("/orders");

        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Step("Тест: создание заказа без авторизации разрешено")
    public void createOrderWithoutAuthorizationSuccess() {
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f"};

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(String.format("{\"ingredients\": [\"%s\", \"%s\"]}", ingredients[0], ingredients[1]))
                .post("/orders");

        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Step("Тест: создание заказа с корректными ингредиентами")
    public void createOrderWithValidIngredientsSuccess() {
        String[] ingredients = {"61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6c"};

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body(String.format("{\"ingredients\": [\"%s\", \"%s\"]}", ingredients[0], ingredients[1]))
                .post("/orders");

        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.ingredients.size()",equalTo(2))
                .body("order.number", notNullValue());

        List<String> ingredientIdsFromResponse = response.jsonPath().getList("order.ingredients._id");

        assertThat("Идентификаторы ингредиентов не совпадают", ingredientIdsFromResponse, containsInAnyOrder(ingredients));
    }

    @Test
    @Step("Тест: создание заказа с одним корректным ингредиентом")
    public void createOrderWithOneValidIngredientSuccess() {
        String[] ingredients = {"61c0c5a71d1f82001bdaaa70"};

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body(String.format("{\"ingredients\": [\"%s\"]}", ingredients[0]))
                .log().all()
                .post("/orders");

        response.then().log().all();
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.ingredients.size()",equalTo(1))
                .body("order.number", notNullValue());

        List<String> ingredientIdsFromResponse = response.jsonPath().getList("order.ingredients._id");

        assertThat("Идентификаторы ингредиентов не совпадают", ingredientIdsFromResponse, containsInAnyOrder(ingredients));
    }

    @Test
    @Step("Тест: попытка создания заказа без ингредиентов")
    public void createOrderWithoutIngredientsFailure() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body("{\"ingredients\": []}")
                .post("/orders");

        response.then().statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Тест: попытка создания заказа с неверным хэшем ингредиентов")
    public void createOrderWithInvalidIngredientHashFailure() {
        String[] ingredients = {"61c0c5a71d1f82001bdaaa702311"};

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .body(String.format("{\"ingredients\": [\"%s\"]}", ingredients[0]))
                .post("/orders");

        response.then().statusCode(500);
    }
}
