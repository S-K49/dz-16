package tests;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


import static java.lang.String.valueOf;

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
    }

    @Test
    public void createBooking() {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate today = new LocalDate().now();
        LocalDate tomorrow = new LocalDate().now().plusDays(1);

        BookingDates bookingdates = BookingDates.builder()
                .checkin(valueOf(today))
                .checkout(valueOf(tomorrow))
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

        response.as(ResponseBooking.class);
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
                .header("Accept", "application/json")
                .body(body)
                .put("/booking/" + 416);

        response2.getBody().asString();
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
    }

    private Integer findFirstBooking() {
        Response getBookings = RestAssured.get("/booking");

        return getBookings.then().extract().jsonPath().get("bookingid[0]");
    }
}
