package xyz.vec3d.game.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

import xyz.vec3d.game.PocketRogue;
import xyz.vec3d.game.entities.PocketRogueEntity;
import xyz.vec3d.game.entities.components.PositionComponent;
import xyz.vec3d.game.model.DefinitionLoader;
import xyz.vec3d.game.model.DefinitionLoader.Definition;
import xyz.vec3d.game.model.Item;
import xyz.vec3d.game.model.DefinitionProperty;
import xyz.vec3d.game.model.ItemStack;

/**
 * Created by darakelian on 6/30/2016.
 * Copyright vec3d.xyz 2016
 * All rights reserved
 *
 * Utils class provides static methods for various repeat functionality such as
 * math functions.
 */
public class Utils {


    /**
     * Gets the x coordinate to draw an object at when being centered in a container
     * in which point 0,0 is the lower left corner of the object.
     *
     * @param objectWidth The width of the object being centered.
     * @param containerWidth The width of the container that object is centered in.
     *
     * @return The x coordinate to draw the lower left corner of the object.
     *
     * @see Utils#getPosCenterX(float, float, float)
     */
    public static float getPosCenterX(float objectWidth, float containerWidth) {
        return getPosCenterX(objectWidth, containerWidth, 0);
    }

    /**
     * Gets the x coordinate to draw an object at based on a given object width
     * and container width such that it is drawn in the center of the container.
     *
     * @param objectWidth The width of the object being centered.
     * @param containerWidth The width of the container that object is centered in.
     * @param containerPos The position of the container (0 if container is the screen).
     *
     * @return The x coordinate to draw the lower left corner of the object.
     */
    public static float getPosCenterX(float objectWidth, float containerWidth, float containerPos) {
        return getPosCenter(objectWidth, containerWidth, containerPos, false);
    }

    /**
     * Gets the y coordinate to draw an object at when being centered in a container
     * in which point 0,0 is the lower left corner of the object.
     *
     * @param objectHeight The height of the object being centered.
     * @param containerHeight The wight of the container that object is centered in.
     *
     * @return The y coordinate to draw the lower left corner of the object.
     *
     * @see Utils#getPosCenterY(float, float, float)
     */
    public static float getPosCenterY(float objectHeight, float containerHeight) {
        return getPosCenterY(objectHeight, containerHeight, 0);
    }

    /**
     * Gets the y coordinate to draw an object at based on a given object height
     * and container height such that it is drawn in the center of the container.
     *
     * @param objectHeight The height of the object being centered.
     * @param containerHeight The height of the container that object is centered in.
     * @param containerPos The position of the container (0 if the container is the screen).
     *
     * @return The y coordinate to draw the lower left corner of the object.
     */
    public static float getPosCenterY(float objectHeight, float containerHeight, float containerPos) {
        return getPosCenter(objectHeight, containerHeight, containerPos, true);
    }

    /**
     * Performs the actual calculation to determine the coordinate that will draw
     * the object centered in the container. Private because this method should
     * not be called be anyone other than the wrapper methods in the Utils class.
     *
     * @param objectDimension Dimension of the object being centered (either width/height).
     * @param containerDimension Dimension of the container (either width/height).
     * @param containerPos Starting x or y coordinate of the container.
     *
     * @return The coordinate to draw the lower left corner of the object.
     */
    private static float getPosCenter(float objectDimension, float containerDimension,
                                      float containerPos, boolean negate) {
        return containerPos + ((containerDimension - objectDimension) / 2);
    }

    /**
     * Prints out an array's contents rather than the Java implementation.
     *
     * @param array Array to be printed out.
     */
    public static void printArray(Object[] array) {
        printArray(array, true);
    }

    /**
     * Prints out an array's contents in either list format or a one-liner.
     *
     * @param array Array to be printed out.
     * @param oneLine True if contents are to be placed on one line.
     */
    private static void printArray(Object[] array, boolean oneLine) {
        String stringToPrint = "";
        for (Object o : array) {
            stringToPrint += oneLine ? o.toString() + " " : o.toString() + "\n";
        }
        Logger.log(stringToPrint.trim());
    }

    /**
     * Attempts to center the camera on a given entity. This can be used for
     * anything that needs to focus on a specific entity.
     *
     * @param worldCamera The camera being centered. There should probably
     *                    only ever be one instance of a camera object in the
     *                    game.
     * @param entity The entity that the camera is being centered on.
     * @param mapWidth The width of the map in world units.
     * @param mapHeight The height of the map in world units.
     */
    public static void centerCamera(OrthographicCamera worldCamera, Entity entity, float mapWidth,
                                    float mapHeight) {
        PositionComponent position = entity.getComponent(PositionComponent.class);
        if (position == null) {
            return;
        }
        float camViewportHalfX = worldCamera.viewportWidth / 2;
        float camViewportHalfY = worldCamera.viewportHeight / 2;
        worldCamera.position.x = position.getPosition().x;
        worldCamera.position.y = position.getPosition().y;
        //Clamp camera first on x, then on y.
        worldCamera.position.x = MathUtils.clamp(worldCamera.position.x,
                camViewportHalfX, mapWidth - camViewportHalfX);
        worldCamera.position.y = MathUtils.clamp(worldCamera.position.y,
                camViewportHalfY, mapHeight - camViewportHalfY);
    }

