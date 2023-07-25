package tests;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mozilla.javascript.Token;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ApiTests {

    public static String TOKEN_VALUE;
    public static final String TOKEN = "token";

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        Authenticaton body = new Authenticaton().builder()
                .username("admin")
                .password("password123")
                .build();

        Response response = RestAssured.given()
                .body(body)
                .post("/auth");

        response.prettyPrint();
        TOKEN_VALUE = response.then().extract().jsonPath().get(TOKEN);
        RestAssured.requestSpecification.cookie(TOKEN, TOKEN_VALUE);

//        String rawResponse = response.getBody().asString();
//        System.out.println("Raw Response:" + rawResponse);
    }

    @Test
    public void auth() {
        Authenticaton body = new Authenticaton().builder()
                .username("admin")
                .password("password123")
                .build();

        Response response = RestAssured.given().log().all()
                .header("Accept", "application/json")
                .body(body)
                .post("/auth");

        response.prettyPrint();
        TOKEN_VALUE = response.then().extract().jsonPath().get(TOKEN);

//        String rawResponse = response.getBody().asString();
//        System.out.println("Raw Response:");
//        System.out.println(rawResponse);
    }

    @Test
    public void createBooking() {

        Date date = new Date();
        SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate tomorrow = new LocalDate().now().plusDays(1);

        BookingDates bookingdates = BookingDates.builder()
                .checkin(dmyFormat.format(date))
                .checkout(dmyFormat.format(date).formatted(tomorrow))
                .build();

        BookingId body = new BookingId().builder()
                .firstname("Valera")
                .lastname("Marginal")
                .totalprice(234)
                .depositpaid(true)
                .bookingdates(bookingdates)
                .additionalneeds("Somth new")
                .build();

        Response response = RestAssured.given().log().all()
                .spec(RestAssured.requestSpecification)
                .header("Accept", "application/json")
                .body(body)
                .post("/booking");

        response.prettyPrint();
//        response.then().assertThat().body(body.getFirstname().equals("Valera"), true);

        response.as(ResponseBooking.class);
//        JSONObject response2 = new JSONObject(response.getBody());
//        ((JSONObject)response2.get("bookingid")).get("firstname");
    }

    @Test
    public void getBookingId() {
        Response response = RestAssured.given().log().all().spec(RestAssured.requestSpecification)
                .header("Accept", "application/json")
                .get("/booking");

        response.then().statusCode(200);
        response.prettyPrint();
        response.jsonPath().get("bookingid");
    }

    @Test
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void bookingUpdate() {

        BookingId body = new BookingId().builder()
                .firstname("New name")
                .additionalneeds("new needs")
                .build();


        Response response2 = RestAssured.given()
                .log().all()
                .spec(RestAssured.requestSpecification)
//                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
//                .cookie(TOKEN, TOKEN_VALUE)
                .body(body)
                .put("/booking/" + 416);

        response2.getBody().asString();

//        JSONObject response3 = new JSONObject(response2.body());
//        ((JSONObject)response3.get("/booking/" + 416)).get("firstname");

    }

    @Test
    public void partialUpdate() {

        JSONObject body = new JSONObject();
        body.put("totalprice", 500);

        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .cookie(TOKEN, TOKEN_VALUE)
                .body(body.toString())
                .patch("/booking/" + findFirstBooking());

        response.prettyPrint();

//        response3.then().body("totalprice", equals("500"));

    }

    @Test
    public void deleteBooking() {

        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .cookie(TOKEN, TOKEN_VALUE)
                .delete("/booking/" + findFirstBooking());

        response.prettyPrint();
        response.then().assertThat().statusCode(201);
//        response.as(DeleteResponse.class);

    }

    private Integer findFirstBooking() {
        Response getBookings = RestAssured.get("/booking");
//        List<Integer> booking = getBookings.jsonPath().getList("/booking");

//        JsonPath getBookingsList = new JsonPath(getBookings.asString());
//        int bookingListSize = j.getInt("Location.size()");
//        for(int i=0; i<bookingListSize; i++) {
//        }
        return getBookings.then().extract().jsonPath().get("bookingid[0]");
    }
}
