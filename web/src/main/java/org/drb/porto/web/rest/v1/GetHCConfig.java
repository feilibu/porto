package org.drb.porto.web.rest.v1;

import com.google.common.io.Resources;
import org.drb.porto.base.Quotes;

import javax.ws.rs.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

@Path("/v1/config/highcharts")
public class GetHCConfig {
    @GET
    @Produces("text/json")
    public String get() {
        try {
            return Resources.toString(Resources.getResource("highcharts.json"), Charset.forName("utf-8"));
        } catch (IOException e) {
            return "{\"error\": \"" + e + "\"}";
        }
    }
}
