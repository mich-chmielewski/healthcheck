package pl.mgis.healthcheck.tool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyCrypt {

    private final String keyCrypt;

    public KeyCrypt(@Value("${application.keycrypt.value}") String keyCrypt) {
        this.keyCrypt = keyCrypt;
    }

    public String getKeyCrypt() {
        return keyCrypt;
    }
}
