/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package com.someguyssoftware.treasure2.charm;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.treasure2.capability.InventoryType;
import com.someguyssoftware.treasure2.charm.cost.ICostEvaluator;
import com.someguyssoftware.treasure2.enums.Rarity;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author Mark Gottschling on Apr 25, 2020
 *
 */
public interface ICharm {
	public ResourceLocation getName();
	public String getType();
	public int getLevel();
	public double getMana();
	public int getDuration();
	public double getFrequency();
	public Rarity getRarity();
	public int getPriority();
	public double getRange();
	public double getCooldown();
	public double getAmount();
	public boolean isEffectStackable();
	public boolean isExclusive();
	public int getRecharges();	
	
	public ICharmEntity createEntity();
	public ICharmEntity createEntity(ICharmEntity entity);
	
    public boolean update(World world, Random random, ICoords coords, EntityPlayer player, Event event, final ICharmEntity entity);

    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn, ICharmEntity entity, InventoryType type);
    
	public NBTTagCompound save(NBTTagCompound nbt);
	
	public boolean isCurse();
	
	public Class<?> getRegisteredEvent();
	
	ICostEvaluator getCostEvaluator();
	TextFormatting getCharmLabelColor();
	TextFormatting getCharmDescColor();
}
