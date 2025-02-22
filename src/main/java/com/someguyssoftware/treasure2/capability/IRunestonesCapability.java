package com.someguyssoftware.treasure2.capability;

import java.util.List;

import com.google.common.collect.Multimap;
import com.someguyssoftware.treasure2.rune.IRune;
import com.someguyssoftware.treasure2.rune.IRuneEntity;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IRunestonesCapability /*extends IMagicsInventorySupportCapability*/ {

	boolean isBindable();

	void setBindable(boolean bindable);

	boolean isSocketable();

	void setSocketable(boolean socketable);

	boolean hasRunestone();

	void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn);
	
	void clear();
	int getCurrentSize(InventoryType type);
	int getMaxSize(InventoryType type);
	
	boolean contains(IRune charm);
	
	/**
	 * @param type
	 * @param entity
	 */
	void add(InventoryType type, IRuneEntity entity);

	boolean remove(InventoryType type, IRuneEntity entity);
	
	List<IRuneEntity> getEntities(InventoryType type);

	/**
	 * Copy of all runestone entities. Not attached to underlying map.
	 * @return
	 */
	Multimap<InventoryType, IRuneEntity> getEntitiesCopy();

	void copyTo(ItemStack stack);
	
	void transferTo(ItemStack stack, InventoryType sourceType, InventoryType destType);	
}
