package com.patchworkmc.mixin.patches.bugfixes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;

import com.patchworkmc.impl.patches.bugfixes.EntityTypeBuilderShim;

@Mixin(EntityType.Builder.class)
@Implements(@Interface(iface = EntityTypeBuilderShim.class, prefix = "patchwork$"))
public abstract class MixinEntityTypeBuilder<T extends Entity> {
	@Unique
	private static final Logger LOGGER = LogManager.getLogger();

	@Shadow
	public abstract EntityType<T> build(String id);

	@Shadow
	@Final
	private EntityType.EntityFactory<T> factory;

	@Shadow
	@Final
	private EntityCategory category;

	@Shadow
	private boolean saveable;

	@Shadow
	private boolean summonable;

	@Shadow
	private boolean fireImmune;

	@Shadow
	private boolean field_19424;

	@Shadow
	private EntityDimensions size;

	/**
	 * Fixes MC-170128: Cannot build an EntityType without a datafixer due to an IllegalArgumentException.
	 */
	@Intrinsic(displace = true)
	public EntityType<T> patchwork$build(String id) {
		try {
			return this.build(id);
		} catch (IllegalArgumentException ex) {
			LOGGER.warn("No data fixer registered for entity {}", id);
			return new EntityType(this.factory, this.category, this.saveable, this.summonable, this.fireImmune, this.field_19424, this.size);
		}
	}

}
