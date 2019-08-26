package net.minecraftforge.energy;

/**
 * Enum of actions that can be taken on {@link IEnergyStorage} capability
 * <p>
 * Created by DarkGuardsman on 8/25/2019.
 */
public enum EnergyActions
{
    /** Asks the storage to simulate the action */
    SIMULATE,
    /** Asks the storage to ignore limits */
    IGNORE_LIMITS,
    /** Asks the storage to match the exact request */
    MATCH_EXACT
}
