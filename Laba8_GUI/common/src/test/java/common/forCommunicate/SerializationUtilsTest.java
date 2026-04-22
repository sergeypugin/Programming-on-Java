package common.forCommunicate;

import common.data.Coordinates;
import common.data.Product;
import common.data.UnitOfMeasure;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SerializationUtilsTest {

    @Test
    void requestAndResponseShouldRoundTrip() throws Exception {
        Product product = new Product("Test", new Coordinates(12.5, 4.0f), 100, UnitOfMeasure.GRAMS, null);
        Request request = new Request("add", "", product, "user", "pass");
        Response response = new Response("ok", true, new ShowData(List.of(product)));

        Request requestCopy = (Request) SerializationUtils.deserialize(SerializationUtils.serialize(request));
        Response responseCopy = (Response) SerializationUtils.deserialize(SerializationUtils.serialize(response));

        assertEquals("add", requestCopy.getCommandName());
        assertEquals("user", requestCopy.getUsername());
        assertNotNull(requestCopy.getObjectArgument());

        assertTrue(responseCopy.isSuccess());
        assertInstanceOf(ShowData.class, responseCopy.getData());
        assertEquals(1, ((ShowData) responseCopy.getData()).getProducts().size());
    }
}
