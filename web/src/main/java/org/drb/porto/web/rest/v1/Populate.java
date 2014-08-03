package org.drb.porto.web.rest.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/populate")
public class Populate
{
    @GET
    public String getIt() 
    {
      return "Got it!";
    }
}
