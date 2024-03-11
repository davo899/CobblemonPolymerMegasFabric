package com.selfdot.cobblemonpolymermegasfabric;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeatureProvider;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatures;
import com.cobblemon.mod.common.api.pokemon.helditem.HeldItemProvider;
import com.cobblemon.mod.common.pokemon.Species;
import com.selfdot.cobblemonpolymermegasfabric.command.CommandTree;
import com.selfdot.cobblemonpolymermegasfabric.item.MegaStoneHeldItemManager;
import com.selfdot.cobblemonpolymermegasfabric.item.MegaStoneItem;
import com.selfdot.cobblemonpolymermegasfabric.util.DisableableMod;
import com.selfdot.cobblemonpolymermegasfabric.util.MegaUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import kotlin.Unit;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CobblemonPolymerMegasFabric extends DisableableMod {

    private static CobblemonPolymerMegasFabric INSTANCE;
    public static CobblemonPolymerMegasFabric getInstance() {
        return INSTANCE;
    }

    private final Set<UUID> TO_MEGA_EVOLVE_THIS_TURN = new HashSet<>();
    private final Set<UUID> HAS_MEGA_EVOLVED_THIS_BATTLE = new HashSet<>();

    private Config config;

    public Set<UUID> getToMegaEvolveThisTurn() {
        return TO_MEGA_EVOLVE_THIS_TURN;
    }

    public Set<UUID> getHasMegaEvolvedThisBattle() {
        return HAS_MEGA_EVOLVED_THIS_BATTLE;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        config = new Config(this);

        CommandRegistrationEvent.EVENT.register(CommandTree::register);
        LifecycleEvent.SERVER_STARTING.register(this::onServerStarting);
        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, this::onBattleStartedPre);

        PolymerResourcePackUtils.markAsRequired();
        PolymerResourcePackUtils.addModAssets(DataKeys.MOD_NAMESPACE);
        MegaStoneItem.initItem();
        Registry.register(Registries.ITEM, MegaStoneItem.ID, MegaStoneItem.ITEM);
    }

    private static void registerAspectProvider(String aspect) {
        SpeciesFeatures.INSTANCE.register(aspect, new FlagSpeciesFeatureProvider(List.of(aspect), false));
    }

    private void onServerStarting(MinecraftServer server) {
        registerAspectProvider(DataKeys.MEGA);
        registerAspectProvider(DataKeys.MEGA_X);
        registerAspectProvider(DataKeys.MEGA_Y);
        for (Species species : PokemonSpecies.INSTANCE.getSpecies()) {
            if (species.getForms().stream().anyMatch(form -> form.getName().equalsIgnoreCase(DataKeys.MEGA))) {
                species.getFeatures().add(DataKeys.MEGA);
            } else if (species.getForms().stream().anyMatch(form -> form.getName().equalsIgnoreCase(DataKeys.MEGA_X))) {
                species.getFeatures().add(DataKeys.MEGA_X);
                species.getFeatures().add(DataKeys.MEGA_Y);
            }
        }
        MegaStoneHeldItemManager.getInstance().loadMegaStoneIds();
        HeldItemProvider.INSTANCE.register(MegaStoneHeldItemManager.getInstance(), Priority.HIGH);
        config.reload();
    }

    private Unit onBattleStartedPre(BattleStartedPreEvent event) {
        MegaUtils.deMegaEvolveAll(event.getBattle());
        return Unit.INSTANCE;
    }

}
