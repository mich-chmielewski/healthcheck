package pl.mgis.healthcheck.tool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyCrypt {

    private String keyCrypt;
    private static String KEY_CRYPT;

    public KeyCrypt(@Value("${application.keycrypt.value}") String keyCrypt) {
        this.keyCrypt = keyCrypt;
        KEY_CRYPT = keyCrypt;
    }

    public String getKeyCrypt() {
        return keyCrypt;
    }

}