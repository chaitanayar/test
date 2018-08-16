package weatherApi;

import java.util.concurrent.TimeUnit;

/*import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.response.ValidatableResponseOptions;*/
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/*public interface ValidatableResponse extends ValidatableResponseOptions<ValidatableResponse, Response> {


}
*/

public class ValidateWeatherApi_1 {

    /**
     * Replace App Id with ur accessed AppId
     */
    private static final String APPID = "687e76f9f24e496603141058707edf09";

    /**
     * WEATHER_END_POINT is the path used it for the API
     */
    private static final String WEATHER_END_POINT = "/data/2.5/weather";

    /**
     * WEATHER_FORECAST_END_POINT is the path used it for the API weather Forecast
     */
    private static final String WEATHER_FORECAST_END_POINT = "/data/2.5/forecast";

    private static final String WEATHER_HISTORY_END_POINT = "/data/2.5/forecast";

    private static final String ZIPCODE = "94040, us";

    private static final Integer LONDON_CITYID = 524901;

    private static final Integer LONDON_LATTITUDE = 35;

    private static final Integer LONDON_LONGITUDE = 139;

    private static final String INVALID_CITY_ID = "%%^&*(";

    private static final String INVALID_ZIPCODE = "%^&**, 78";

    private static final String CITYNAME = "Souix falls,USA";

    private static final String INVALID_CITYNAME = "*&FG****";






    @BeforeClass
    public void initialSetUp() {

        RestAssured.baseURI = "http://api.openweathermap.org";
    }

    // Verify the Response code
    @Test
    public void validateResponseCode() {
        given().when().
                get(WEATHER_END_POINT
                        + "?zip=" + ZIPCODE
                        + "&appid=" + APPID)
                .then()
                .statusCode(200);

    }

    // Passing Parameters to GET Request
    @Test
    public void passQueryParametersToGetRequest() {

        given().
                params("zip", ZIPCODE, "appid" + APPID)
                .when().
                get(WEATHER_END_POINT).then().body("cod", equalTo(200));


    }

    // Passing Parameters to GET Request
    @Test
    public void validateTemperature() {

        int minTemp = given()
                .params("zip", ZIPCODE, "appid", APPID)
                .when()
                .get(WEATHER_END_POINT)
                .then()
                .extract()
                .path("main.temp_min");

        int maxTemp = given().
                params("zip", ZIPCODE, "appid", APPID)
                .when()
                .get(WEATHER_END_POINT)
                .then()
                .extract()
                .path("main.temp_max");

        int temp = given().
                params("zip", ZIPCODE, "appid",  APPID)
                .when()
                .get(WEATHER_END_POINT)
                .then()
                .extract()
                .path("main.temp");

        assertTrue(minTemp <= temp && temp <= maxTemp);


    }

    // PathParameter to read a JSON file
    @Test
    public void useSinglePathParameter() {

        String responseBody = given().pathParam("suffixName", "monarchs").when()
                .get("http://mysafeinfo.com/api/data?list=english{suffixName}&format=json").getBody().asString();
        // System.out.println("Response Body is: " + responseBody);
    }

    // Negative to read a JSON file
    @Test
    public void negativeTest() {

        given().params("zip", "94040,AAA", "appid", APPID)
                .when()
                .get(WEATHER_END_POINT)
                .then()
                .body("message", equalTo("city not found"), "cod", equalTo("404"));
    }

    @Test
    // Validate if the format is JSON
    public void checkResponseContentTypeJson() {

        given().params("zip", "94040,us", "appid",  APPID)
                .when()
                .get(WEATHER_END_POINT)
                .then()
                .assertThat()
                .contentType("application/json");
    }

    //A Key is available in the Response
    @Test
    public void isKeyAvailable() {

        given().params("zip", "94040,us", "appid",  APPID)
                .when()
                .get(WEATHER_END_POINT)
                .then().
                assertThat()
                .body("$", hasKey("name"))
                .body("$", not(hasKey("IamInvisible")));
    }

    //Data Providers
    // Returning a String Array. Data Providers can return maps too
    @DataProvider(name = "city")
    public String[][] cityData() {
        return new String[][]{
                {"94040,us", "Mountain View"},
                {"03820,us", "Manchester"},
                {"75024,us", "Frisco"}
        };
    }

    @Test(dataProvider = "city")
    public void validateWithDataProviders(String zipCode, String cityName) {

        given().
                params("zip", zipCode, "appid",  APPID)
                .when().
                get(WEATHER_END_POINT).
                then().
                body("name", equalTo(cityName));
    }

    @Test
    public void responseTimeValidation() {

        given().
                params("zip", ZIPCODE, "appid",  APPID)
                .when().
                get(WEATHER_END_POINT).
                then().
                assertThat().
                time(lessThan(1000L), TimeUnit.MILLISECONDS);
    }

    ResponseSpecification respSpec;

    @BeforeClass
    public void createResponseSpecification() {

        respSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("name", equalTo("Mountain View"))
                .build();
    }

    @Test
    public void useResponseSpecification() {
        given().
                params("zip", ZIPCODE, "appid",  APPID)
                .when().
                get(WEATHER_END_POINT).then().
                spec(respSpec).
                and().
                body("cod", equalTo(200));
    }

    /*******************TESTS FOR 5 day weather forecast API***************************/

