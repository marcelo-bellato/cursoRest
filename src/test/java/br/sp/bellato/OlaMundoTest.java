package br.sp.bellato;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import io.restassured.http.Method;
import io.restassured.response.*;
import java.util.*;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class OlaMundoTest
{
  @Test
  public void testeOlaMundo()
  {
    Response response = request(Method.GET, "https://restapi.wcaquino.me/ola");
    assertEquals("Ola Mundo!", response.getBody().asString());
    assertEquals(200, response.statusCode());

    ValidatableResponse validacao = response.then();
    validacao.statusCode(200);
  }

  @Test
  public void devoConhecerOutrasFormasRestAssured()
  {
    Response response = request(Method.GET, "https://restapi.wcaquino.me/ola");
    ValidatableResponse validacao = response.then();
    validacao.statusCode(200);

    get("https://restapi.wcaquino.me/ola").then().statusCode(200);

    given()
        .when()
        .get("https://restapi.wcaquino.me/ola")
        .then()
        .statusCode(200);
  }

  @Test
  public void devoConhecerMatchersHamcrest()
  {
    Assert.assertThat("Maria", Matchers.is("Maria"));
    Assert.assertThat(128, Matchers.is(128));
    Assert.assertThat(128, Matchers.isA(Integer.class));
    Assert.assertThat(128d, Matchers.isA(double.class));
    Assert.assertThat(128d, Matchers.greaterThan(120d));
    Assert.assertThat(128d, Matchers.lessThan(130d));

    List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
    assertThat(impares, hasSize(5));
    assertThat(impares, contains(1, 3, 5, 7, 9));
    assertThat(impares, containsInAnyOrder(1, 9, 7, 3, 5));
    assertThat(impares, hasItem(5));
    assertThat("Maria", is(not("João")));
    assertThat("Maria", anyOf(is("Maria"), is("Joaquina")));
    assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("aqui")));
  }

  @Test
  public void devoValidarBody() {
    given()
        .when()
        .get("https://restapi.wcaquino.me/ola")
        .then()
        .statusCode(200)
        .body(is("Ola Mundo!"))
        .body(containsString("Mundo"))
        .body(is(notNullValue()));
  }
}
