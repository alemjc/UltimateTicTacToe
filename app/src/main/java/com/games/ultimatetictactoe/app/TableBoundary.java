package com.games.ultimatetictactoe.app;

/**
 * Created by alemjc on 11/3/15.
 */
public class TableBoundary {

    private int x;
    private int y;
    private int endX;
    private int endY;
    private int state;
    protected static final int TIED = -1;
    protected static final int PLAYER1 = 1;
    protected static final int PLAYER2 = 2;
    protected static final int NOTPLAYED = 0;

    public TableBoundary(int x, int y, int state){
        this.x = x;
        this.y = y;
        endX = x+2;
        endY = y+2;
        this.state = state;
    }
    public int getState(){
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

    public void setState(int state){
        this.state = state;
    }
}
