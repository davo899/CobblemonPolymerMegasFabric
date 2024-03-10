package com.selfdot.cobblemonpolymermegasfabric.util;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.selfdot.cobblemonpolymermegasfabric.CobblemonPolymerMegasFabric;
import com.selfdot.cobblemonpolymermegasfabric.DataKeys;
import com.selfdot.cobblemonpolymermegasfabric.item.MegaStoneHeldItemManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.stream.Stream;

public class MegaUtils {

    public static String reasonCannotMegaEvolve(ServerPlayerEntity player, Pokemon pokemon) {
        if (
            Stream.of(DataKeys.MEGA, DataKeys.MEGA_X, DataKeys.MEGA_Y)
                .noneMatch(aspect -> pokemon.getSpecies().getFeatures().contains(aspect))
        ) {
            return "This Pokémon has no Mega form.";
        }

        if (!MegaStoneHeldItemManager.getInstance().isHoldingValidMegaStone(pokemon)) {
            return "This Pokémon is not holding their Mega Stone.";
        }

        PokemonBattle battle = BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player);
        if (battle == null) return null;

        if (CobblemonPolymerMegasFabric.getInstance().getHasMegaEvolvedThisBattle().contains(player.getUuid())) {
            return "Mega evolution can only be used once per battle.";
        }

        BattleActor playerBattleActor = battle.getActor(player);
        if (playerBattleActor == null) return "PlayerBattleActor is null (Report this error)";
        List<ActiveBattlePokemon> activeBattlePokemon = playerBattleActor.getActivePokemon();
        if (activeBattlePokemon.size() != 1) return "Mega evolution is currently only available in 1v1 battles.";
        BattlePokemon battlePokemon = activeBattlePokemon.get(0).getBattlePokemon();
        if (battlePokemon == null) return "BattlePokemon is null (Report this error)";

        if (!battlePokemon.getEffectedPokemon().getUuid().equals(pokemon.getUuid())) {
            return "This is not your active battle Pokémon.";
        }

        return null;
    }

}
