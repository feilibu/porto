package org.drb.porto.web.rest.v1;

import com.google.common.io.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.Charset;

@Controller
@RequestMapping("/v1/config/highcharts")
public class GetHCConfig {
    @RequestMapping(method= RequestMethod.GET, produces= "text/json")
    public @ResponseBody String get() {
        try {
            return Resources.toString(Resources.getResource("highcharts.json"), Charset.forName("utf-8"));
        } catch (IOException e) {
            return "{\"error\": \"" + e + "\"}";
        }
    }
}
