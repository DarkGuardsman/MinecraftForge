/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.energy;

import static net.minecraftforge.energy.EnergyActions.IGNORE_LIMITS;
import static net.minecraftforge.energy.EnergyActions.MATCH_EXACT;
import static net.minecraftforge.energy.EnergyActions.SIMULATE;

/**
 * Reference implementation of {@link IEnergyStorage}. Use/extend this or implement your own.
 * <p>
 * Derived from the Redstone Flux power system designed by King Lemming and originally utilized in Thermal Expansion and related mods.
 * Created with consent and permission of King Lemming and Team CoFH. Released with permission under LGPL 2.1 when bundled with Forge.
 */
public class EnergyStorage implements IEnergyStorage
{
    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public EnergyStorage(int capacity)
    {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorage(int capacity, int maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract)
    {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public int receiveEnergy(int maxReceive, FunctionEnergyAction actions)
    {
        //Reject if we can't receive
        if (!canReceive())
        {
            return 0;
        }

        //Check for match exact
        if (actions.apply(MATCH_EXACT))
        {
            //Enforce take all, not enough storage space
            if (getEnergyStored() + maxReceive > getMaxEnergyStored())
            {
                return 0;
            }
            //Enforce take all, limit would prevent exact
            else if (!actions.apply(IGNORE_LIMITS) && maxReceive > this.maxReceive)
            {
                return 0;
            }
        }

        //Calculate receive
        final int receiveLimit =  Math.min(actions.apply(IGNORE_LIMITS) ? capacity : this.maxReceive, maxReceive);
        final int energyReceived = Math.min(capacity - energy, receiveLimit);

        //Do action
        if (!actions.apply(SIMULATE))
        {
            setEnergyStored(energy + energyReceived);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, FunctionEnergyAction actions)
    {
        //Reject if we can't exact
        if (!canExtract())
        {
            return 0;
        }

        //Check for match exact
        if (actions.apply(MATCH_EXACT))
        {
            //Enforce take all, not enough energy
            if (maxExtract > energy)
            {
                return 0;
            }
            //Enforce take all, limit would prevent exact
            else if (!actions.apply(IGNORE_LIMITS) && maxExtract > this.maxExtract)
            {
                return 0;
            }
        }

        //Calculate extract
        final int extractLimit = Math.min(actions.apply(IGNORE_LIMITS) ? capacity : this.maxExtract, maxExtract);
        final int energyExtracted = Math.min(energy, extractLimit);

        //Do action
        if (!actions.apply(SIMULATE))
        {
            setEnergyStored(energy - energyExtracted);
        }

        return energyExtracted;
    }

    /**
     * Allows setting the energy directly
     *
     * @param value - value to set
     */
    public void setEnergyStored(int value)
    {
        final int prevEnergy = this.energy;
        this.energy = Math.max(0, Math.min(value, getMaxEnergyStored()));
        if (prevEnergy != this.energy)
        {
            onEnergyChanged(prevEnergy, value);
        }
    }

    /**
     * Triggered any time the energy state changes.
     *
     * @param prevValue - value before changes were applied
     * @param newValue  - new value after changes were applied
     */
    protected void onEnergyChanged(int prevValue, int newValue)
    {
        //Override this to implement update/sync logic
    }

    @Override
    public int getEnergyStored()
    {
        return energy;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return capacity;
    }

    @Override
    public boolean canExtract()
    {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive()
    {
        return this.maxReceive > 0;
    }
}
