package org.drb.porto.web.rest.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/v1/test")
public class Test
{
   @GET
   @Produces("application/json")
   public TestData test()
   {
      return new TestData("gle", "be");
   }

   static class TestData
   {
      String toto;
      String titi;

      TestData(String toto, String titi)
      {
         this.toto = toto;
         this.titi = titi;
      }

      public String getToto()
      {
         return toto;
      }

      public String getTiti()
      {
         return titi;
      }
   }
}
