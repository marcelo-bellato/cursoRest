package br.sp.bellato;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class AuthTest
{

  @Test
  public void deveAcessarSwapi() {
    given()
        .log().all()
        .when()
        .get("https://swapi.dev/api/people/1")
        .then()
        .log().all()
        .statusCode(200)
        .body("name", is("Luke Skywalker"));
  }

    //https://api.openweathermap.org/data/2.5/weather?q=Osasco,BR&appid=65c30c29904cc80cbb07dee880d80ce7&units=metric

  @Test
  public void deveObterClima() {
    given()
        .log().all()
        .queryParam("q", "Osasco, BR")
        .queryParam("appid", "65c30c29904cc80cbb07dee880d80ce7")
        .queryParam("units", "metric")
        .when()
        .get("https://api.openweathermap.org/data/2.5/weather")
        .then()
        .log().all()
        .statusCode(200)
        .body("name", is("Osasco"))
        .body("coord.lon", is(-46.7917f))
        .body("main.temp", greaterThan(20f));
  }

  @Test
  public void naoDeveAcessarSemSenha() {
    given()
        .log().all()
        .when()
        .get("https://restapi.wcaquino.me/basicauth")
        .then()
        .log().all()
        .statusCode(401)
    ;
  }

  @Test
  public void DeveFazerAutenticacaoBasica() {
    given()
        .log().all()
        .when()
        .get("https://admin:senha@restapi.wcaquino.me/basicauth")
        .then()
        .log().all()
        .statusCode(200)
        .body("status", is("logado"))
    ;
  }

  @Test
  public void DeveFazerAutenticacaoBasicaDois() {
    given()
        .log().all()
        .auth().basic("admin", "senha")
        .when()
        .get("https://restapi.wcaquino.me/basicauth")
        .then()
        .log().all()
        .statusCode(200)
        .body("status", is("logado"));
  }

  @Test
  public void DeveFazerAutenticacaoBasicaComChalenge() {
    given()
        .log().all()
        .auth().preemptive().basic("admin", "senha")
        .when()
        .get("https://restapi.wcaquino.me/basicauth2")
        .then()
        .log().all()
        .statusCode(200)
        .body("status", is("logado"));
  }

  @Test
  public void deveFazerAutenticacaoComToken() {
    Map<String, String> login = new HashMap<>();
    login.put("email", "bellato.consultor@gmail.com");
    login.put("senha", "123456");

    String token = given()
        .log().all()
        .body(login)
        .contentType(ContentType.JSON)
        .when()
        .post("http://barrigarest.wcaquino.me/signin")
        .then()
        .log().all()
        .statusCode(200)
        .extract().path("token");

    given()
        .log().all()
        .header("Authorization", "JWT " + token)
        .when()
        .get("http://barrigarest.wcaquino.me/contas")
        .then()
        .log().all()
        .statusCode(200)
        .body("nome", hasItems("Conta para extrato"))
        ;
  }

  @Test
  public void deveAcessarAplicacaoWeb() {

    String cookie = given()
        .log().all()
        .formParam("email", "bellato.consultor@gmail.com")
        .formParam("senha", "123456")
        .contentType(ContentType.URLENC.withCharset("utf-8"))
        .when()
        .post("https://seubarriga.wcaquino.me/logar")
        .then()
        .log().all()
        .statusCode(200)
        .extract().header("set-cookie");

    cookie = cookie.split("=")[1].split(";")[0];
    System.out.println(cookie);

    String body = given()
        .log().all()
        .cookie("connect.sid", cookie)
        .when()
        .get("https://seubarriga.wcaquino.me/contas")
        .then()
        .log().all()
        .statusCode(200)
        .body("html.body.table.tbody.tr[0].td[0]", is("Conta para movimentacoes"))
        .extract().body().asString();

    System.out.println("-----------------------------------");
    XmlPath xmlPath = new XmlPath(CompatibilityMode.XML, body);
    System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
  }

}
