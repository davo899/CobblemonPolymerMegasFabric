package com.selfdot.cobblemonpolymermegasfabric;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeatureProvider;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatures;
import com.cobblemon.mod.common.api.pokemon.helditem.HeldItemProvider;
import com.cobblemon.mod.common.pokemon.Species;
import com.selfdot.cobblemonpolymermegasfabric.command.CommandTree;
import com.selfdot.cobblemonpolymermegasfabric.item.MegaStoneHeldItemManager;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CobblemonPolymerMegasFabric implements ModInitializer {

    private static CobblemonPolymerMegasFabric INSTANCE;
    public static CobblemonPolymerMegasFabric getInstance() {
        return INSTANCE;
    }

    private final Set<UUID> BATTLE_MEGA_EVOLVE = new HashSet<>();

    public Set<UUID> getBattleMegaEvolve() {
        return BATTLE_MEGA_EVOLVE;
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        CommandRegistrationEvent.EVENT.register(CommandTree::register);
        LifecycleEvent.SERVER_STARTING.register(this::onServerStarting);
        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, this::onBattleStartedPre);
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, this::onBattleVictory);
    }

    private void onServerStarting(MinecraftServer server) {
        SpeciesFeatures.INSTANCE.register(
            DataKeys.MEGA_SPECIES_FEATURE,
            new FlagSpeciesFeatureProvider(List.of(DataKeys.MEGA_SPECIES_FEATURE), false)
        );
        for (Species species : PokemonSpecies.INSTANCE.getSpecies()) {
            if (species.getForms().stream().anyMatch(
                form -> form.getName().equalsIgnoreCase(DataKeys.MEGA_SPECIES_FEATURE)
            )) {
                species.getFeatures().add(DataKeys.MEGA_SPECIES_FEATURE);
            }
        }
        MegaStoneHeldItemManager.getInstance().loadMegaStoneIds();
        HeldItemProvider.INSTANCE.register(MegaStoneHeldItemManager.getInstance(), Priority.HIGH);
    }

    private static void deMegaEvolveAll(PokemonBattle battle) {
        battle.getActors().forEach(
            actor -> actor.getPokemonList().forEach(
                battlePokemon -> new FlagSpeciesFeature(DataKeys.MEGA_SPECIES_FEATURE, false)
                    .apply(battlePokemon.getOriginalPokemon())
            )
        );
    }

    private Unit onBattleStartedPre(BattleStartedPreEvent event) {
        deMegaEvolveAll(event.getBattle());
        return Unit.INSTANCE;
    }

    private Unit onBattleVictory(BattleVictoryEvent event) {
        deMegaEvolveAll(event.getBattle());
        return Unit.INSTANCE;
    }

}
