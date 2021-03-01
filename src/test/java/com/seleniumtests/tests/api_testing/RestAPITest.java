package com.seleniumtests.tests.api_testing;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.hamcrest.CoreMatchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class RestAPITest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        RestAssured.baseURI = "https://reqres.in";
    }

    public Response getResponse(String api) {
        return get(api);
    }

    /**
     * Not a real test
     */
    @Test
    public void printResponse() {
        getResponse("/api/users?page=2")
                .getBody()
                .prettyPrint();
    }


    @Test
    public void validateGetRequest() {

        // Validate response code
        RestAssured
                .given()
                .when()
                .get("/api/users?page=2")
                .then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK);


        // Validate response code failure
        RestAssured
                .given()
                .when()
                .get("/api/users?page=2")
                .then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST);


        // Assert element value in json
        RestAssured
                .given()
                .when()
                .get("/api/users?page=2")
                .then()
                .assertThat()
                .body("total_pages", equalTo(2));

        // Or with with() but this time test for fail
        RestAssured
                .with()
                .get("/api/users?page=2")
                .then()
                .assertThat()
                .body("total_pages", equalTo(100));


        // Without syntax sugar
        get("/api/users?page=2")
                .then()
                .body("total_pages", equalTo(2));


        // Multiple checks
        get("/api/users?page=2")
                .then()
                    .statusCode(HttpURLConnection.HTTP_OK)
                .and()
                    .contentType(ContentType.JSON)
                .and()
                    .header("Connection", "keep-alive");
    }


    @Test(dataProvider = "queryParameter")
    public void testWithQueryParameters(Object queryParameter, Object expectedResponse) {
        // Query parameters are appended at the end of an API endpoint and are identified by the question mark in front of them.
        // For example, in the endpoint /api/users?page=2, "page" is a query parameter (with value "2")
        // Query Parameters are specified using param method which takes name and value arguments
        given()
                .param("page", queryParameter)
                .get("/api/users")
                .then()
                .body("data.email[0]", equalTo(expectedResponse));
        // Let's run it in debug mode
    }


    @Test
    public void testWithPathParameters() {
        // Path parameter are part of the REST API endpoint.
        // In the endpoint https://reqres.in/api/users/2, "users" is a path parameter value.
        // Path parameters are specified using pathParam method
        String usersParameter = "users";
        given()
                .pathParam("path_parameter", usersParameter)
                .get("/api/{path_parameter}?page=2")
                .then()
                .statusCode(HttpsURLConnection.HTTP_OK);
    }

    @Test
    public void passParametersBetweenTests() {
        int total = get("/api/users?page=2")
                .then()
                .extract()
                .path("total"); //There are total 12 users

        given()
                .pathParam("total", total)
                .get("/api/users/{total}")
                .then()
                .body("data.first_name", equalTo("Rachel"));
        // Let's run it  in debug mode and analyze results
    }

    @DataProvider(name = "queryParameter")
    public Object[][] queryParameter() {
        return new Object[][]{{1, "george.bluth@reqres.in"}, {2, "michael.lawson@reqres.in"}};
    }
}
