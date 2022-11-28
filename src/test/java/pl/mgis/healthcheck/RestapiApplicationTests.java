package pl.mgis.healthcheck;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"application.keycrypt.value=jabadabadoo"})
class RestapiApplicationTests {

	@Test
	void contextLoads() {
	}

}
