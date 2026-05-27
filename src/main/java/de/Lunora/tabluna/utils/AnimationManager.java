package de.Lunora.tabluna.utils;

import de.Lunora.tabluna.Tabluna;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationManager {

    private final Map<String, Animation> animations = new HashMap<>();

    public void load() {
        animations.clear();
        File file = new File(Tabluna.getInstance().getDataFolder(), "animations.yml");
        if (!file.exists()) {
            Tabluna.getInstance().saveResource("animations.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section != null) {
                int interval = section.getInt("change-interval", 500);
                List<String> texts = section.getStringList("texts");
                animations.put(key, new Animation(texts, interval));
            }
        }
    }

    public String getMessage(String name) {
        Animation anim = animations.get(name);
        return anim != null ? anim.getCurrentText() : "";
    }

    private static class Animation {
        private final List<String> texts;
        private final int interval;
        private int currentIndex = 0;
        private long lastUpdate = System.currentTimeMillis();

        public Animation(List<String> texts, int interval) {
            this.texts = texts != null ? texts : new ArrayList<>();
            this.interval = interval;
        }

        public String getCurrentText() {
            if (texts.isEmpty()) return "";
            if (System.currentTimeMillis() - lastUpdate >= interval) {
                currentIndex = (currentIndex + 1) % texts.size();
                lastUpdate = System.currentTimeMillis();
            }
            return texts.get(currentIndex);
        }
    }
}
