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
    }

    private static void deMegaEvolveAll(PokemonBattle battle) {
        battle.getActors().forEach(
            actor -> actor.getPokemonList().forEach(
                battlePokemon -> {
                    new FlagSpeciesFeature(DataKeys.MEGA, false).apply(battlePokemon.getOriginalPokemon());
                    new FlagSpeciesFeature(DataKeys.MEGA_X, false).apply(battlePokemon.getOriginalPokemon());
                    new FlagSpeciesFeature(DataKeys.MEGA_Y, false).apply(battlePokemon.getOriginalPokemon());
                }
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
