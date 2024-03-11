package com.selfdot.cobblemonpolymermegasfabric.item;

import com.mojang.logging.LogUtils;
import com.selfdot.cobblemonpolymermegasfabric.DataKeys;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

public class MegaStoneItem extends Item implements PolymerItem {

    public static MegaStoneItem ITEM;
    public static final Identifier ID = new Identifier(DataKeys.MOD_NAMESPACE, DataKeys.ITEM_ID_MEGA_STONE);

    public static void initItem() {
        ITEM = new MegaStoneItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE));
    }

    private final PolymerModelData modelData;

    public MegaStoneItem(Settings settings) {
        super(settings);
        modelData = PolymerResourcePackUtils.requestModel(Items.EMERALD, ID);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return modelData.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return modelData.value();
    }

}
