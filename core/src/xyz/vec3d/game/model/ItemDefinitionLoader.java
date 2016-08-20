package xyz.vec3d.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

import xyz.vec3d.game.utils.Utils;

/**
 * Created by Daron on 8/19/2016.
 *
 * Loads item definitions (name and texture information).
 */
public class ItemDefinitionLoader {

    public static Map<Integer, ItemDefinition> itemDefinitions;

    public ItemDefinitionLoader() {
        itemDefinitions = new HashMap<>();
    }

    public void loadItemDefinitions() {
        JsonReader jsonReader = new JsonReader();
        JsonValue values = jsonReader.parse(Gdx.files.internal("item_definitions.json"));
        for (int i = 0; i < values.child().size; i++) {
            JsonValue child = values.child().get(i);
            ItemDefinition definition = new ItemDefinition();
            for (int childIndex = 0; childIndex < child.size; childIndex++) {
                JsonValue value = child.get(childIndex);
                String key = value.name;
                definition.putProperty(key, Utils.getJsonTypeValue(value));
            }
            itemDefinitions.put(i, definition);
        }
    }

    public class ItemDefinition {

        private Map<ItemProperty, Object> definitions;

        public ItemDefinition() {
            definitions = new HashMap<>();
        }

        public Object getProperty(ItemProperty property) {
            return definitions.get(property);
        }

        public void putProperty(String propertyName, Object property) {
            definitions.put(ItemProperty.valueOf(propertyName), property);
        }

    }
}