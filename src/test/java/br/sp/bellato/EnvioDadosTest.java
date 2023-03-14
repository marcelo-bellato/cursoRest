package br.sp.bellato;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import io.restassured.http.ContentType;
import org.junit.Test;

public class EnvioDadosTest
{
  @Test
  public void devoEnviarValorViaQuery()
  {
    given()
        .log().all()
        .when()
        .get("https://restapi.wcaquino.me/v2/users?format=json")
        .then()
        .log().all()
        .statusCode(200)
        .contentType(ContentType.JSON);
  }

  @Test
  public void devoEnviarValorViaQueryViaParam()
  {
    given()
        .log().all()
        .queryParam("format", "json")
        .when()
        .get("https://restapi.wcaquino.me/v2/users")
        .then()
        .log().all()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .contentType(containsString("utf-8"));
  }

  @Test
  public void devoEnviarValorViaHeader()
  {
    given()
        .log().all()
        .accept(ContentType.XML)
        .when()
        .get("https://restapi.wcaquino.me/v2/users")
        .then()
        .log().all()
        .statusCode(200)
        .contentType(ContentType.XML);
  }
}
