package com.kingyellow.mod.client;

import com.kingyellow.mod.livro.LivrosDoRei;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.world.item.ItemStack;

/** Codigo exclusivo do cliente: abre a tela de leitura de livro. */
public final class LivroCliente {
    private LivroCliente() {
    }

    public static void abrir(int n) {
        ItemStack livro = LivrosDoRei.criarLivroParaLeitura(n);
        Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(livro)));
    }
}
