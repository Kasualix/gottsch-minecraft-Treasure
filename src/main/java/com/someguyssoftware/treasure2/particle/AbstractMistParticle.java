/**
 * 
 */
package com.someguyssoftware.treasure2.particle;

import com.someguyssoftware.gottschcore.spatial.ICoords;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.tileentity.MistEmitterTileEntity;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Mark Gottschling on Feb 17, 2020
 *
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractMistParticle extends SpriteTexturedParticle implements IMistParticle {
	/*
	 * particle lifespan: 200 = 10 seconds (10 * 20 ticks/sec): too short 400 = 20
	 * seconds (10 * 20 ticks/sec): too long 320 = 16 seconds (16 * 20 ticks/sec):
	 * seems to be a good length
	 */
	public static final int DEFAULT_PARTICLE_MAX_AGE = 320;
	public static final int DEFAULT_PARTICLE_MAX_SCALE = 12;

	public static final float DEFAULT_PARTICLE_ALPHA = 0.1F;
	public static final float DEFAULT_PARTICLE_GRAVITY = 0.001F;

	private static final int TRANSITION_IN_STOP_AGE = 105;
	private static final int TRANSITION_OUT_START_AGE = 240;
	private static final float TRANSITION_IN_START_SCALE = 2;
	private static final float TRANSITION_OUT_FINAL_SCALE = 2;
	private static final int DEFAULT_BRIGHTNESS = 0xe3e3e3;

	private ICoords parentEmitterCoords;
	private float transitionInScaleIncrement;
	private float transitionOutScaleIncrement;

	/*
	 * NOTES:
	 *  x, y, z = current position
	 *  xo, yo, zo, = old position
	 *  xd, yd, zd = motion (or delta)
	 */
	/**
	 * 
	 * @param worldIn
	 * @param posXIn
	 * @param posYIn
	 * @param posZIn
	 */
	protected AbstractMistParticle(ClientWorld worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);

		// add horizontal movement
		this.xd = (Math.random() * 2.0D - 1.0D) * 0.0050200D;
		this.zd = (Math.random() * 2.0D - 1.0D) * 0.0050200D;
		// turn off any initial verticle motion
		this.yd = 0;

	}

	@Override
	public void init() {
		/*
		 * determine scale increments. this is the difference between the max scale size
		 * and the variable scale size divided by variable (ex. stopAge - initial
		 * age(zero) = stopAge). this will not actually result in the scale reaching
		 * max/min size, because that would assume that this method is called every
		 * tick, which is unlikely.
		 */
		transitionInScaleIncrement = (provideMaxScale() - TRANSITION_IN_START_SCALE) / TRANSITION_IN_STOP_AGE;
		transitionOutScaleIncrement = (provideMaxScale() - TRANSITION_OUT_FINAL_SCALE)
				/ (provideMaxAge() - TRANSITION_OUT_START_AGE);

		// set default properties
		this.gravity = provideGravity();

		// set the max age
		this.setLifetime(provideMaxAge());

		// a value less than 1 turns on alpha blending. Otherwise, alpha blending is off
		// and the particle won't be transparent.
		this.setAlpha(provideAlpha());

		this.scale(TRANSITION_IN_START_SCALE);
//		this.setSize(TRANSITION_IN_START_SCALE, TRANSITION_IN_START_SCALE); // TODO redo - there IS particleScale proprety

	}

	/**
	 * call once per tick to update the Particle position, calculate collisions,
	 * remove when max lifetime is reached, etc
	 */
	@Override
	public void tick() {
Treasure.LOGGER.info("mist particle ticking...");
System.out.println("sys:mist particle ticking...");
		doPlayerCollisions(this.level);

		// save the previous location
		xo = x;
		yo = y;
		zo = z;

		// calculate the y motion if not on the ground
		if (!this.onGround) {
			this.yd -= provideGravity(); // gravity
		}
		this.move(this.xd, this.yd, this.zd);

		doTransitions();

		// NOTE the movement coupled with the transitions gives the mist a "roiling"
		// effect.

		// detect a collision while moving upwards (can't move up at all)
		if (yo == y && yd > 0) {
			// do nothing. not used for mist
		}

		// increase the age and test against max age
		if (this.age++ >= this.lifetime) {
			this.remove();
		}
	}

	public void doTransitions() {
		// transition the mist into the environment
		transitionIn(TRANSITION_IN_START_SCALE, TRANSITION_IN_STOP_AGE, getTransitionInScaleIncrement());

		// transition the mist out of the environment
		transitionOut(TRANSITION_OUT_FINAL_SCALE, TRANSITION_OUT_START_AGE, getTransitionOutScaleIncrement());
	}

	/**
	 * 
	 * @param initialScale
	 * @param stopAge
	 */
	@Override
	public void transitionIn(float initialScale, int stopAge, float scaleIncrement) {
		if (this.age < stopAge && getScale() < provideMaxScale()) {
			// increase in size
//			this.particleScale += scaleIncrement;
			this.scale(getScale() + scaleIncrement);
		}
	}

	@Override
	public void transitionOut(float finalScale, int startAge, float scaleIncrement) {
		if (age >= startAge && getScale() > finalScale) {
			// decrease in size
//			particleScale -= scaleIncrement;
			this.scale(getScale() - scaleIncrement);
		}
	}

	/**
	 * 
	 * @param world
	 */
	public void doPlayerCollisions(IWorld world) {
		if (getParentEmitterCoords() == null) {
			Treasure.LOGGER.debug("emitter coords is null");
			return;
		}

		// get the emitter tile entity
		TileEntity emitterTileEntity = world.getBlockEntity(getParentEmitterCoords().toPos());
		if (emitterTileEntity == null) {
			return;
		}

		// create an AxisAlignedBB for the particle
		AxisAlignedBB aabb = new AxisAlignedBB(x - 0.125D, y, z - 0.125D, x + 0.125D, y + 0.25D,
				z + 0.125D);

		// for all the players in the mist emitter tile entity list
		for (PlayerEntity player : ((MistEmitterTileEntity) emitterTileEntity).getPlayersWithinProximity()) {
			if (player.getBoundingBox().intersects(aabb)) {
				inflictEffectOnPlayer(player);
			}
		}
	}

	/**
	 * 
	 * @param player
	 */
	public abstract void inflictEffectOnPlayer(PlayerEntity player);

	/**
	 * Used to control what texture and lighting is used for the EntityFX. Returns
	 * 1, which means "use a texture from the blocks + items texture sheet" The
	 * vanilla layers are: normal particles: ignores world brightness lighting map
	 * Layer 0 - uses the particles texture sheet (textures\particle\particles.png)
	 * Layer 1 - uses the blocks + items texture sheet lit particles: changes
	 * brightness depending on world lighting i.e. block light + sky light Layer 3 -
	 * uses the blocks + items texture sheet (I think)
	 *
	 * @return
	 */
	//	@Override
	//	public int getFXLayer() {
	//		return 1;
	//	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	/*
	 * can be used to change the brightness of the rendered Particle.
	 */
	@Override
