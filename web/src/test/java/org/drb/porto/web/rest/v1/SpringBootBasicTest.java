package org.drb.porto.web.rest.v1;

import com.jayway.restassured.response.Response;
import org.drb.porto.web.main.PortoRestConfiguration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;

import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortoRestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class SpringBootBasicTest {
   @LocalServerPort
   private int port;

   @Value("${local.management.port}")
   private int mgt;

   @Autowired
   private TestRestTemplate testRestTemplate;

   @Test
   public void shouldReturn200WhenSendingRequestToController() throws Exception {
      @SuppressWarnings("rawtypes")
      ResponseEntity<String> entity = testRestTemplate.getForEntity(
              "http://localhost:" + port + "/v1/test?callback=toto", String.class);

      then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
      String body = entity.getBody();
      System.err.println("url: http://localhost:" + port + "/v1/test" + "Coucou:" + body);
   }

   @Test
   public void shouldReturn200WhenSendingRequestToManagementEndpoint() throws Exception {
      @SuppressWarnings("rawtypes")
      ResponseEntity<Map> entity = testRestTemplate.getForEntity(
              "http://localhost:" + mgt + "/info", Map.class);

      then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
   }

   public void testBasic()
   {
      Response r = given()
              .expect()
              .statusCode(200)
              .when()
              .get("http://localhost:" + port + "/v1/test");
      assertThat(r.header("Content-Type")).isEqualTo("text/javascript");
   }

   @Test
   public void testFromDatabase()
   {
      Response r = given()
              .param("callback", "toto")
              .expect()
              .statusCode(200)
              .when()
              .get("http://localhost:" + port + "/v1/stock/ADP.PA");
      assertThat(r.header("Content-Type")).contains("text/javascript");
      String s = r.body().asString();
      System.err.println("**********" + s + "****************");
      assertThat(s.length()).isGreaterThan(1000);
   }

}
