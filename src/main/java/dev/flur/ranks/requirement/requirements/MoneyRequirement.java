package dev.flur.ranks.requirement.requirements;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.AnnotatedRequirement;
import dev.flur.ranks.requirement.annotations.RequirementAnnotation;
import dev.flur.ranks.vault.VaultProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequirementAnnotation(
        name = "money",
        maximum = 1,
        usage = "Format: amount"
)
public final class MoneyRequirement extends AnnotatedRequirement {

    // For testing purposes
    private static @Nullable VaultProvider testVaultProvider = null;

    public static void setTestVaultProvider(@Nullable VaultProvider provider) {
        testVaultProvider = provider;
    }

    public MoneyRequirement(String[] params) {
        super(params);
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        return getEconomyBalance(player) >= getRequiredAmount();
    }

    private double getEconomyBalance(@NotNull Player player) {
        if (testVaultProvider != null) {
            return testVaultProvider.getEconomy().getBalance(player);
        }
        return Ranks.getPlugin(Ranks.class).getVaultProvider().getEconomy().getBalance(player);
    }

    private double getRequiredAmount() {
        return super.amount;
    }

    @Override
    public String toString() {
        return "money: " + super.amount;
    }
}
