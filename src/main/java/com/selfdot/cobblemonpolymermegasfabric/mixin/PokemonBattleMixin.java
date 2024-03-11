package com.selfdot.cobblemonpolymermegasfabric.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.selfdot.cobblemonpolymermegasfabric.CobblemonPolymerMegasFabric;
import com.selfdot.cobblemonpolymermegasfabric.util.MegaUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonBattle.class)
public abstract class PokemonBattleMixin {

    @Inject(method = "end", at = @At("TAIL"), remap = false)
    private void injectEnd(CallbackInfo ci) {
        PokemonBattle thisBattle = (PokemonBattle)(Object)this;
        MegaUtils.deMegaEvolveAll(thisBattle);
        thisBattle.getActors().forEach(
            actor -> CobblemonPolymerMegasFabric.getInstance().getHasMegaEvolvedThisBattle().remove(actor.getUuid())
        );
    }

}
