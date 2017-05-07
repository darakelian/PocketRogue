package xyz.vec3d.game.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

import xyz.vec3d.game.messages.IMessageReceiver;
import xyz.vec3d.game.messages.IMessageSender;
import xyz.vec3d.game.messages.Message;
import xyz.vec3d.game.model.Inventory;
import xyz.vec3d.game.model.Item;
import xyz.vec3d.game.model.ItemStack;
import xyz.vec3d.game.model.combat.CombatSystem;
import xyz.vec3d.game.utils.Utils;

/**
 * Created by Daron on 8/16/2016.
 *
 * GUI overlay for the player {@link Inventory};
 */
class GuiInventory extends Gui {

    private Inventory inventory;
    private ScrollPane itemScrollPane;
    private Table itemTable;
    private List<ItemStackDisplay> itemStackDisplays = new ArrayList<>();
    private List<IMessageReceiver> messageReceivers = new ArrayList<>();

    private Label meleeDamage, magicDamage, rangeDamage, attackSpeed, meleeDef,
            magicDef, rangeDef;

    public GuiInventory() {
        super();
    }

    @Override
    public void setup() {
        this.inventory = (Inventory) getParameters()[0];
        Skin skin = (Skin) getParameters()[1];
        CombatSystem combatSystem = (CombatSystem) getParameters()[2];
        this.messageReceivers.add(combatSystem);

        //Create and set up the window.
        Window window = new Window("Inventory", skin);
        window.setSize(400, 300);
        window.setResizable(false);
        window.setMovable(false);
        Utils.centerActor(window, getStage());

        //Set up root table.
        Table componentTable = new Table(skin);
        componentTable.pad(4);
        componentTable.padTop(22);
        componentTable.setFillParent(true);

        //Set up table for the ItemStackDisplays and the scroll pane.
        itemTable = new Table(skin);
        itemScrollPane = new ScrollPane(itemTable, skin);

        //Set up table for item stats.
        Table itemInfoTable = new Table(skin);
        meleeDamage = new Label("Melee Damage: ", skin);
        magicDamage = new Label("Magic Damage: ", skin);
        rangeDamage = new Label("Range Damage: ", skin);
        attackSpeed = new Label("Attack Speed: ", skin);
        meleeDef = new Label("Melee Defense: ", skin);
        magicDef = new Label("Magic Defense: ", skin);
        rangeDef = new Label("Range Defense: ", skin);
        Button equipButton = new TextButton("Equip", skin);
        equipButton.addCaptureListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ItemStackDisplay itemStackDisplay = getSelectedDisplay();
                if (itemStackDisplay == null) {
                    return;
                }

                ItemStack itemToEquip = itemStackDisplay.getItemStack();
                if (inventory.equipItem(itemToEquip)) {
                    Message itemEquippedMessage = new Message(Message.MessageType.ITEM_EQUIPPED);
                    notifyMessageReceivers(itemEquippedMessage);
                    refreshInventoryTable();
                }
            }
        });

        itemInfoTable.add(meleeDamage).pad(4).fillX().expandX().height(20).row();
        itemInfoTable.add(magicDamage).pad(4).fillX().expandX().height(20).row();
        itemInfoTable.add(rangeDamage).pad(4).fillX().expandX().height(20).row();
        itemInfoTable.add(attackSpeed).pad(4).fillX().expandX().height(20).row();
        itemInfoTable.add(meleeDef).pad(4).fillX().expandX().height(20).row();
        itemInfoTable.add(magicDef).pad(4).fillX().expandX().height(20).row();
        itemInfoTable.add(rangeDef).pad(4).fillX().expandX().height(20).row();
        itemInfoTable.add(equipButton).pad(4).fillX().expandX().height(40);
        itemInfoTable.add().fill().expand();
        //itemInfoTable.add().expand().fill();


        //Add item display and stat display tables to root table.
        componentTable.add(itemScrollPane).expandY().fillY().width(250);
        componentTable.add(itemInfoTable).expandX().fill();

        //Add root table to window and add the item displays.
        window.addActor(componentTable);
        refreshInventoryTable();

        //Add stuff to the stage and set scroll focus to item scroll pane.
        getStage().addActor(window);
        getStage().setScrollFocus(itemScrollPane);
    }

    private void refreshInventoryTable() {
        ItemStack[] items = inventory.getItemsAsArray();
        itemTable.clear();
        for (int i = 0; i < items.length; i++) {
            ItemStackDisplay display;
            if (itemStackDisplays.size() > i) {
                display = itemStackDisplays.get(i);
            } else {
                display = new ItemStackDisplay(items[i]);
                display.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //Update bonuses.
                        ItemStackDisplay displayFired = (ItemStackDisplay) event.getListenerActor();
                        int[] bonuses = displayFired.getItemStack().getItem().getBonuses();
                        meleeDamage.setText(Utils.modifyDisplayValue(meleeDamage, bonuses[Item.ATTACK]));
                        magicDamage.setText(Utils.modifyDisplayValue(magicDamage, bonuses[Item.MAGIC]));
                        rangeDamage.setText(Utils.modifyDisplayValue(rangeDamage, bonuses[Item.RANGE]));
                        attackSpeed.setText(Utils.modifyDisplayValue(attackSpeed, bonuses[Item.ATTACK_SPEED]));
                        meleeDef.setText(Utils.modifyDisplayValue(meleeDef, bonuses[Item.MELEE_DEFENSE]));
                        magicDef.setText(Utils.modifyDisplayValue(magicDef, bonuses[Item.MAGIC_DEFENSE]));
                        rangeDef.setText(Utils.modifyDisplayValue(rangeDef, bonuses[Item.RANGE_DEFENSE]));
                        for (ItemStackDisplay stackDisplay : itemStackDisplays) {
                            stackDisplay.deselect();
                        }
                        displayFired.select();
                    }
                });
                itemStackDisplays.add(display);
            }
            itemTable.add(display).expandX().fillX().left().pad(4).row();
        }
        itemTable.add().expand().fill();
        itemScrollPane.validate();
    }

    private ItemStackDisplay getSelectedDisplay() {
        for (ItemStackDisplay display : itemStackDisplays) {
            if (display.isSelected()) {
                return display;
            }
        }
        return null;
    }

    @Override
    public void onMessageReceived(Message message) {
        switch (message.getMessageType()) {
            case PLAYER_INVENTORY_CHANGED:
                //Refresh inventory.
                this.inventory = (Inventory) message.getPayload()[0];
                refreshInventoryTable();
                break;
        }
    }

    @Override
    public void notifyMessageReceivers(Message message) {
        for (IMessageReceiver messageReceiver : messageReceivers) {
            messageReceiver.onMessageReceived(message);
        }
    }
}
