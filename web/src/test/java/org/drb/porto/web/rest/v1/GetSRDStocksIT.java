package org.drb.porto.web.rest.v1;

import com.jayway.restassured.response.Response;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class GetSRDStocksIT {
    @Test
    public void testFromDatabase()
    {
        Response r = given()
                .expect()
                .statusCode(200)
                .when()
                .get("http://localhost:9090/rest/v1/stocks");
        assertThat(r.header("Content-Type")).isEqualTo("text/json");
        String s = r.body().asString();
        assertThat(s.length()).isGreaterThan(1000);
        assertThat(s).contains("ADP.PA");
        assertThat(s).contains("Vilmorin");
        assertThat(s).contains("LU0088087324");
    }
}
