package com.example.examplemod.interceptor;

import com.example.examplemod.ExampleMod;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.api.BotaniaAPI;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class FeatureInterceptor {

  public static boolean enabled = true;
  public static int spawnerRadius = 64;

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    if (!enabled) {
      return;
    }

    BotaniaAPI.registerSubTile(SubTileInterceptor.NAME, SubTileInterceptor.class);
    BotaniaAPI.addSubTileToCreativeMenu(SubTileInterceptor.NAME);

    MinecraftForge.EVENT_BUS.register(new FeatureInterceptor());
  }

  public static void init() {
    if (!enabled) {
      return;
    }

    /*
 recipePetroPetunia = BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType(SubTilePetroPetunia.NAME),
                "redstoneRoot", "runeWaterB", "runeFireB", "petalOrange", "petalBlack", "petalBrown",
                "elvenDragonstone");
 */

/*
        petroPetunia = new BasicLexiconEntry(SubTilePetroPetunia.NAME, BotaniaAPI.categoryGenerationFlowers);
        petroPetunia.setLexiconPages(new PageText("0"), new PagePetalRecipe<>("1", BotaniaRecipes.recipePetroPetunia));
        petroPetunia.setKnowledgeType(BotaniaAPI.elvenKnowledge);
 */
  }


  @SubscribeEvent
  public void onMobSpawn(LivingSpawnEvent.SpecialSpawn spawnEvent) {
    if (spawnEvent.getSpawner() != null) {
      return;
    }

    if (spawnEvent.getResult() != Event.Result.DEFAULT || spawnEvent.isCanceled()) {
      return;
    }

    EntityLivingBase entity = spawnEvent.getEntityLiving();
    if (!entity.isNonBoss()) {
      return;
    }

    if (!entity.isCreatureType(EnumCreatureType.MONSTER, false)) {
      return;
    }

    boolean hasInterceptorInRange = false;
    for (SubTileInterceptor interceptor : SubTileInterceptor.interceptors.keySet()) {

      if (!interceptor.canIntercept(entity)) {
        continue;
      }

      hasInterceptorInRange = true;

      if (interceptor.interceptSpawn(entity)) {
        return;
      }
    }

    // interception failed due to cooldown, cancel spawn
    if (hasInterceptorInRange) {
      spawnEvent.setCanceled(true);
    } else {
      System.out.println("no interceptor to prevent spawn");
    }
  }
}