//	public int getBrightnessForRender(float partialTick) {
	public int getLightColor(float partialTick) {
		return DEFAULT_BRIGHTNESS;
	}

	/*
	 * this function is used by ParticleManager.addEffect() to determine whether
	 * depthmask writing should be on or not. FlameBreathFX uses alphablending (i.e.
	 * the FX is partially transparent) but we want depthmask writing on, otherwise
	 * translucent objects (such as water) render over the top of our breath, even
	 * if the particle is in front of the water and not behind.
	 */
	//	@Override
	//	public boolean shouldDisableDepth() {
	//		return true;
	//	}

	@Override
	public ICoords getParentEmitterCoords() {
		return parentEmitterCoords;
	}

	@Override
	public void setParentEmitterCoords(ICoords parentEmitterCoords) {
		this.parentEmitterCoords = parentEmitterCoords;
	}

	@Override
	public float getGravity() {
		return this.gravity;
	}

	@Override
	public float getScale() {
		return this.bbWidth;
	}

	@Override
	public float getAlpha() {
		return this.alpha;
	}

	@Override
	public float provideGravity() {
		return DEFAULT_PARTICLE_GRAVITY;
	}

	@Override
	public float provideAlpha() {
		return DEFAULT_PARTICLE_ALPHA;
	}

	@Override
	public int provideMaxAge() {
		return DEFAULT_PARTICLE_MAX_AGE;
	}

	@Override
	public float provideMaxScale() {
		return DEFAULT_PARTICLE_MAX_SCALE;
	}

	@Override
	public float getTransitionInScaleIncrement() {
		return transitionInScaleIncrement;
	}

	@Override
	public void setTransitionInScaleIncrement(float transitionInScaleIncrement) {
		this.transitionInScaleIncrement = transitionInScaleIncrement;
	}

	@Override
	public float getTransitionOutScaleIncrement() {
		return transitionOutScaleIncrement;
	}

	@Override
	public void setTransitionOutScaleIncrement(float transitionOutScaleIncrement) {
		this.transitionOutScaleIncrement = transitionOutScaleIncrement;
	}
}