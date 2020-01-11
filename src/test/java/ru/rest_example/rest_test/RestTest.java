package ru.rest_example.rest_test;

import static io.restassured.RestAssured.get;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ser.ContainerSerializer;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RestTest {
    private String path = "https://api.openweathermap.org/data/2.5/weather?q={query}&appid={key}";
    private String pathForFormat = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";

    @DataProvider()
    Object[][] getWeatherFromOpenWeatherAPIProvider() throws IOException {
        return DataProviderMaker.loadCvsData("positiveTestSuite.csv");
    }

    @Test(description = "GET, positive test flow", dataProvider = "getWeatherFromOpenWeatherAPIProvider")
    public void getWeatherFromOpenWeatherAPI(String query, String key) throws JSONException, InterruptedException {
        Response response = sendGetRequest(query, key);
        response.then().assertThat().statusCode(HttpStatus.SC_OK).contentType(ContentType.JSON);
        Assert.assertTrue(response.jsonPath().getString("name").contains(query.split(",")[0]),
                "Responce should contain city from request");
    }

    @DataProvider()
    public Object[][] failToGetWeatherFromAPINotFoundProvider() throws IOException {
        return DataProviderMaker.loadCvsData("negativeTestSuite.csv");
    }

    @Parameters({ "query", "key" })
    @Test(description = "GET,  Fail to get weather with wrong parameters", dataProvider = "failToGetWeatherFromAPINotFoundProvider")
    public void failToGetWeatherFromAPINotFound(String query, String key) throws JSONException, InterruptedException {
        Response response = sendGetRequest(query, key);
        response.then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @DataProvider()
    public Object[][] failToGetWeatherInvalidKeyProvider() throws IOException {
        return DataProviderMaker.loadCvsData("notValidKey.csv");
    }

    @Test(description = "GET, Fail to get weather with invalid key", dataProvider = "failToGetWeatherInvalidKeyProvider")
    public void failToGetWeatherWithInvalidKey(String query, String key) throws JSONException, InterruptedException {
        Response response = sendGetRequest(query, key);
        response.then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @DataProvider()
    public Object[][] failToGetWeatherWithEmptyParameter() throws IOException {
        return DataProviderMaker.loadCvsData("onlyKey.csv");
    }

    @Test(description = "GET, Fail to get weather with empty parameter", dataProvider = "failToGetWeatherWithEmptyParameter")
    public void failToGetWeatherWithEmptyParameter(String query, String key)
            throws JSONException, InterruptedException {
        Response response = sendGetRequest(query, key);
        response.then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    private Response sendGetRequest(String query, String key) {
        System.out.println("\n" + String.format(pathForFormat, query, key));
        Response response = get(path, query, key);
        System.out.println(response.body().asString());
        return response;
    }

}
