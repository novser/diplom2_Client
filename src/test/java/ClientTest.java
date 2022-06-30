import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ClientTest {

    @Test
    public void testRun() throws IOException {
        Client client = new Client();
        client.start();
        Assertions.assertTrue(client.isAlive());
    }
}
