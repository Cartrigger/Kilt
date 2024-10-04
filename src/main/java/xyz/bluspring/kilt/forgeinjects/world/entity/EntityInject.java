// TRACKED HASH: 34fb617752c8022973f9dca4fb9eed32600931bf
package xyz.bluspring.kilt.forgeinjects.world.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.fabricators_of_create.porting_lib.entity.extensions.EntityExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProviderImpl;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.bluspring.kilt.helpers.mixin.Extends;
import xyz.bluspring.kilt.injections.CapabilityProviderInjection;
import xyz.bluspring.kilt.injections.world.entity.EntityInjection;

import java.util.function.BiPredicate;

@Mixin(Entity.class)
@Extends(CapabilityProvider.class)
public abstract class EntityInject implements IForgeEntity, CapabilityProviderInjection, ICapabilityProviderImpl<Entity>, EntityExtensions, EntityInjection {
    @Shadow public Level level;

    @Shadow public abstract float getBbWidth();

    @Shadow public abstract float getBbHeight();

    @Shadow protected abstract void unsetRemoved();

    @Shadow protected abstract float getEyeHeight(Pose pose, EntityDimensions dimensions);

    private boolean canUpdate = true;

    @Override
    public boolean canUpdate() {
        return canUpdate;
    }

    @Override
    public void canUpdate(boolean value) {
        canUpdate = value;
    }

    @Override
    public CompoundTag getPersistentData() {
        return this.getCustomData();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return this.level.random.nextFloat() < fallDistance - .5F
                && ((Object) this) instanceof LivingEntity
                && (((Object) this) instanceof Player || ForgeEventFactory.getMobGriefingEvent(this.level, ((Entity) (Object) this)))
                && this.getBbWidth() * this.getBbWidth() * this.getBbHeight() > .512F;
    }

    private boolean isAddedToWorld;

    @Override
    public boolean isAddedToWorld() {
        return isAddedToWorld;
    }

    @Override
    public void onAddedToWorld() {
        isAddedToWorld = true;
    }

    @Override
    public void onRemovedFromWorld() {
        isAddedToWorld = false;
    }

    @Override
    public void revive() {
        this.unsetRemoved();
        this.reviveCaps();
    }

    public float getEyeHeightAccess(Pose pose, EntityDimensions size) {
        return this.getEyeHeight(pose, size);
    }

    // TODO: Implement these
    @Override
    public double getFluidTypeHeight(FluidType type) {
        return 0;
    }

    @Override
    public FluidType getMaxHeightFluidType() {
        return null;
    }

    @Override
    public boolean isInFluidType(BiPredicate<FluidType, Double> predicate, boolean forAllTypes) {
        return false;
    }

    @Override
    public boolean isInFluidType() {
        return false;
    }

    @Override
    public FluidType getEyeInFluidType() {
        return null;
    }

    @WrapOperation(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean kilt$captureSpawnDrops(Level instance, Entity entity, Operation<Boolean> original) {
        if (captureDrops() != null) {
            captureDrops().add((ItemEntity) entity);
            return false;
        } else {
            return original.call(instance, entity);
        }
    }
}