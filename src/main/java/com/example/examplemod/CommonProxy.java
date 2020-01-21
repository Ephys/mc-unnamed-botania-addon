package com.example.examplemod;

import com.example.examplemod.interceptor.FeatureInterceptor;

public class CommonProxy {
  public void preInit() {
  }

  public void init() {
    FeatureInterceptor.init();
  }
}
