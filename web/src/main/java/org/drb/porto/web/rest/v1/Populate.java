package org.drb.porto.web.rest.v1;

import org.drb.porto.db.PopulateDB;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1/populate")
public class Populate
{
    @RequestMapping(method= RequestMethod.GET,produces = "text/plain")
    public @ResponseBody String populate()
    {
       PopulateDB aPopulate = new PopulateDB( );
       aPopulate.DoPopulate();
       return "population done";
    }
}
