package com.games.ultimatetictactoe.app;

/**
 * Created by alemjc on 11/5/15.
 */
public interface Index {
     enum STATE {PLAYER1,PLAYER2,NONE,TIE};
     STATE getState();
     void setState(STATE state);
}
