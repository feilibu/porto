package org.drb.porto.web.rest.v1;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/v1/test")
public class Test
{
   public TestData test()
   {
      return new TestData("gle", "be");
   }

   @RequestMapping(method= RequestMethod.GET, produces="text/javascript")
   public String test1(@RequestParam("callback") String callback) throws IOException
   {
      URL url = Resources.getResource("hs-example.json");
      return callback + "([" + Resources.toString(url, Charsets.UTF_8) + "]);";
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
