package br.sp.bellato;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class VerbosTest
{

  @Test
  public void devoSalvarUsuario()
  {
    given()
        .log().all()
        .contentType("application/json")
        .body("{\"name\": \"Jose\",\"age\": 50}")
        .when()
        .post("https://restapi.wcaquino.me/users")
        .then()
        .log().all()
        .statusCode(201)
        .body("id", is(notNullValue()))
        .body("name", is("Jose"))
        .body("age", is(50));
  }

  @Test
  public void devoSalvarUsuarioUsandoMap()
  {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Usuario via map");
    params.put("age", 25);

    given()
        .log().all()
        .contentType("application/json")
        .body(params)
        .when()
        .post("https://restapi.wcaquino.me/users")
        .then()
        .log().all()
        .statusCode(201)
        .body("id", is(notNullValue()))
        .body("name", is("Usuario via map"))
        .body("age", is(25));
  }

  @Test
  public void devoSalvarUsuarioUsandoObjeto()
  {
    User user = new User("Usuario via map", 35);

    given()
        .log().all()
        .contentType("application/json")
        .body(user)
        .when()
        .post("https://restapi.wcaquino.me/users")
        .then()
        .log().all()
        .statusCode(201)
        .body("id", is(notNullValue()))
        .body("name", is("Usuario via map"))
        .body("age", is(35));
  }

  @Test
  public void devoDeserealizarObjetoAoSalvarUsuario()
  {
    User user = new User("Usuario deserializado", 35);

    User usuarioInserido = given()
        .log().all()
        .contentType("application/json")
        .body(user)
        .when()
        .post("https://restapi.wcaquino.me/users")
        .then()
        .log().all()
        .statusCode(201)
        .extract().body().as(User.class);

    System.out.println(usuarioInserido);
    Assert.assertThat(usuarioInserido.getId(), notNullValue());
    Assert.assertEquals("Usuario deserializado", usuarioInserido.getName());
    Assert.assertThat(usuarioInserido.getAge(), is(35));
  }

  @Test
  public void devoSalvarUsuarioXML()
  {
    given()
        .log().all()
        .contentType(ContentType.XML)
        .body("<user><name>Jose</name><age>50</age></user>")
        .when()
        .post("https://restapi.wcaquino.me/usersXML")
        .then()
        .log().all()
        .statusCode(201)
        .body("user.@id", is(notNullValue()))
        .body("user.name", is("Jose"))
        .body("user.age", is("50"));
  }

  @Test
  public void devoSalvarUsuarioXMLUsandoObjeto()
  {
    User user = new User("Usuario XML", 40);

    given()
        .log().all()
        .contentType(ContentType.XML)
        .body(user)
        .when()
        .post("https://restapi.wcaquino.me/usersXML")
        .then()
        .log().all()
        .statusCode(201)
        .body("user.@id", is(notNullValue()))
        .body("user.name", is("Usuario XML"))
        .body("user.age", is("40"));
  }

  @Test
  public void devoDeserealizarAoSalvarUsuarioXML()
  {
    User user = new User("Usuario XML", 40);

    User usuario = given()
        .log().all()
        .contentType(ContentType.XML)
        .body(user)
        .when()
        .post("https://restapi.wcaquino.me/usersXML")
        .then()
        .log().all()
        .statusCode(201)
        .extract().body().as(User.class);

    Assert.assertThat(usuario.getId(), notNullValue());
    Assert.assertThat(usuario.getName(), is("Usuario XML"));
    Assert.assertThat(usuario.getAge(), is(40));
    Assert.assertThat(usuario.getSalary(), nullValue());
  }

  @Test
  public void naoDeveSalvaUsuarioSemNome()
  {
    given()
        .log().all()
        .contentType("application/json")
        .body("{\"age\": 50}")
        .when()
        .post("https://restapi.wcaquino.me/users")
        .then()
        .log().all()
        .statusCode(400)
        .body("id", is(nullValue()))
        .body("error", is("Name é um atributo obrigatório"));
  }

  @Test
  public void deveAlterarUsuario()
  {
    given()
        .log().all()
        .contentType(ContentType.JSON)
        .body("{\"name\": \"Jose alterado\",\"age\": 80}")
        .when()
        .put("https://restapi.wcaquino.me/users/1")
        .then()
        .log().all()
        .statusCode(200)
        .body("id", is(1))
        .body("name", is("Jose alterado"))
        .body("age", is(80))
        .body("salary", is(1234.5678f));
  }

  @Test
  public void deveRemoverUsuario()
  {
    given()
        .log().all()
        .when()
        .delete("https://restapi.wcaquino.me/users/1")
        .then()
        .log().all()
        .statusCode(204);
  }

  @Test
  public void naoDeveRemoverUsuarioInexistente()
  {
    given()
        .log().all()
        .when()
        .delete("https://restapi.wcaquino.me/users/1000")
        .then()
        .log().all()
        .statusCode(400)
        .body("error", is("Registro inexistente"));
  }
}


