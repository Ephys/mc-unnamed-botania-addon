package com.example.examplemod.interceptor;

import com.example.examplemod.ExampleMod;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPIClient;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Side.CLIENT)
public class FeatureInterceptorClient {

  public static final ModelResourceLocation interceptorDefaultNormal = new ModelResourceLocation(new ResourceLocation(ExampleMod.MODID, SubTileInterceptor.NAME), "normal");
  public static final ModelResourceLocation interceptorDefaultInventory = new ModelResourceLocation(new ResourceLocation(ExampleMod.MODID, SubTileInterceptor.NAME), "inventory");

//  public static final ModelResourceLocation interceptorUpgradeNormal = new ModelResourceLocation(new ResourceLocation(ExampleMod.MODID, SubTileInterceptor.NAME + "_upgrade"), "normal");
//  public static final ModelResourceLocation interceptorUpgradeInventory = new ModelResourceLocation(new ResourceLocation(ExampleMod.MODID, SubTileInterceptor.NAME+ "_upgrade"), "inventory");

  @SubscribeEvent
  public static void preInit(ModelRegistryEvent event) {
    BotaniaAPIClient.registerSubtileModel(SubTileInterceptor.NAME, interceptorDefaultNormal, interceptorDefaultInventory);
  }
}
