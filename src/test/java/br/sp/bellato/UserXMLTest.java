package br.sp.bellato;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserXMLTest
{
  public static RequestSpecification reqSpec;
  public static ResponseSpecification resSpec;

  @BeforeClass
  public static void setup()
  {
    RestAssured.baseURI = "https://restapi.wcaquino.me";
    RestAssured.port = 443;

    RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
    reqBuilder.log(LogDetail.ALL);
    reqSpec = reqBuilder.build();

    ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
    resBuilder.expectStatusCode(200);
    resSpec = resBuilder.build();

    RestAssured.requestSpecification = reqSpec;
    RestAssured.responseSpecification = resSpec;
  }

  @Test
  public void devoTrabalharComXML()
  {


    given()
        .when()
        .get("/usersXML/3")
        .then()
        .body("user.name", is("Ana Julia"))
        .body("user.@id", is("3"))
        .body("user.filhos.name.size()", is(2))
        .body("user.filhos.name[0]", is("Zezinho"))
        .body("user.filhos.name[1]", is("Luizinho"))
        .body("user.filhos.name", hasItem("Luizinho"))
        .body("user.filhos.name", hasItems("Luizinho", "Zezinho"));
  }

  @Test
  public void devoTrabalharComXMLAvancado()
  {
    given()
        .when()
        .get("/usersXML/3")
        .then()

        .rootPath("user")
        .body("name", is("Ana Julia"))
        .body("@id", is("3"))

        .rootPath("user.filhos")
        .body("name.size()", is(2))
        .body("name[0]", is("Zezinho"))
        .body("name[1]", is("Luizinho"))
        .body("name", hasItem("Luizinho"))
        .body("name", hasItems("Luizinho", "Zezinho"));
  }

  @Test
  public void pesquisaAvancadaComXML()
  {
    given()
        .when()
        .get("/usersXML")
        .then()
        .statusCode(200)
        .rootPath("users.user")
        .body("size()", is(3))
        .body("findAll{it.age.toInteger() <= 25}.size()", is(2))
        .body("@id", hasItems("1", "2", "3"))
        .body("find{it.age == 25}.name", is("Maria Joaquina"))
        .body("findAll{it.name.toString().contains('n')}.name",
            hasItems("Maria Joaquina", "Ana Julia"))
        .body("age.collect{it.toInteger() * 2}", hasItems(40, 50, 60));
  }

  @Test
  public void devoFazerPesquisaAvancadaComXMLEJava()
  {
    String name = given()
        .when()
        .get("/usersXML")
        .then()
        .statusCode(200)
        .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");

    System.out.println(name);
    Assert.assertEquals("Maria Joaquina".toUpperCase(), name.toUpperCase());
  }

  @Test
  public void devoFazerPesquisaAvancadaComXMLEJavaNovo()
  {
    ArrayList<NodeImpl> name = given()
        .when()
        .get("/usersXML")
        .then()
        .statusCode(200)
        .extract().path("users.user.name.findAll{it.toString().contains('n')}");

    System.out.println(name);
    Assert.assertEquals(2, name.size());
    Assert.assertEquals("Maria Joaquina".toUpperCase(), name.get(0).toString().toUpperCase());
    Assert.assertTrue("ANA JULIA".equalsIgnoreCase(name.get(1).toString()));
  }

  @Test
  public void devoFazerPesquisaAvancadasComXpath()
  {
    given()
        .when()
        .get("/usersXML")
        .then()
        .statusCode(200)
        .body(hasXPath("count(/users/user)", is("3")))
        .body(hasXPath("/users/user[@id = '1']"))
        .body(hasXPath("//user[@id = '1']"))
        .body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))
        .body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos",
            allOf(containsString("Zezinho"), containsString("Luizinho"))))
        .body(hasXPath("/users/user/name", is("João da Silva")))
        .body(hasXPath("//name", is("João da Silva")))
        .body(hasXPath("//user[2]/name", is("Maria Joaquina")))
        .body(hasXPath("//user[last()]/name", is("Ana Julia")))
        .body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))
        .body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
        .body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
        .body(hasXPath("//user[age > 20 ][age < 30]/name", is("Maria Joaquina")));
  }

}
