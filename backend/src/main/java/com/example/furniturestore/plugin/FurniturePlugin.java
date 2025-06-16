package com.example.furniturestore.plugin;

import org.springframework.context.ApplicationContext;

public interface FurniturePlugin {
    String getName();
    void onLoad(ApplicationContext context);
}
