package com.someguyssoftware.treasure2.tileentity;

import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.treasure2.inventory.AbstractChestContainer;
import com.someguyssoftware.treasure2.inventory.ITreasureContainer;
import com.someguyssoftware.treasure2.inventory.StandardChestContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * 
 * @author Mark Gottschling on Jan 19, 2018
 *
 */
public class CrateChestTileEntity extends AbstractTreasureChestTileEntity {
	/** The current angle of the latch (between 0 and 1) */
	public float latchAngle;
	/** The angle of the latch last tick */
	public float prevLatchAngle;

	/**
	 * 
	 * @param texture
	 */
	public CrateChestTileEntity() {
		super(TreasureTileEntities.CRATE_CHEST_TILE_ENTITY_TYPE);
		setCustomName(new TranslationTextComponent("display.crate_chest.name"));
	}

	public CrateChestTileEntity(TileEntityType<? extends CrateChestTileEntity> tileEntityType) {
		super(tileEntityType);
		setCustomName(new TranslationTextComponent("display.crate_chest.name"));
	}

	/**
	 * 
	 * @param windowID
	 * @param inventory
	 * @param player
	 * @return
	 */
	public Container createServerContainer(int windowID, PlayerInventory inventory, PlayerEntity player) {
		return new StandardChestContainer(windowID, inventory, this);
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	@Override
	public void tick() {
		int i = this.getBlockPos().getX();
		int j = this.getBlockPos().getY();
		int k = this.getBlockPos().getZ();
		++this.ticksSinceSync;

		if (WorldInfo.isServerSide(getLevel()) && this.openCount != 0
				&& (this.ticksSinceSync + i + j + k) % 200 == 0) {
			this.openCount = 0;

			for (PlayerEntity player : this.getLevel().getEntitiesOfClass(PlayerEntity.class,
					new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F),
							(double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F),
							(double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F)))) {
				if (player.containerMenu instanceof ITreasureContainer) {
					//					IInventory iinventory = ((ITreasureContainer) player.openContainer).getContents();
					IInventory inventory = ((AbstractChestContainer)player.containerMenu).getContents();

					if (inventory == this) {
						++this.openCount;
					}
				}
			}
		}

		this.prevLidAngle = this.lidAngle;

		if (this.openCount > 0 && this.lidAngle == 0.0F) {
			this.playSound(SoundEvents.CHEST_OPEN);
		}

		if (this.openCount == 0 && this.lidAngle > 0.0F || this.openCount > 0 && this.lidAngle < .125F) {
			float f2 = this.lidAngle;

			if (this.openCount > 0) {
				this.lidAngle += 0.0125F;
			} else {
				this.lidAngle -= 0.0125F;
			}

			if (this.lidAngle > 0.125F) {
				this.lidAngle = 0.125F;
			}

			if (this.lidAngle < 0.06F && f2 >= 0.06F) {
				//				double d3 = (double) i + 0.5D;
				//				double d0 = (double) k + 0.5D;
				//
				//				this.world.playSound((EntityPlayer) null, d3, (double) j + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE,
				//						SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);

				this.playSound(SoundEvents.CHEST_CLOSE);
			}

			if (this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}
	}

//	@Override
//	public float getLidAngle(float partialTicks) {
//		return this.lidAngle;
//	}

}
