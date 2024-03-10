package com.selfdot.cobblemonpolymermegasfabric.command;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemonpolymermegasfabric.DataKeys;
import com.selfdot.cobblemonpolymermegasfabric.item.MegaStoneHeldItemManager;
import com.selfdot.cobblemonpolymermegasfabric.CobblemonPolymerMegasFabric;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class MegaEvolveInBattleCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PokemonBattle battle = BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player);
        if (battle == null) {
            context.getSource().sendError(Text.literal("This can only be used in battle."));
            return -1;
        }

        BattleActor playerBattleActor = battle.getActor(player);
        if (playerBattleActor == null) return 0;
        List<ActiveBattlePokemon> activeBattlePokemon = playerBattleActor.getActivePokemon();
        if (activeBattlePokemon.size() != 1) return 0;
        BattlePokemon battlePokemon = activeBattlePokemon.get(0).getBattlePokemon();
        if (battlePokemon == null) return 0;

        Pokemon pokemon = battlePokemon.getEffectedPokemon();

        if (Stream.of(DataKeys.MEGA, DataKeys.MEGA_X, DataKeys.MEGA_Y)
            .noneMatch(aspect -> pokemon.getSpecies().getFeatures().contains(aspect))
        ) {
            context.getSource().sendError(Text.literal("This Pokémon has no Mega form."));
            return -1;
        }
        if (!MegaStoneHeldItemManager.getInstance().isHoldingValidMegaStone(battlePokemon)) {
            context.getSource().sendError(Text.literal("This Pokémon is not holding their Mega Stone."));
            return -1;
        }
        CobblemonPolymerMegasFabric.getInstance().getBattleMegaEvolve().add(playerBattleActor.getUuid());
        context.getSource().sendMessage(Text.literal(
            pokemon.getDisplayName().getString() + " will mega evolve this turn if a move is used."
        ));
        return SINGLE_SUCCESS;
    }

}
