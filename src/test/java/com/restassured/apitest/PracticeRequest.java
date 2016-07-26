package com.restassured.apitest;

import io.restassured.http.ContentType;
import org.junit.Test;
import org.w3c.dom.events.EventException;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

import static io.restassured.RestAssured.given;

/**
 * Created by chenli on 7/26/16.
 */
public class PracticeRequest {
    //  Practice using Parameters in Request.
    //  Google Book API reference  https://developers.google.com/books/docs/v1/reference/volumes/list
    //  Google Sheets API reference  https://developers.google.com/sheets/reference/rest/
    private static String token = "ya29.CjArAyNfeQ9z1yeDQGvIxQ0TcdnRq4SqalJ_uiVs5CV4SQFLJJ8d5TVI9xr1BZ-fjXY";
    private static String spreadID = "1HdzbvwDDpebq9vUbCeIPPP7l7B4RxPO5QjhC3QRqQ7w";

    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //发送get请求，返回书名为含有cucumber的书，并返回两个结果
    @Test
    public void testGoogleBookAPIDataInURL() throws EventException {
        given()
                .when()
                .get("https://www.googleapis.com/books/v1/volumes?q=cucumber&maxResults=2")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200);


    }


    //https://developers.google.com/books/docs/v1/reference/volumes/list
    //使用参数
    //https://github.com/rest-assured/rest-assured/wiki/Usage#syntactic-sugar
    @Test
    public void testGoogleBookAPIDataInParameters() throws EventException {
        given()
                .param("q","cucumber")
                .param("maxResults","2")
                .log().all()
                .when()
                .get("https://www.googleapis.com/books/v1/volumes")
                .then()
                .assertThat()
                .statusCode(200);

    }


    //https://developers.google.com/sheets/reference/rest/v4/spreadsheets.values/update
    //https://developers.google.com/sheets/samples/writing
    //发送PUT请求，增加GoogleSheet中的一行数据
    //https://github.com/rest-assured/rest-assured/wiki/Usage#request-body
    @Test
    public void testGoogleSheetsAPIDataInBody() throws EventException {
        String requestData = "{\n" +
                "  \"range\": \"Sheet1!A4:B4\",\n" +
                "  \"majorDimension\": \"ROWS\",\n" +
                "  \"values\": [\n" +
                "    [\"study\", \"shopping\"]\n" +
                "  ],\n" +
                "}";
        given()
                .auth().oauth2(this.token)
                .pathParam("spreadsheetId",this.spreadID)
                .pathParam("range","Sheet1!A4:B4")
                .param("valueInputOption","USER_ENTERED")
                .body(requestData)
                .log().all()
                .when()
                .put("https://sheets.googleapis.com/v4/spreadsheets/{spreadsheetId}/values/{range}")
                .then()
                .assertThat()
                .statusCode(200);



    }


    //https://developers.google.com/sheets/reference/rest/v4/spreadsheets.values/update
    //发送PUT请求，Update GoogleSheet中的一行数据 - Object Mapping
    //https://github.com/rest-assured/rest-assured/wiki/Usage#create-json-from-a-hashmap
    @Test
    public void testGoogleSheetsAPIMapDataInBody() throws EventException {
        Map<String, Object> jsonAsMap = new HashMap<String, Object>();
        jsonAsMap.put("range","Sheet1!A5:B5");
        jsonAsMap.put("majorDimension","ROWS");
        jsonAsMap.put("values",asList(asList("test2","test3")));

        given()
                .auth().oauth2(this.token)
                .pathParam("spreadsheetId",this.spreadID)
                .pathParam("range","Sheet1!A5:B5")
                .param("valueInputOption","USER_ENTERED")
                .body(jsonAsMap)
                .log().all()
                .when()
                .put("https://sheets.googleapis.com/v4/spreadsheets/{spreadsheetId}/values/{range}")
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(200);

    }


    //https://developers.google.com/sheets/reference/rest/v4/spreadsheets.values/update
    //发送PUT请求，Update GoogleSheet中的一行数据 - 序列化
    //https://github.com/rest-assured/rest-assured/wiki/Usage#serialization
    @Test
    public void testGoogleSheetsAPIObjectDataInBody() {
        RequestData data = new RequestData();
        data.setRange("Sheet1!A6:C6");
        data.setMajorDimension("ROWS");
        data.setValues(asList(asList("Hello","super","world")));

        given()
                .auth().oauth2(this.token)
                .pathParam("spreadsheetId",this.spreadID)
                .pathParam("range","Sheet1!A6:C6")
                .param("valueInputOption","USER_ENTERED")
                .body(data)
                .when()
                .put("https://sheets.googleapis.com/v4/spreadsheets/{spreadsheetId}/values/{range}")
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(200);

    }


    //https://developers.google.com/sheets/reference/rest/v4/spreadsheets/batchUpdate
    //发送post请求，修改Favorite Color列的背景色
    @Test
    public void testGoogleSheetsPost() throws EventException {
        String postData = "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"updateCells\": {\n" +
                "        \"start\": {\n" +
                "          \"sheetId\": 0,\n" +
                "          \"rowIndex\": 1,\n" +
                "          \"columnIndex\": 3\n" +
                "        },\n" +
                "        \"rows\": [\n" +
                "          {\n" +
                "            \"values\": [\n" +
                "              {\n" +
                "                \"userEnteredFormat\": {\"backgroundColor\": {\"red\": 0.2,\n" +
                "                  \"green\": 1.0,\n" +
                "                  \"blue\": 0.5}}\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ],\n" +
                "        \"fields\": \"userEnteredFormat.backgroundColor\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        given()
                .log().all()
                .urlEncodingEnabled(false)
                .auth().oauth2(this.token)
                .pathParam("spreadsheetId",this.spreadID)
                .body(postData)
                .when()
                .post("https://sheets.googleapis.com/v4/spreadsheets/{spreadsheetId}:batchUpdate")
                .then()
                .assertThat()
                .statusCode(200);

    }
}
