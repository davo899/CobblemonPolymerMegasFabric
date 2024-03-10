package com.selfdot.cobblemonpolymermegasfabric.command;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemonpolymermegasfabric.DataKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MegaEvolveSlotCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        PokemonBattle battle = BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player);
        if (battle != null) {
            context.getSource().sendError(Text.literal("This cannot be used in battle."));
            return -1;
        }

        Pokemon pokemon = PartySlotArgumentType.Companion.getPokemon(context, "pokemon");
        if (!pokemon.getSpecies().getFeatures().contains(DataKeys.MEGA)) {
            context.getSource().sendError(Text.literal("This Pokémon has no Mega form."));
            return -1;
        }
        new FlagSpeciesFeature(DataKeys.MEGA, true).apply(pokemon);
        return SINGLE_SUCCESS;
    }

}
