package br.sp.bellato;

import static io.restassured.RestAssured.given;

import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Test;
import org.xml.sax.SAXParseException;

public class SchemaTest
{
  @Test
  public void deveValidarSchemaTest() {
    given()
        .log().all()
        .when()
        .get("https://restapi.wcaquino.me/usersXML")
        .then()
        .log().all()
        .statusCode(200)
        .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
    ;
  }

  @Test(expected = SAXParseException.class)
  public void naoDeveValidarSchemaTestInvalido() {
    given()
        .log().all()
        .when()
        .get("https://restapi.wcaquino.me/invalidusersXML")
        .then()
        .log().all()
        .statusCode(200)
        .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
    ;
  }

  @Test
  public void deveValidarSchemaJson() {
    given()
        .log().all()
        .when()
        .get("https://restapi.wcaquino.me/users")
        .then()
        .log().all()
        .statusCode(200)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))
    ;
  }
}
