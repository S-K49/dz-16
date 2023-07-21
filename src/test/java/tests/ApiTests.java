package tests;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ApiTests {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addCookie("token=abc123")
                .build();
    }

    @Test
    public void authorization() {

        Authenticaton body = new Authenticaton().builder()
                .username("admin")
                .password("password123")
                .build();

//        Response response = RestAssured.given().log().all().spec(RestAssured.requestSpecification)
//                .get("/auth");

        Response response = RestAssured.given().log().all()
                .spec(RestAssured.requestSpecification)
                .body(body)
                .post("/auth");

        response.prettyPrint();
        response.then().assertThat().statusCode(200);

//        response.as(ResponseBooking.class);
//        JSONObject response2 = new JSONObject(response.asString());
//        ((JSONObject)response2.get("bookingid")).get("firstname");
    }

    @Test
    public void createBooking() {

        Date checkinDate = new Date(2023-06-12);
        Date checkoutDate = new Date(2023-06-13);

//        LocalDate localDate = LocalDate.parse(checkinDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        BookingDates bookingdates = BookingDates.builder()
                .checkin(checkinDate)
                .checkout(checkoutDate)
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
                .body(body)
                .post("/booking");

        response.prettyPrint();

//        response.as(ResponseBooking.class);
//        JSONObject response2 = new JSONObject(response.asString());
//        ((JSONObject)response2.get("bookingid")).get("firstname");
    }

    @Test
    public void getBookingId() {
        Response response = RestAssured.given().log().all().spec(RestAssured.requestSpecification)
                .get("/booking");

        response.then().statusCode(200);
        response.prettyPrint();
        response.jsonPath().get("bookingid");
    }

    @Test
    public void changeNameAdditionalNeeds() {

        int bookingid = 416;

        Response response = RestAssured.given().log().all().spec(RestAssured.requestSpecification)
                .get("/booking/" + bookingid);

        BookingId body = new BookingId().builder()
                .firstname("New name")
                .additionalneeds("new needs")
                .build();

        Response response2 = RestAssured.given().log().all()
                .spec(RestAssured.requestSpecification)
                .body(body)
                .put("/booking/" + bookingid);

        response.prettyPrint();

//        JSONObject response3 = new JSONObject(response.asString());
//        ((JSONObject)response3.get("bookingid/" + bookingid)).get("firstname");

    }

    @Test
    public void changePrice() {

        int bookingid = 1108;

        Response response = RestAssured.given().log().all().spec(RestAssured.requestSpecification)
                .get("/booking/{id}", bookingid);

        BookingId body = new BookingId().builder()
                .totalprice(234)
                .build();

        Response response2 = RestAssured.given().log().all()
                .spec(RestAssured.requestSpecification)
                .body(body)
                .patch("/booking/" + bookingid);

        response.then().assertThat().statusCode(200);
        response2.prettyPrint().equals(body.getTotalprice());

    }

    @Test
    public void deleteBooking() {
        authorization();

        int bookingid = 407;

        Response response = RestAssured.given().log().all().spec(RestAssured.requestSpecification)
                .delete("/booking/" + bookingid);

        response.prettyPrint();
        response.then().assertThat().statusCode(201);
        response.as(DeleteResponse.class);

    }
}
