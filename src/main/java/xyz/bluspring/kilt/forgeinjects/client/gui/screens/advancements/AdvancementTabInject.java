// TRACKED HASH: 4b65780a21b72c6641617a99b2ca8d8e31521909
package xyz.bluspring.kilt.forgeinjects.client.gui.screens.advancements;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.kilt.helpers.mixin.CreateInitializer;
import xyz.bluspring.kilt.injections.client.gui.screens.advancements.AdvancementTabInjection;
import xyz.bluspring.kilt.injections.client.gui.screens.advancements.AdvancementTabTypeInjection;

@Mixin(AdvancementTab.class)
public class AdvancementTabInject implements AdvancementTabInjection {
    @Shadow @Final private Advancement advancement;
    @Unique private int page;

    public AdvancementTabInject(Minecraft p_97145_, AdvancementsScreen p_97146_, AdvancementTabType p_97147_, int p_97148_, Advancement p_97149_, DisplayInfo p_97150_) {}

    @CreateInitializer
    public AdvancementTabInject(Minecraft mc, AdvancementsScreen screen, AdvancementTabType type, int index, int page, Advancement adv, DisplayInfo info) {
        this(mc, screen, type, index, adv, info);
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void kilt$setPage(int page) {
        this.page = page;
    }

    @Redirect(method = "create", at = @At(value = "NEW", target = "net/minecraft/client/gui/screens/advancements/AdvancementTab"))
    private static AdvancementTab kilt$createWithPages(Minecraft minecraft, AdvancementsScreen advancementsScreen, AdvancementTabType advancementTabType, int tabIndex, Advancement advancement, DisplayInfo displayInfo, @Share("originalTabs") LocalIntRef originalTabsValue) {
        return AdvancementTabInjection.create(minecraft, advancementsScreen, advancementTabType, originalTabsValue.get() % AdvancementTabTypeInjection.MAX_TABS, originalTabsValue.get() / AdvancementTabTypeInjection.MAX_TABS, advancement, displayInfo);
    }

    @Inject(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTabType;getMax()I", ordinal = 0, shift = At.Shift.BY, by = -2))
    private static void kilt$checkWithMaxTabs(Minecraft minecraft, AdvancementsScreen screen, int tabIndex, Advancement advancement, CallbackInfoReturnable<AdvancementTab> cir, @Share("originalTabs") LocalIntRef originalTabsValue, @Local(argsOnly = true) LocalIntRef tabIndexRef) {
        originalTabsValue.set(tabIndex);
        tabIndexRef.set(tabIndex % AdvancementTabTypeInjection.MAX_TABS);
    }

    @Inject(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTabType;getMax()I", ordinal = 1, shift = At.Shift.BY, by = -2))
    private static void kilt$fixMaxTabs(Minecraft minecraft, AdvancementsScreen screen, int tabIndex, Advancement advancement, CallbackInfoReturnable<AdvancementTab> cir, @Local(argsOnly = true) LocalIntRef tabIndexRef, @Share("originalTabs") LocalIntRef originalTabsValue) {
        tabIndexRef.set(originalTabsValue.get());
    }
}