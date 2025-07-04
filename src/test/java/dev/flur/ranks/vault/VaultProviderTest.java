package dev.flur.ranks.vault;

import dev.flur.ranks.Ranks;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertSame;

public class VaultProviderTest {

    @Mock
    private Economy mockEconomy;
    
    @Mock
    private Permission mockPermission;
    
    private VaultProvider mockVaultProvider;
    private VaultProvider originalVaultProvider;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Save the original VaultProvider to restore it after the test
        originalVaultProvider = Ranks.getVaultProvider();
        
        // Create a mock VaultProvider
        mockVaultProvider = new VaultProvider() {
            @Override
            public Economy getEconomy() {
                return mockEconomy;
            }
            
            @Override
            public Permission getPermissions() {
                return mockPermission;
            }
        };
        
        // Set the mock VaultProvider
        Ranks.setVaultProvider(mockVaultProvider);
    }
    
    @AfterEach
    public void tearDown() {
        // Restore the original VaultProvider
        Ranks.setVaultProvider(originalVaultProvider);
    }
    
    @Test
    public void testMockEconomy() {
        // Verify that Ranks.getEconomy() returns the mock Economy
        assertSame(mockEconomy, Ranks.getEconomy());
    }
    
    @Test
    public void testMockPermission() {
        // Verify that Ranks.getPermissions() returns the mock Permission
        assertSame(mockPermission, Ranks.getPermissions());
    }
}