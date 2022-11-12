package pl.mgis.restapi.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String getHelloMessage(){
        return "Welcome my Dear frend!";
    }

}
