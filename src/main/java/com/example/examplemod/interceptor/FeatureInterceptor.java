package com.example.examplemod.interceptor;

import com.example.examplemod.ExampleMod;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.lexicon.BasicLexiconEntry;
import vazkii.botania.common.lexicon.page.PagePetalRecipe;
import vazkii.botania.common.lexicon.page.PageText;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class FeatureInterceptor {

  public static boolean enabled = true;
  public static int spawnerRadius = 64;

  @GameRegistry.ItemStackHolder(value = "botania:manaResource", meta = 5)
  public static ItemStack botaniaItemGaiaSpirit;

  public static BasicLexiconEntry interceptorLexiconEntry;

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

    RecipePetals interceptorRecipe = BotaniaAPI.registerPetalRecipe(
      ItemBlockSpecialFlower.ofType(SubTileInterceptor.NAME),
      "petalPurple",
      "petalPurple",
      "petalBlue",
      "elvenDragonstone",
      "bEnderAirBottle",
      "runeSlothB",
      "runeWrathB",
      "runeEnvyB"
    );

    interceptorLexiconEntry = new BasicLexiconEntry(SubTileInterceptor.NAME, BotaniaAPI.categoryFunctionalFlowers);
    interceptorLexiconEntry.setLexiconPages(
      new PageText("0"),
      new PageText("1"),
      new PagePetalRecipe<>("2", interceptorRecipe)
    );
    interceptorLexiconEntry.setKnowledgeType(BotaniaAPI.elvenKnowledge);
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
    }
  }
}
