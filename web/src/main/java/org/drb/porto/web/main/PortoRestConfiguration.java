package org.drb.porto.web.main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.drb.porto.web.rest.v1"})
public class PortoRestConfiguration {
   public static void main(String[] args) {
      SpringApplication.run(PortoRestConfiguration.class, args);
   }
}


