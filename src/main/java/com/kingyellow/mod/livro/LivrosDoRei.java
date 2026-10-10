package com.kingyellow.mod.livro;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Textos dos Livros do Rei de Amarelo (existencialismo pessimista). */
public final class LivrosDoRei {
    public static final int TOTAL = 7;

    private static final String[] TITULOS = {
            "Do Vazio Primeiro",
            "O Peso da Luz",
            "Cassilda Esquece",
            "Sob Dois Sóis",
            "A Máscara e a Face",
            "Lago de Hali",
            "O Último Ato"
    };

    private static final String[][] PAGINAS = {
            // 1
            {
                    "Antes de haver um nome para o que somos, havia o vazio, e o vazio não sentia falta de nada. Fomos nós, os que temos olhos, que inventamos a falta.",
                    "Você acorda todos os dias e chama isso de vida. Mas o sol que o acorda não sabe que você existe. Não há ninguém do outro lado da luz.",
                    "Os homens constroem casas, templos, nomes. Cada parede é um pedido de desculpas ao silêncio. O silêncio nunca aceitou nenhum.",
                    "Leia devagar. Cada verdade que você entende é uma porta que não volta a se fechar. Eu o aviso apenas porque a cortesia é a última coisa que resta a um rei."
            },
            // 2
            {
                    "Dizem que a luz consola. Mas a luz só mostra: mostra a rachadura na parede, o cansaço nos rostos, a distância entre o que você quis ser e o que é.",
                    "Em Carcosa há dois sóis, e nenhum deles aquece. Um mostra o que se perdeu. O outro, o que ainda será perdido.",
                    "O sofrimento não tem propósito oculto. Quem procura um sentido na dor procura uma carta em uma casa onde ninguém mora.",
                    "Não chore a luz que se apaga. Ela nunca foi sua. Apenas lhe emprestaram os olhos para vê-la ir embora."
            },
            // 3
            {
                    "Cassilda cantou às estrelas negras, e as estrelas não responderam. Cantou então para si mesma, e descobriu que também não havia ninguém ali.",
                    "Todo canto é uma tentativa de preencher uma sala vazia. Quanto mais alto se canta, mais o eco revela o tamanho da sala.",
                    "Ela esqueceu o nome do pai, o nome do amante, o próprio nome. Não foi uma maldição. Foi o único alívio que o mundo lhe ofereceu.",
                    "Esquecer é a misericórdia do universo. Lembrar é o castigo dos que ainda acreditam que algo foi importante."
            },
            // 4
            {
                    "Na cidade de Carcosa as sombras caem em duas direções. Quem tenta fugir de uma sombra corre para dentro da outra.",
                    "Assim é a escolha: duas sombras, nenhuma saída. Os que dizem ter escolhido apenas escolheram o lado em que preferem ser engolidos.",
                    "Liberdade é a palavra que os acorrentados inventaram para o som das próprias correntes. Eu a ouvi muitas vezes. Nunca soou como nada.",
                    "Não há lado certo a escolher. Há apenas o momento em que se para de fingir que isso importa."
            },
            // 5
            {
                    "Todos usam uma máscara. Os sábios sabem; os tolos juram que é o rosto. Mas por baixo da máscara não há um rosto escondido.",
                    "Arranque a máscara e encontrará outra, e outra, até que suas mãos se cansem. No fim, o que resta é o gesto de arrancar.",
                    "Eu uso a Máscara Pálida porque ela não finge ser outra coisa. Não há homem sob ela. Não há rei. Há só o hábito de ser chamado.",
                    "Talvez você também seja um nome que alguém repete no escuro. Talvez ninguém tenha sobrado para repeti-lo."
            },
            // 6
            {
                    "O lago de Hali não reflete o céu. Reflete a quem o olha, e mostra a hora em que essa pessoa deixará de importar.",
                    "Os que bebem de suas águas esquecem a sede. Esquecem, depois, o motivo de terem sede. Depois, o corpo que a sentia.",
                    "Há uma nascente ao pé de uma árvore antiga, e dela cai uma luz fina e fria. Dizem que é a memória do mundo se desfazendo.",
                    "Nada se perde no lago. Perder exigiria que algo tivesse valor. Tudo apenas se dissolve, em silêncio, sem testemunhas."
            },
            // 7
            {
                    "A peça tem dois atos. O primeiro apresenta os personagens, e é suportável. O segundo revela a plateia, e ninguém o termina.",
                    "Quem lê o segundo ato entende que sempre esteve em cena, e que as cortinas nunca existiram. Não há para onde sair.",
                    "Não existe plateia. Não existe aplauso. O fim da peça é o momento em que o ator percebe que esteve falando sozinho.",
                    "Se chegou até aqui, já é um dos meus. Não precisa jurar nada. Apenas peça que o levem a Carcosa, e deite-se."
            }
    };

    private LivrosDoRei() {
    }

    public static String titulo(int n) {
        return TITULOS[n - 1];
    }

    /** Monta um livro assinado "de mentira" (so para exibir a tela de leitura no cliente). */
    public static ItemStack criarLivroParaLeitura(int n) {
        ItemStack livro = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = livro.getOrCreateTag();
        tag.putString("title", titulo(n));
        tag.putString("author", "O Rei de Amarelo");
        tag.putBoolean("resolved", true);
        ListTag paginas = new ListTag();
        for (String p : PAGINAS[n - 1]) {
            paginas.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(p))));
        }
        tag.put("pages", paginas);
        return livro;
    }
}
