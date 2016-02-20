package com.games.ultimatetictactoe.app.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class GameContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<GameItem> ITEMS = new ArrayList<GameItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, GameItem> ITEM_MAP = new HashMap<String, GameItem>();

    /*static {
        // Add 3 sample items.
        addItem(new GameItem("1", "Item 1"));
        addItem(new GameItem("2", "Item 2"));
        addItem(new GameItem("3", "Item 3"));
    }*/

    public static void addItem(GameItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.userName, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class GameItem {
        public String gameName;
        public String userName;
        public int state;

        public GameItem(String userName,String gameName, int state) {
            this.userName = userName;
            this.gameName = gameName;
            this.state = state;
        }

        @Override
        public String toString() {
            return userName;
        }

        @Override
        public boolean equals(Object o) {

            if(! (o instanceof GameItem)){
                return false;
            }
            else{
                GameItem t = (GameItem) o;
                return this.userName.equals(t.userName);
            }

        }
    }
}
