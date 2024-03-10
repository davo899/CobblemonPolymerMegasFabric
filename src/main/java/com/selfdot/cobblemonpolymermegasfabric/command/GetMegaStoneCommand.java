package com.selfdot.cobblemonpolymermegasfabric.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemonpolymermegasfabric.item.MegaStoneHeldItemManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class GetMegaStoneCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        player.giveItemStack(MegaStoneHeldItemManager.getInstance().getMegaStoneItem(
            StringArgumentType.getString(context, "megaStone")
        ));
        return SINGLE_SUCCESS;
    }

}
