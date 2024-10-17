package com.gnu.mojadol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@SpringBootTest(classes = MojadolApplication.class)
class MojadolApplicationTests {

    @Test
    void contextLoads() {
    }

}
