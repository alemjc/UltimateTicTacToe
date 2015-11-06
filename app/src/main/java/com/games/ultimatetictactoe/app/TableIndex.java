package com.games.ultimatetictactoe.app;

/**
 * Created by alemjc on 11/5/15.
 */
public interface TableIndex {
     enum STATE {PLAYER1,PLAYER2,NONE};
     STATE getState();
     void setState(STATE state);
}
