package org.drb.porto.web.rest.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.drb.porto.db.PopulateDB;

@Path("/v1/populate")
public class Populate
{
    @GET
    public String populate() 
    {
       PopulateDB aPopulate = new PopulateDB( );
       aPopulate.DoPopulate();
       return "population done";
    }
}
