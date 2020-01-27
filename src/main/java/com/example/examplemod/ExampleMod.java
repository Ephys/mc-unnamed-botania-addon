package com.example.examplemod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
  modid = ExampleMod.MODID,
  name = ExampleMod.NAME,
  version = ExampleMod.VERSION,
  dependencies = "required-after:botania"
)
public class ExampleMod {
  public static final String MODID = "examplemod";
  public static final String NAME = "Example Mod";
  public static final String VERSION = "1.0";

  private static Logger logger;

  @SidedProxy(clientSide = "com.example.examplemod.ClientProxy", serverSide = "com.example.examplemod.CommonProxy")
  private static CommonProxy proxy;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();

    proxy.preInit();
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    proxy.init();
  }
}

// TODO:
//  - fishing pole
//  - end ore gen