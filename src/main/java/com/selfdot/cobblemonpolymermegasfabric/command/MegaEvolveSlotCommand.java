package com.selfdot.cobblemonpolymermegasfabric.command;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemonpolymermegasfabric.DataKeys;
import com.selfdot.cobblemonpolymermegasfabric.util.MegaUtils;
import net.minecraft.nbt.NbtCompound;
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

        String reasonCannotMegaEvolve = MegaUtils.reasonCannotMegaEvolve(player, pokemon);
        if (reasonCannotMegaEvolve != null) {
            context.getSource().sendError(Text.literal(reasonCannotMegaEvolve));
            return -1;
        }

        String megaType = DataKeys.MEGA;
        if (
            pokemon.getSpecies().getName().equalsIgnoreCase("charizard") ||
                pokemon.getSpecies().getName().equalsIgnoreCase("mewtwo")
        ) {
            NbtCompound nbtCompound = pokemon.heldItem().getNbt();
            if (nbtCompound == null) return 0;
            megaType = nbtCompound.getString(DataKeys.NBT_KEY_MEGA_STONE).endsWith("x") ?
                DataKeys.MEGA_X : DataKeys.MEGA_Y;
        }

        new FlagSpeciesFeature(megaType, true).apply(pokemon);
        context.getSource().sendMessage(Text.literal(
            pokemon.getDisplayName().getString() + " has mega evolved!"
        ));
        return SINGLE_SUCCESS;
    }

}
