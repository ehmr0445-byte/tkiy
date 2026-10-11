package com.kingyellow.mod.entity;

import com.kingyellow.mod.ModSons;
import com.kingyellow.mod.consciencia.ConscienciaData;
import com.kingyellow.mod.consciencia.Discipulo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * O Rei de Amarelo.
 *  - Pacifico, parado, nao pode ser atacado nem morto (so cai se for para fora do mundo).
 *  - Quem o olha diretamente ouve sussurros altos; na primeira vez o jogador se torna
 *    discipulo: Forca II permanente e a Armadura Amarela (ver Discipulo).
 */
public class ReiAmareloEntity extends PathfinderMob {
    /** Intervalo minimo (em ticks) entre sussurros para o mesmo jogador: 6 segundos. */
    private static final long INTERVALO_SUSSURROS = 120L;

    public ReiAmareloEntity(EntityType<? extends ReiAmareloEntity> tipo, Level nivel) {
        super(tipo, nivel);
        this.setPersistenceRequired();
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder criarAtributos() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1024.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 32.0F));
    }

    // ---------- imortalidade e imobilidade ----------

    @Override
    public boolean hurt(DamageSource fonte, float quantidade) {
        if (fonte.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return super.hurt(fonte, quantidade);
        }
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    /** Os golpes dos jogadores passam por ele. */
    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player jogador) {
        return false;
    }

    // ---------- o olhar ----------

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || this.tickCount % 5 != 0) {
            return;
        }
        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(48.0D))) {
            if (p instanceof ServerPlayer sp && !sp.isSpectator() && estaOlhando(sp)) {
                reagir(sp);
            }
        }
    }

    /** O jogador esta mirando (a ~16 graus) no capuz do Rei, com linha de visao livre. */
    private boolean estaOlhando(ServerPlayer jogador) {
        Vec3 olhos = jogador.getEyePosition();
        Vec3 alvo = new Vec3(this.getX(), this.getY() + 3.1D, this.getZ());
        if (olhos.distanceTo(alvo) > 48.0D) {
            return false;
        }
        Vec3 direcao = alvo.subtract(olhos).normalize();
        return jogador.getViewVector(1.0F).dot(direcao) > 0.96D && jogador.hasLineOfSight(this);
    }

    private void reagir(ServerPlayer jogador) {
        long agora = this.level().getGameTime();
        if (agora - ConscienciaData.ultimoSussurro(jogador) >= INTERVALO_SUSSURROS) {
            ConscienciaData.definirUltimoSussurro(jogador, agora);
            jogador.playNotifySound(ModSons.SUSSURROS.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }
        if (!ConscienciaData.discipulo(jogador)) {
            Discipulo.tornar(jogador);
        }
    }
}
