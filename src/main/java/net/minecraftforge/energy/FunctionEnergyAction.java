package net.minecraftforge.energy;

/**
 * Functional interface for use with {@link IEnergyStorage} for checking if an action should be applied
 * <p>
 * Created by DarkGuardsman on 8/25/2019.
 */
@FunctionalInterface
public interface FunctionEnergyAction
{
    /**
     * @param actions
     * @return
     */
    boolean apply(EnergyActions actions);
}
