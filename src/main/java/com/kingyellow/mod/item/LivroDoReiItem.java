package com.kingyellow.mod.item;

import com.kingyellow.mod.client.LivroCliente;
import com.kingyellow.mod.consciencia.ConscienciaData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import javax.annotation.Nullable;

/** Livro escrito pelo Rei de Amarelo. Ler pela primeira vez aumenta a consciencia. */
public class LivroDoReiItem extends Item {
    private final int id;

    public LivroDoReiItem(int id, Item.Properties props) {
        super(props);
        this.id = id;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            final int n = this.id;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LivroCliente.abrir(n));
        } else if (player instanceof ServerPlayer sp) {
            ConscienciaData.lerLivro(sp, this.id);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Escritos do Rei de Amarelo").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
    }
}
