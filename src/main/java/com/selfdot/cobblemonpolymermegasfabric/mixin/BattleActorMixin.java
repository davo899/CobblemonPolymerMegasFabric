package com.selfdot.cobblemonpolymermegasfabric.mixin;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.MoveActionResponse;
import com.cobblemon.mod.common.battles.ShowdownActionResponse;
import com.cobblemon.mod.common.battles.ShowdownMoveset;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.selfdot.cobblemonpolymermegasfabric.DataKeys;
import com.selfdot.cobblemonpolymermegasfabric.CobblemonPolymerMegasFabric;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(BattleActor.class)
public abstract class BattleActorMixin {

    @Shadow public abstract UUID getUuid();

    @Shadow private List<ShowdownActionResponse> responses;

    @Shadow @Final private List<ActiveBattlePokemon> activePokemon;

    @Inject(method = "writeShowdownResponse", at = @At("HEAD"), remap = false)
    private void injectWriteShowdownResponse(CallbackInfo ci) {
        UUID uuid = getUuid();
        if (CobblemonPolymerMegasFabric.getInstance().getToMegaEvolveThisTurn().remove(uuid)) {
            if (responses.size() != 1) return;
            if (!(responses.get(0) instanceof MoveActionResponse moveActionResponse)) return;
            if (activePokemon.size() != 1) return;
            BattlePokemon battlePokemon = activePokemon.get(0).getBattlePokemon();
            if (battlePokemon == null) return;
            String megaStone = battlePokemon.getHeldItemManager().showdownId(battlePokemon);
            if (megaStone == null) return;
            String megaType = DataKeys.MEGA;
            if      (megaStone.endsWith("-x")) megaType = DataKeys.MEGA_X;
            else if (megaStone.endsWith("-y")) megaType = DataKeys.MEGA_Y;
            new FlagSpeciesFeature(megaType, true).apply(battlePokemon.getOriginalPokemon());
            new FlagSpeciesFeature(megaType, true).apply(battlePokemon.getEffectedPokemon());
            moveActionResponse.setGimmickID(ShowdownMoveset.Gimmick.MEGA_EVOLUTION.getId());
            CobblemonPolymerMegasFabric.getInstance().getHasMegaEvolvedThisBattle().add(uuid);
        }
    }

}
