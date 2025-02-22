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
package com.someguyssoftware.treasure2.loot.function;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.treasure2.rune.TreasureRunes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

/**
 * 
 * @author Mark Gottschling on Mar 28, 2022
 *
 */
public class RandomRunestone extends LootFunction {
	private static final ResourceLocation LOCATION = new ResourceLocation("treasure2:random_runestone");

	/**
	 * 
	 * @param conditions
	 * @param levels
	 * @param defaultCharm
	 */
	public RandomRunestone(LootCondition[] conditions) {
		super(conditions);
	}

	@Override
	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		Random random = new Random();

		// randomly select a runestone
		Item runestone = TreasureRunes.itemValues().get(random.nextInt(TreasureRunes.itemValues().size()));

		return runestone == null ? stack : new ItemStack(runestone);
	}


	public static class Serializer extends LootFunction.Serializer<RandomRunestone> {
		public Serializer() {
			super(LOCATION, RandomRunestone.class);
		}

		/**
		 * 
		 */
		public void serialize(JsonObject json, RandomRunestone value, JsonSerializationContext context) {
		}

		/**
		 * 
		 */
		public RandomRunestone deserialize(JsonObject json, JsonDeserializationContext deserializationContext, LootCondition[] conditions) {
			return new RandomRunestone(conditions);
		}
	}
}