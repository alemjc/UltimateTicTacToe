package com.games.ultimatetictactoe.app;

/**
 * Created by alemjc on 11/3/15.
 */
public class TableIndex implements Index {

    private int tableNumber;
    private STATE state;

    public TableIndex(int tableNumber, STATE state){
        this.tableNumber = tableNumber;
        this.state = state;
    }
    public STATE getState(){
        return state;
    }

    public int getTableNumber(){
        return tableNumber;
    }

    public void setTableNumber(int tableNumber){
        this.tableNumber = tableNumber;
    }

    public void setState(STATE state){
        this.state = state;
    }
}
