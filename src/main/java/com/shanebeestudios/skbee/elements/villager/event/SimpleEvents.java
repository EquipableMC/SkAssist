package com.shanebeestudios.skbee.elements.villager.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.eclipse.jdt.annotation.Nullable;

public class SimpleEvents {

    static {
        Skript.registerEvent("Trade Select", SimpleEvent.class, TradeSelectEvent.class, "trade select")
                .description("This event is called whenever a player clicks a new trade on the trades sidebar.",
                        "This event allows the user to get the index of the trade, letting them get the MerchantRecipe via the Merchant.",
                        "`event-number` = Used to get the index of the trade the player clicked on.",
                        "`event-merchantrecipe` = The merchant recipe of the trade that the player clicked on.")
                .examples("")
                .since("1.17.0");

        EventValues.registerEventValue(TradeSelectEvent.class, MerchantInventory.class, new Getter<>() {
            @Override
            public @Nullable MerchantInventory get(TradeSelectEvent event) {
                return event.getInventory();
            }
        }, 0);

        EventValues.registerEventValue(TradeSelectEvent.class, Number.class, new Getter<>() {
            @Override
            public @Nullable Number get(TradeSelectEvent event) {
                return event.getIndex();
            }
        }, 0);

        EventValues.registerEventValue(TradeSelectEvent.class, Merchant.class, new Getter<>() {
            @Override
            public @Nullable Merchant get(TradeSelectEvent event) {
                return event.getMerchant();
            }
        }, 0);

        EventValues.registerEventValue(TradeSelectEvent.class, MerchantRecipe.class, new Getter<>() {
            @Override
            public @Nullable MerchantRecipe get(TradeSelectEvent event) {
                return event.getInventory().getSelectedRecipe();
            }
        }, 0);

        EventValues.registerEventValue(TradeSelectEvent.class, Player.class, new Getter<>() {
            @Override
            public @Nullable Player get(TradeSelectEvent event) {
                HumanEntity trader = event.getMerchant().getTrader();
                if (trader instanceof Player player) {
                    return player;
                }
                return null;
            }
        }, 0);
    }
}
