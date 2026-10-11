# The King In Yellow (Minecraft 1.20.1 / Forge 47.3.0) - Etapa 3 (revisao 1)

## Como compilar pelo GitHub
1. No repositorio, envie os arquivos desta pasta por cima dos antigos (src, build.gradle, gradle.properties...).
   A pasta `.github` ja existe no repositorio; nao precisa mexer nela.
2. Aba **Actions** -> a compilacao inicia sozinha. Ao terminar (marca verde), baixe o `.jar` em *Artifacts*.
3. Troque o `.jar` antigo na pasta `mods`.

## O que ha na etapa 3
- Armadura Amarela redesenhada (estilo netherite dourado, crista vermelha no elmo)
- Dimensao Carcosa (ceu e horizonte amarelos, dois sois, mundo plano)
- Cidade de Carcosa, Castelo do Rei e Lago de Hali (um de cada, em posicao fixa)
- Entrada: ler 5 Livros do Rei, escrever "Leve-me ate Carcosa" no chat e clicar em uma cama
- Em Carcosa, clicar em uma cama leva de volta ao ponto de renascimento

## Revisao 1 (correcoes e novidades)
- Spawn em Carcosa corrigido (o chunk de destino agora e gerado antes do teleporte)
- Estruturas de Carcosa assentadas na altura certa (antes ficavam 1 bloco afundadas)
- Lago de Hali: sem a haste de vidro; agora com agua azul capri brilhante
- Perto do Lago de Hali o ceu fica azul cobalto com estrelas
- Cidade: fonte nova de 3 niveis com agua, predios mobiliados, escadas, bibliotecas e baus
- Estruturas de Carcosa mais proximas (castelo e lago a 288 blocos da cidade)
- Para ver as posicoes novas, use um mundo novo ou apague a pasta dimensions/kingyellow/carcosa do mundo
