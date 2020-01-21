package com.example.examplemod.interceptor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class SubTileInterceptor extends SubTileFunctional {

  public static final Map<SubTileInterceptor, Void> interceptors = new WeakHashMap<>();
  public static final String NAME = "interceptor";

  private static Random rand = new Random();

  private static final int MAX_MANA = 100;
  private static final int MANA_PER_TICK = 5;
  private static final int MANA_PER_OP = 15;
  private static final int COOLDOWN = 5; // 1 mob per .5 second max

  private static final String TAG_COOLDOWN = "cooldown";

  private byte cooldown = 0;

  @Override
  public void onUpdate() {
    super.onUpdate();

    if (getWorld().isRemote) {
      return;
    }

    if (!interceptors.containsKey(this)) {
      interceptors.put(this, null);
    }

    if (this.mana > MANA_PER_TICK) {
      this.mana -= MANA_PER_TICK;
      this.cooldown--;
    }

    if (ticksExisted % 50 == 0) {
      sync();
    }
  }

  @Override
  public void writeToPacketNBT(NBTTagCompound cmp) {
    super.writeToPacketNBT(cmp);

    cmp.setByte(TAG_COOLDOWN, cooldown);
  }

  @Override
  public void readFromPacketNBT(NBTTagCompound cmp) {
    super.readFromPacketNBT(cmp);

    cooldown = cmp.getByte(TAG_COOLDOWN);
  }

  public boolean interceptSpawn(EntityLivingBase entity) {

    if (this.cooldown > 0) {
      System.out.println("cooldown too high");
      return false;
    }

    BlockPos pos = getPos();
    entity.setPosition(
      pos.getX() + rand.nextInt(8) - 4,
      pos.getY() + 1,
      pos.getZ() + rand.nextInt(8) - 4
    );

    this.cooldown = COOLDOWN;
    this.mana -= MANA_PER_OP;

    return true;
  }

  public boolean canIntercept(EntityLivingBase entity) {
    if (!isAlive()) {
      System.out.println("flower is dead");
      return false;
    }

    if (entity.getEntityWorld() != getWorld()) {
      System.out.println("flower in another world");
      return false;
    }

    // use vinculotus
    if (entity instanceof EntityEnderman) {
      System.out.println("is enderman");
      return false;
    }

    if (this.mana < (MANA_PER_TICK + MANA_PER_OP)) {
      System.out.println("no mana");
      return false;
    }

    BlockPos tePos = getPos();
    if (
      distance(entity.posX, tePos.getX()) > getInterceptionRadius()
        || distance(entity.posZ, tePos.getZ()) > getInterceptionRadius()
    ) {
      System.out.println("too far:  R=" +  getInterceptionRadius() + ", x:{"+entity.posX+", "+tePos.getX()+"}="+distance(entity.posX, tePos.getX())+", z:{"+entity.posZ+", "+tePos.getZ()+"}="+distance(entity.posZ, tePos.getZ()));
      return false;
    }

    return true;
  }

  private int getInterceptionRadius() {
    float modifier = this.overgrowth ? 1.5f : 1f;

    return Math.round(FeatureInterceptor.spawnerRadius * modifier);
  }

  @Override
  public boolean isOvergrowthAffected() {
    return false;
  }

  @Override
  public int getMaxMana() {
    return MAX_MANA;
  }

  protected boolean isAlive() {
    return !supertile.isInvalid();
  }

  private static double distance(double x1, double x2) {
    return Math.abs(x1 - x2);
  }

  // TODO
  @Override
  public LexiconEntry getEntry() {
    // TODO
    return super.getEntry();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public RadiusDescriptor getRadius() {
    return new RadiusDescriptor.Square(toBlockPos(), getInterceptionRadius());
  }

  @Override
  public int getColor() {
    return 0x0A6051;
  }
}
