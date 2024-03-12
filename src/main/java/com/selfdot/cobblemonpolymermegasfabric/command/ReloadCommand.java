package com.selfdot.cobblemonpolymermegasfabric.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemonpolymermegasfabric.CobblemonPolymerMegasFabric;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        CobblemonPolymerMegasFabric.getInstance().getConfig().reload();
        context.getSource().sendMessage(Text.literal("Reloaded CobblemonPolymerMegas config."));
        return SINGLE_SUCCESS;
    }

}
