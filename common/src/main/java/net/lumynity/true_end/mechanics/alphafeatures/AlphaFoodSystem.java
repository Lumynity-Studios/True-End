package net.lumynity.true_end.mechanics.alphafeatures;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.Map;

import static net.lumynity.true_end.registries.TEDimKeys.BTD;

public class AlphaFoodSystem {
    private static final Map<Item, Float> FOOD_HEALTH = Map.ofEntries(
        Map.entry(Items.PORKCHOP, 1.5F),
        Map.entry(Items.COOKED_PORKCHOP, 4.0F),
        Map.entry(Items.BEEF, 3.0F),
        Map.entry(Items.COOKED_BEEF, 8.0F),
        Map.entry(Items.MUTTON, 2.0F),
        Map.entry(Items.COOKED_MUTTON, 6.0F),
        Map.entry(Items.CHICKEN, 2.0F),
        Map.entry(Items.COOKED_CHICKEN, 6.0F),
        Map.entry(Items.BREAD, 2.5F),
        Map.entry(Items.APPLE, 2.0F),
        Map.entry(Items.GOLDEN_APPLE, 10.0F)
    );

    public static CompoundEventResult<ItemStack> onRightClickItem(Player player, InteractionHand hand) {
        if (player.level().dimension() != BTD) return CompoundEventResult.pass();
        if (hand != InteractionHand.MAIN_HAND) return CompoundEventResult.interruptTrue(player.getItemInHand(hand));
        ItemStack stack = player.getItemInHand(hand);

        Float healAmount = FOOD_HEALTH.get(stack.getItem());
        if (healAmount != null) {
            if (!healthCheck(player)) {
                stack.shrink(1);
                player.getInventory().setChanged();
                player.setHealth(Math.min(player.getHealth() + healAmount, player.getMaxHealth()));
                playEatSound(player.level(), player.getX(), player.getY(), player.getZ());
            }
            return CompoundEventResult.interruptTrue(player.getItemInHand(hand));
        }

        if (stack.getItem().isEdible()) {
            return CompoundEventResult.interruptTrue(stack);
        }

        return CompoundEventResult.pass();
    }

    public static EventResult onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        if (hand != InteractionHand.MAIN_HAND) return EventResult.pass();
        if (player.level().dimension() != BTD) return EventResult.pass();
        ItemStack stack = player.getItemInHand(hand);

        if (FOOD_HEALTH.containsKey(stack.getItem())) {
            return EventResult.pass();
        }
        if (stack.getItem().isEdible()) {
            return EventResult.interruptTrue();
        }

        return EventResult.pass();
    }

    private static void playEatSound(LevelAccessor world, double x, double y, double z) {
        float pitch = (float) (0.8 + Math.random() * 0.4);

        if (world instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(x, y, z),
                    SoundEvents.GENERIC_EAT,
                    SoundSource.NEUTRAL, 1.0f, pitch);
            } else {
                level.playLocalSound(x, y, z,
                    SoundEvents.GENERIC_EAT,
                    SoundSource.NEUTRAL, 1.0f, pitch, false);
            }
        }
    }

    private static boolean healthCheck(Player player) {
        return player.getHealth() >= player.getMaxHealth();
    }
}