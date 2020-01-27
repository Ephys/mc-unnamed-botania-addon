package com.example.examplemod.interceptor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;

import java.util.List;
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
  private static final String TAG_UPGRADED = "upg";

  private byte cooldown = 0;
  private boolean isUpgraded = false;

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
    cmp.setBoolean(TAG_UPGRADED, isUpgraded);
  }

  @Override
  public void readFromPacketNBT(NBTTagCompound cmp) {
    super.readFromPacketNBT(cmp);

    cooldown = cmp.getByte(TAG_COOLDOWN);
    isUpgraded = cmp.getBoolean(TAG_UPGRADED);
  }

  @Override
  public List<ItemStack> getDrops(List<ItemStack> list) {

    if (isUpgraded) {
      ItemStack newItem = FeatureInterceptor.botaniaItemGaiaSpirit.copy();
      newItem.setCount(1);
      list.add(newItem);
    }

    return super.getDrops(list);
  }

  public boolean interceptSpawn(EntityLivingBase entity) {

    if (this.cooldown > 0) {
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
      return false;
    }

    if (entity.getEntityWorld() != getWorld()) {
      return false;
    }

    // use vinculotus
    if (entity instanceof EntityEnderman) {
      return false;
    }

    if (this.mana < (MANA_PER_TICK + MANA_PER_OP)) {
      return false;
    }

    BlockPos tePos = getPos();
    if (
      distance(entity.posX, tePos.getX()) > getInterceptionRadius()
        || distance(entity.posZ, tePos.getZ()) > getInterceptionRadius()
    ) {
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

  @Override
  public LexiconEntry getEntry() {
    // TODO
    return FeatureInterceptor.interceptorLexiconEntry;
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

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    ItemStack heldItem = player.getHeldItem(hand);

    if (this.isUpgraded) {
      return false;
    }

    if (!heldItem.isItemEqual(FeatureInterceptor.botaniaItemGaiaSpirit)) {
      return false;
    }

    if (world.isRemote) {
      return true;
    }

    if (!player.isCreative()) {
      heldItem.shrink(1);
    }

    this.isUpgraded = true;
    this.sync();

    return true;
  }
}
