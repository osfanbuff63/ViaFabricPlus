package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrushItem.class)
public class MixinBrushItem {

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    public void changeMaxUseTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_4)) {
            cir.setReturnValue(225);
        }
    }
}
