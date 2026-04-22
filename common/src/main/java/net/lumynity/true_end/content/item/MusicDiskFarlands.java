
package net.lumynity.true_end.content.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.lumynity.true_end.registries.TESounds;

import java.util.List;

public class MusicDiskFarlands extends RecordItem {
    public MusicDiskFarlands() {
        super(8, TESounds.MUSIC_FARLANDS.get(), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 2540);
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
    }
}
