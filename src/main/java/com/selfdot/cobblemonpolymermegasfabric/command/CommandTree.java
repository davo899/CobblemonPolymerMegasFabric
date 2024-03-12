package com.selfdot.cobblemonpolymermegasfabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.selfdot.cobblemonpolymermegasfabric.CobblemonPolymerMegasFabric;
import com.selfdot.cobblemonpolymermegasfabric.item.MegaStoneHeldItemManager;
import com.selfdot.cobblemonpolymermegasfabric.util.CommandUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class CommandTree {

    public static void register(
        CommandDispatcher<ServerCommandSource> dispatcher,
        CommandRegistryAccess commandRegistryAccess,
        CommandManager.RegistrationEnvironment registrationEnvironment
    ) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("megas")
            .requires(source ->
                !CobblemonPolymerMegasFabric.getInstance().isDisabled() &&
                CommandUtils.hasPermission(source, "selfdot.megas.reload")
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("reload")
                .executes(new ReloadCommand())
            )
        );
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("megaevolve")
            .requires(source ->
                !CobblemonPolymerMegasFabric.getInstance().isDisabled() &&
                source.isExecutedByPlayer() &&
                CommandUtils.hasPermission(source, "selfdot.megas.megaevolve")
            )
            .executes(new MegaEvolveInBattleCommand())
        );
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("getmegastone")
            .requires(source ->
                !CobblemonPolymerMegasFabric.getInstance().isDisabled() &&
                source.isExecutedByPlayer() &&
                CommandUtils.hasPermission(source, "selfdot.megas.getmegastone")
            )
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("megaStone", string())
                .suggests((context, builder) -> {
                    MegaStoneHeldItemManager.getInstance().getAllMegaStoneIds().stream()
                        .filter(id ->
                            CobblemonPolymerMegasFabric.getInstance().getConfig().getMegaStoneWhitelist().contains(id)
                        )
                        .forEach(builder::suggest);
                    return builder.buildFuture();
                })
                .executes(new GetMegaStoneCommand())
            )
        );
    }

}
