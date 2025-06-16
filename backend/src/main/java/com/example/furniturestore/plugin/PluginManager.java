package com.example.furniturestore.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class PluginManager {
    private final ApplicationContext context;

    public PluginManager(ApplicationContext context) {
        this.context = context;
        loadPlugins();
    }

    public void loadPlugins() {
        File dir = new File("plugins");
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] jars = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (jars == null) return;
        for (File jar : jars) {
            try {
                URLClassLoader cl = new URLClassLoader(new URL[] { jar.toURI().toURL() }, this.getClass().getClassLoader());
                ServiceLoader<FurniturePlugin> loader = ServiceLoader.load(FurniturePlugin.class, cl);
                for (FurniturePlugin plugin : loader) {
                    plugin.onLoad(context);
                    System.out.println("Loaded plugin: " + plugin.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
