package com.games.ultimatetictactoe.app;

/**
 * Created by alemjc on 11/3/15.
 */
public class BigTableIndex implements TableIndex{

    private int x;
    private int y;
    private int endX;
    private int endY;
    private STATE state;

    public BigTableIndex(int x, int y, STATE state){
        this.x = x;
        this.y = y;
        endX = x+2;
        endY = y+2;
        this.state = state;
    }
    public STATE getState(){
        return state;
    }

    public int getStartingX(){
        return x;
    }
    public int getStartingY(){
        return y;
    }

    public int getEndX(){
        return endX;
    }

    public int getEndY(){
        return endY;
    }

    public void setState(STATE state){
        this.state = state;
    }
}
