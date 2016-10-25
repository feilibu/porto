package org.drb.porto.web.rest.v1;


import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

import com.jayway.restassured.response.Response;
import org.junit.Test;

public class GetStockDataIT {
    @Test
    public void testBasic()
    {
        Response r = given()
                .expect()
                .statusCode(200)
                .when()
                .get("http://localhost:9090/rest/v1/test");
        assertThat(r.header("Content-Type")).isEqualTo("text/javascript");
    }
}
