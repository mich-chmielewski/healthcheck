package pl.mgis.restapi.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mgis.restapi.service.HelloService;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;


@RestController
@Scope(value = SCOPE_SINGLETON)
public class HelloController extends Thread {

    private final HelloService helloservice;
    private int counter;

    public HelloController(HelloService helloservice) {
        this.helloservice = helloservice;
    }


    @GetMapping("/")
    public ResponseEntity<String> getHello() {
        counter += 1;
        return new ResponseEntity<>(helloservice.getHelloMessage() + counter + " -> " + currentThread().getName(), HttpStatus.OK);
    }
}
