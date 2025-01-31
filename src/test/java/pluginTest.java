import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.flur.ranks.Ranks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class pluginTest {

    private ServerMock server;
    private Ranks plugin;

    @BeforeEach
    public void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Ranks.class);
    }

    @AfterEach
    public void deconstruct() {
        MockBukkit.unmock();
    }

    @Test @Disabled
    public void initializationTest() {
        System.out.println("init?");
    }
}