    /**
     * Returns the appropriate Java object type for the JsonValue.
     *
     * @param value The JsonValue object.
     *
     * @return Returns the value as its intended type.
     */
    public static Object getJsonTypeValue(JsonValue value) {
        switch (value.type()) {
            case longValue:
                return value.asInt();
            case doubleValue:
                return value.asDouble();
            case booleanValue:
                return value.asBoolean();
            case object:
                return value;
            case array:
                return value.asIntArray();
            case stringValue:
            default:
                return value.asString();
        }
    }

    /**
     * Centers an Actor within the specified stage.
     *
     * @param actor Actor being centered.
     * @param stage The stage to center the actor in.
     */
    public static void centerActor(Actor actor, Stage stage) {
        float newX = getPosCenterX(actor.getWidth(), stage.getWidth());
        float newY = getPosCenterY(actor.getHeight(), stage.getHeight());
        actor.setPosition(newX, newY);
    }

    /**
     * Retrieves a TextureRegion corresponding to the provided ItemStack.
     *
     * @param itemStack The ItemStack for which a TextureRegion is being retrieved.
     *
     * @return The TextureRegion for the item stack.
     *
     * @see Utils#getItemTexture(Item)
     */
    public static TextureRegion getItemTexture(ItemStack itemStack) {
        return getItemTexture(itemStack.getItem());
    }

    public static TextureRegion getItemTexture(Item item) {
        return getItemTexture(item.getId());
    }

    public static TextureRegion getItemTexture(int itemId) {
        Definition definition = DefinitionLoader.getItemDefinition(itemId);
        int[] iconCoords = (int[]) definition.getProperty(DefinitionProperty.ICON);
        TextureRegion itemIcon = PocketRogue.getInstance().getSpriteSheet(itemId).
                getTextureFromSheet(iconCoords[0], iconCoords[1]);
        if (itemIcon == null) {
            Logger.log("Unable to find icon for item.", Utils.class);
            return null;
        }
        return itemIcon;
    }

    public static TextureRegion getEntityTexture(String entityName) {
        String name = entityName.toLowerCase() + ".png";
        Texture texture = PocketRogue.getAsset(name);
        if (texture == null) {
            return null;
        }
        return new TextureRegion(texture, texture.getWidth(), texture.getHeight());
    }

    /**
     * Determines if two specified entities are within range of each other. This
     * check is done using the bottom left corners of the hit boxes which are what
     * entity positions represent anyways. There is no reason to offset these
     * positions so that they represent the center as when the distance is calculated
     * the offsets end up cancelling themselves anyways. Makes use of Pythagoras'
     * Theorem (a^2 + b^2 <= c^2) as opposed to using the built in Java math
     * functions because those are technically slower.
     *
     * @param e1 The first entity in the comparison.
     * @param e2 The second entity in the comparison.
     * @param range The range we are checking for [0-range]
     *
     * @return True if the entities are within range.
     */
    public static boolean inRange(PocketRogueEntity e1, PocketRogueEntity e2, float range) {
        Vector2 e1pos = e1.getPosition();
        Vector2 e2pos = e2.getPosition();
        return (e2pos.x - e1pos.x) * (e2pos.x - e1pos.x) +
                (e2pos.y - e1pos.y) * (e2pos.y - e1pos.y)
                <= range * range;
    }

    /**
     * Iterates through a collection of entities and returns an array list
     * containing all the entities that were found within a certain range.
     *
     * @param entityPool The collection of entities to perform the checks on.
     * @param entityRelativeTo The entity that checks are being performed off of.
     * @param range The max distance in world units allowed between entities.
     *
     * @return A new collection of all entities within range of another entity.
     */
    public static ArrayList<PocketRogueEntity> getEntitiesWithinRange(
            ImmutableArray<Entity> entityPool, PocketRogueEntity entityRelativeTo,
            float range) {
        //Create a new collection
        ArrayList<PocketRogueEntity> entitiesInRange = new ArrayList<>();
        //Loop through the entities in the collection
        for (Entity entity : entityPool) {
            //Only look at entities that aren't the same as the provided one
            if (!entity.equals(entityRelativeTo)) {
                if (inRange((PocketRogueEntity) entity, entityRelativeTo, range)) {
                    entitiesInRange.add((PocketRogueEntity) entity);
                }
            }
        }
        return entitiesInRange;
    }

    public static boolean entitiesCollide(PocketRogueEntity e1, PocketRogueEntity e2) {
        Rectangle e1r = new Rectangle(e1.getPosition().x, e1.getPosition().y, e1.getSize(), e1.getSize());
        Rectangle e2r = new Rectangle(e2.getPosition().x, e2.getPosition().y, e1.getSize(), e1.getSize());
        //Logger.log(String.format("Checking if entity %s collided with entity %s", e1, e2));
        return e1r.overlaps(e2r);
    }

    public static String modifyDisplayValue(Label label, Object newString) {
        return modifyDisplayValue(label.getText().toString(), newString);
    }

    private static String modifyDisplayValue(String original, Object newString) {
        if (!original.contains(":")) {
            return original;
        }
        return original.substring(0, original.indexOf(":") + 2) + newString;
    }
}
