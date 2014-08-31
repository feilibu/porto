package org.drb.porto.web.rest.v1;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@Path("/v1/test")
public class Test
{
   public TestData test()
   {
      return new TestData("gle", "be");
   }

   @GET
   @Produces("text/javascript")
   public String test1(@QueryParam("callback") String callback) throws IOException
   {
      URL url = Resources.getResource("hs-example.json");
      return callback + "(" + Resources.toString(url, Charsets.UTF_8) + ");";
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