    /**
     * test for 5 day weather forecast -- BY CITY ID.
     */
  /*  @Test
    public void testFiveDayWeatherForCastByCityId1() {

        validatingResponseForForecast(given()
                        .params("id", LONDON_CITYID)
                        .when()
                        .get(WEATHER_FORECAST_END_POINT).
                        then()
                        .extract()
                        .path("list"));

        validateResponseForeCast(given()
        .params("id", LONDON_CITYID)
        .when()
        .get(WEATHER_FORECAST_END_POINT)
        .then()
        .statusCode(200));

    }

    *//**
     * test for 5 day weather forecast -- By geographic coordinates.
     *//*
    @Test
    public void testFiveDayWeatherForCastByGeoCoordinates() {

        validatingResponseForForecast(given()
                .params("lat", LONDON_LATTITUDE , "lon", LONDON_LONGITUDE)
                .when()
                .get(WEATHER_FORECAST_END_POINT)
                .then()
                .extract()
                .path("list"));

        validateResponseForeCast(
                        given()
                .params("lat", LONDON_LATTITUDE , "lon", LONDON_LONGITUDE)
                .when()
                .get(WEATHER_FORECAST_END_POINT)
                .then()
                .statusCode(200));
    }

    *//**
     * test for 5 day weather forecast -- By ZIP code.
     *//*
    @Test
    public void testFiveDayWeatherForCastByZipCode() {
        validatingResponseForForecast(given()
                .params("zip", ZIPCODE)
                .when()
                .get(WEATHER_FORECAST_END_POINT)
                .then()
                .extract()
                .path("list"));
        validateResponseForeCast(given()
                .params("zip", ZIPCODE)
                .when()
                .get(WEATHER_FORECAST_END_POINT)
                .then()
                .statusCode(200));

    }

    *//**
     * to verify when the invalid params passed , to verify we wont get the response with keys exist.
     *//*
    @Test
    public void testInvalidCityIdForWeatherForCast() {

        given()
                .params("id", INVALID_CITY_ID)
                .when()
                .get(WEATHER_FORECAST_END_POINT)
                .then().
                assertThat()
                .body("$", not(hasKey("city")))
                .body("$", not(hasKey("coord")))
                .body("$", not(hasKey("message")))
                .body("$", not(hasKey("cnt")))
                .body("$", not(hasKey("country")));
    }

    *//**
     * to verify when the invalid params passed , to verify we wont get the response with keys exist.
     *//*
    @Test
    public void testInvalidZipCodeForWeatherForCast() {

        given()
                .params("id", INVALID_ZIPCODE)
                .when()
                .get(WEATHER_FORECAST_END_POINT)
                .then().
                assertThat()
                .body("$", not(hasKey("city")))
                .body("$", not(hasKey("coord")))
                .body("$", not(hasKey("message")))
                .body("$", not(hasKey("cnt")))
                .body("$", not(hasKey("country")));
    }

    *//**
     * Method to ensure the all the values inside the forecast list is valid, and proper.
     * @param foreCastDataList
     *//*
    private void validatingResponseForForecast(List<ForeCast> foreCastDataList) {

        assertNotNull(foreCastDataList);
        assertTrue(foreCastDataList.size() > 0);
        for (ForeCast foreCast : foreCastDataList) {
            assertNotNull(foreCast.getWeather());
            assertNotNull(foreCast.getClouds());
            assertNotNull(foreCast.getMain());
            assertNotNull(foreCast.getMain().getTemp());
            assertTrue(foreCast.getMain().getTemp_Max() >= foreCast.getMain().getTemp_Min());
            assertEquals(foreCast.getDt(), 1406106000);
        }
    }


    private ValidatableResponse validateResponseForeCast(ValidatableResponse response) {
        return response
                .body("city.id", equalTo(1851632))
                .body("city.name", equalTo("Shuzenji"))
                .body("coord.lon", equalTo(138.933334))
                .body("coord.lat", equalTo(34.966671))
                .body("country", equalTo("JP"))
                .body("cod", equalTo(200))
                .body("message", equalTo(0.0045))
                .body("cnt", equalTo(38));
    }


    *//*******************TESTS FOR Historical data API***************************//*
    *//**
     * to verify when the invalid params passed , to verify we wont get the response with keys exist.
     *//*
    @Test
    public void testInvalidCityNameForWeatherForCast() {

        given()
                .params("city", INVALID_CITYNAME)
                .when()
                .get(WEATHER_HISTORY_END_POINT)
                .then().
                assertThat()
                .body("$", not(hasKey("base")))
                .body("$", not(hasKey("wind")))
                .body("$", not(hasKey("clouds")))
                .body("$", not(hasKey("rain")))
                .body("$", not(hasKey("sys")))
                .body("$", not(hasKey("name")))
                .body("$", not(hasKey("id")))
                .body("$", not(hasKey("country")));
    }

    *//**
     * test for History data Api  -- BY CITY NAME.
     *//*
    @Test
    public void testFiveDayWeatherForCastByCityId() {

        validateResponseHistory(given()
                .params("city", CITYNAME)
                .when()
                .get(WEATHER_HISTORY_END_POINT)
                .then()
                .statusCode(200));

    }

    private ValidatableResponse validateResponseHistory(ValidatableResponse response) {
        return response
                .body("coord.lon", equalTo(145.77))
                .body("coord.lat", equalTo(-16.92))
                .body("weather.id", equalTo(803))
                .body("main.temp", equalTo(293.25))
                .body("main.pressure", equalTo(1019))
                .body("main.humidity", equalTo(83))
                .body("wind.speed", equalTo(5.1))
                .body("wind.deg", equalTo(150))
                .body("name", equalTo("Cairns"));
    }*/

}
	
