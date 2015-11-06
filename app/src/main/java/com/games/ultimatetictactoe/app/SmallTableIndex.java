package com.games.ultimatetictactoe.app;



/**
 * Created by alemjc on 11/5/15.
 */
public class SmallTableIndex implements TableIndex{

    private STATE state;

    public SmallTableIndex(STATE state){
        this.state = state;
    }

    @Override
    public void setState(STATE state) {

    }

    @Override
    public STATE getState() {
        return null;
    }
}
