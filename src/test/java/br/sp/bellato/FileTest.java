package br.sp.bellato;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;

public class FileTest
{
  @Test
  public void deveObrigarEnviarArquivo() {
    given()
        .log().all()
        .when()
        .post("http://restapi.wcaquino.me/upload")
        .then()
        .log().all()
        .statusCode(404)
        .body("error", is("Arquivo não enviado"));
  }

  @Test
  public void deveFazerUploadDoArquivo() {
    given()
        .log().all()
        .multiPart("arquivo", new File("src/main/resources/users.pdf"))
        .when()
        .post("http://restapi.wcaquino.me/upload")
        .then()
        .log().all()
        .statusCode(200)
        .body("name", is("users.pdf"));
  }

  @Test
  public void naoDeveFazerUploadDeArquivoGrande() {
    given()
        .log().all()
        .multiPart("arquivo", new File("src/main/resources/empacotar_ios.zip"))
        .when()
        .post("http://restapi.wcaquino.me/upload")
        .then()
        .log().all()
        .time(lessThan(15000L))
        .statusCode(413);
  }

  @Test
  public void deveBaixarArquivo() throws IOException
  {
    byte[] image = given()
        .log().all()
        .when()
        .get("http://restapi.wcaquino.me/download")
        .then()
        .statusCode(200)
        .extract().asByteArray();

    File imagem = new File("src/main/resources/file.jpg");
    OutputStream output = new FileOutputStream(imagem);

    output.write(image);
    output.close();

    System.out.println(imagem.length());
    Assert.assertThat(imagem.length(), lessThan(100000L));
  }
}
