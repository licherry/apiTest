package com.restassured.apitest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Test;
import org.w3c.dom.events.EventException;

import java.util.Map;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.matcher.ResponseAwareMatcherComposer.and;
import static io.restassured.matcher.RestAssuredMatchers.endsWithPath;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class PracticeResponse {

    //  Practice using Parameters in Request.
    //  Google Book API reference  https://developers.google.com/books/docs/v1/reference/volumes/list
    //  Google Sheets API reference  https://developers.google.com/sheets/reference/rest/


    private static String token = "ya29.CjAsAweyghKHe1WxO8cvToyFE2gaQ6mlp-GKeCgfaVqd0hEedWwQc5WBm-_bXQgkf5g";
    private static String spreadsheetId = "1HdzbvwDDpebq9vUbCeIPPP7l7B4RxPO5QjhC3QRqQ7w";


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //发送get请求，返回书名为含有cucumber的书，并返回一个结果；打印response返回内容
    //https://github.com/rest-assured/rest-assured/wiki/Usage#getting-response-data
    @Test
    public void testGetAllResponse() throws EventException {
        String result =
                given()
                        .when().get("https://www.googleapis.com/books/v1/volumes?q=cucumber&maxResults=1").asString();
        System.out.println(result);


    }


    //直接从response body中查找特定值
    //https://github.com/rest-assured/rest-assured/wiki/Usage#example-1---json
    @Test
    public void testResponseBasicUsage() throws EventException {
        given()
        .when()
                .get("https://www.googleapis.com/books/v1/volumes?q=cucumber&maxResults=2")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("kind", equalTo("books#volumes"))
                .body("items[0].volumeInfo.title", equalTo("Advances in Sea Cucumber Aquaculture and Management"));

    }


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //使用extract method提取response片段
    //https://github.com/rest-assured/rest-assured/wiki/Usage#extracting-values-from-the-response-after-validation
    @Test
    public void testExtractFragmentFromGoogleBookResponse() throws EventException {
        String bookResponse =
                given()
                .when()
                        .get("https://www.googleapis.com/books/v1/volumes?q=cucumber&maxResults=2")
                .then()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .path("items[0].volumeInfo.title");
        System.out.print("The title :" + bookResponse);
    }

    @Test
    public void testExtractFragmentFromGoogleSheetResponse() throws EventException {
        String sheetResponse =
                given()
                        .auth().oauth2(this.token)
                        .when()
                        .get("https://sheets.googleapis.com/v4/spreadsheets/"+this.spreadsheetId+"/values/Sheet1!A1:D6")
                        .then()
                        .log().all()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .path("values[5][2]");
        System.out.println("The memo : " + sheetResponse);

    }



    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //使用extract method提取header和cookie
    //https://github.com/rest-assured/rest-assured/wiki/Usage#extracting-values-from-the-response-after-validation
    @Test
    public void testExtractAllResponse() throws EventException {
        Response response =
                given()
                .when()
                        .get("https://www.googleapis.com/books/v1/volumes?q=cucumber&maxResults=2")
                .then()
                        .contentType(ContentType.JSON)
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response();

        int totalItems = response.path("totalItems");
        String title = response.path("items[0].volumeInfo.title");
        String publisher = response.path("items[0].volumeInfo.publisher");
        Headers allHeaders = response.headers();
        Map<String, String> allCookies = response.getCookies();

        System.out.print("Header : \n" + allHeaders);
        System.out.print("\nCookies : \n" + allCookies.toString());
        System.out.print("\nTotal Items : " + totalItems + "\ntitle : " + title + "\npublisher :" + publisher);


    }


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //使用Response的一部分去验证Response另一部分
    //https://github.com/rest-assured/rest-assured/wiki/Usage#use-the-response-to-verify-other-parts-of-the-response
    @Test
    public void testUsePartInResponseByLambda() throws EventException {
        given()
        .when()
                .get("https://www.googleapis.com/books/v1/volumes?q=cucumber&maxResults=2")
        .then()
                .log().all()
                .body("items[0].selfLink", response -> equalTo("https://www.googleapis.com/books/v1/volumes/" + response.path("items[0].id")));

    }


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //使用Response的一部分去验证Response另一部分
    //https://github.com/rest-assured/rest-assured/wiki/Usage#use-the-response-to-verify-other-parts-of-the-response
    @Test
    public void testUsePartInResponse() throws EventException {
        given()
        .when()
                .get("https://www.googleapis.com/books/v1/volumes?q=cucumber&maxResults=2")
        .then()
                .assertThat()
                .body("items[0].selfLink", endsWithPath("items[0].id"))
                .body("items[0].selfLink", and(startsWith("https://www.googleapis.com/"), endsWithPath("items[0].id")));


    }


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //pageCount>300的书里面是否包含title为："Advances in Sea Cucumber Aquaculture and Management"和"A Treatise on the Culture of the Cucumber"的书
    // https://github.com/rest-assured/rest-assured/wiki/Usage#json-schema-validation -> Example 3 - Complex parsing and validation
    @Test
    public void testGroovyCollection1() throws EventException {
        given()
                .param("q","cucumber")
                .param("maxResults",10)
        .when()
                .get("https://www.googleapis.com/books/v1/volumes")
        .then()
                .log().all()
                .assertThat()
                .body("items.findAll{it.volumeInfo.pageCount>300}.volumeInfo.title", hasItems("Advances in Sea Cucumber Aquaculture and Management","A Treatise on the Culture of the Cucumber"));

    }


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    // https://github.com/rest-assured/rest-assured/wiki/Usage#json-schema-validation -> Example 3 - Complex parsing and validation
    @Test
    public void testGroovyCollection2() throws EventException {
        given()
                .param("filter","paid-ebooks")
                .param("q","cucumber")
                .param("maxResults",10)
        .when()
                .get("https://www.googleapis.com/books/v1/volumes")
        .then()
                .assertThat()
                .body("items.findAll {it.saleInfo.listPrice.amount > 200}.size()", greaterThanOrEqualTo(2))
                .body("items.collect {it.saleInfo.retailPrice.amount}.sum()",greaterThanOrEqualTo(800.00))
                .body("items*.saleInfo.retailPrice.amount.sum()",greaterThanOrEqualTo(800.00))
                .statusCode(200);

    }


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //查找saleability属性
    // https://github.com/rest-assured/rest-assured/wiki/Usage#json-schema-validation -> Example 3 - Complex parsing and validation
    @Test
    public void testGroovyCollection3() throws EventException {
        given()
                .param("filter","paid-ebooks")
                .param("q","cucumber")
                .param("maxResults",10)
        .when()
                .get("https://www.googleapis.com/books/v1/volumes")
        .then()
                .assertThat()
                .body("items.unique {it.saleInfo.saleability}.size()",lessThanOrEqualTo(2));

    }


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //使用Specification
    //https://github.com/rest-assured/rest-assured/wiki/Usage#specification-re-use
    @Test
    public void testSpecification() throws EventException {
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .addParam("q", "cucumber")
                .addParam("maxResult",2)
                .build();

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(200);
        responseSpecBuilder.expectResponseTime(lessThan(5000L));
        responseSpecBuilder.expectBody("kind", equalTo("books#volumes"));
        ResponseSpecification responseSpec = responseSpecBuilder.build();

       Response response =
               given()
                       .spec(requestSpec)
               .when()
                       .get("https://www.googleapis.com/books/v1/volumes")
               .then()
                        .assertThat()
                        .spec(responseSpec)
                        .extract()
                        .response();
        assertTrue(Pattern.matches("\\d+", response.path("totalItems").toString()));


    }

}
