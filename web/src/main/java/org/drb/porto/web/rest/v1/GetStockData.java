package org.drb.porto.web.rest.v1;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.drb.porto.db.PopulateDB;

import javax.ws.rs.*;
import java.io.IOException;
import java.net.URL;

@Path("/v1/stock/{ticker}")

public class GetStockData
{
    @GET
    @Produces("text/javascript")
    public String getStockData(@PathParam("ticker") String ticker, @QueryParam("callback") String callback) throws IOException {



        URL url = Resources.getResource("hs-example.json");
        return callback + "(" + Resources.toString(url, Charsets.UTF_8) + ");";
    }
}
