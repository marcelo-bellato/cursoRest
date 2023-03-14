package br.sp.bellato;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import org.junit.Test;

public class HTML
{
  @Test
  public void deveFazerBuscasComHTML()
  {
    given()
        .log().all()
        .when()
        .get("https://restapi.wcaquino.me/v2/users")
        .then()
        .log().all()
        .statusCode(200)
        .contentType(ContentType.HTML)
        .appendRootPath("html.body.div.table.tbody")
        .body("tr.size()", is(3))
        .body("tr[1].td[2]", is("25"))
        .body("tr.find{it.toString().startsWith('2')}.td[1]", is("Maria Joaquina"));
  }

  @Test
  public void deveFazerBuscasComXMLEmHTML()
  {
    given()
        .log().all()
        .queryParam("format", "clean")
        .when()
        .get("https://restapi.wcaquino.me/v2/users")
        .then()
        .log().all()
        .statusCode(200)
        .contentType(ContentType.HTML)
        .body(hasXPath("count(//table/tr)", is("4")))
        .body(hasXPath("//td[text()='2']/../td[2]", is("Maria Joaquina")))
        ;
  }

}
