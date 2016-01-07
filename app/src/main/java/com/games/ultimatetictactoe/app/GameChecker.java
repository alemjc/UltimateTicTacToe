package com.games.ultimatetictactoe.app;

import java.util.ArrayList;

/**
 * Created by jonchen on 11/7/2015.
 */
public class GameChecker {
    public enum STATE {PLAYER,OPPONENT,NONE,TIE}

    private GameChecker(){}

    public static STATE checkBoard(int[][] tableIndex, String coordinates,int player,int opponent){
        //TODO decide on how to implement this method
        int x=Character.getNumericValue(coordinates.charAt(0));
        int y=Character.getNumericValue(coordinates.charAt(1));
        //ArrayList<Boolean> tieArr = new ArrayList<Boolean>();

        //TODO fix this
        //Should this check be done here or else where
        /*if(tableIndex[x][y].getState()!= Index.STATE.NONE){
            if((x==y) || ((x==0 || x==2) && (y==0 || y==2))){
                Index.STATE state = checkRow(x, y, tableIndex, player, enemy);
                if(state==player){
                    return player;
                }else if(state== Index.STATE.TIE){
                    tieArr.add(true);
                }else{
                    tieArr.add(false);
                }

            }else{

            }
        }*/
        return STATE.NONE;
    }

    public static STATE checkRow(int x, int y, int[][] tableIndex, int player, int enemy){
        int tmpY1,tmpY2;
        if(y!=1){
            tmpY1=y+1%3;
            tmpY2=y+2%3;
        }else{
            tmpY1=y+1;
            tmpY2=y-1;
        }
        if(tableIndex[x][tmpY1]==player && tableIndex[x][tmpY2]==player){
            return STATE.PLAYER;
        }else if(tableIndex[x][tmpY1]==enemy || tableIndex[x][tmpY2]==enemy){
            return STATE.TIE;
        }else{
            return STATE.NONE;
        }
    }

    public static STATE checkColumn(int x, int y, int[][] tableIndex, int player, int enemy){
        int tmpX1,tmpX2;
        if(x!=1){
            tmpX1=x+1%3;
            tmpX2=x+2%3;
        }else{
            tmpX1=x+1;
            tmpX2=x-1;
        }
        if(tableIndex[tmpX1][y]==player && tableIndex[tmpX2][y]==player){
            return STATE.PLAYER;
        }else if(tableIndex[tmpX1][y]==enemy || tableIndex[tmpX2][y]==enemy){
            return STATE.TIE;
        }else{
            return STATE.NONE;
        }
    }

    public static STATE checkDiagonal(int x, int y, int[][] tableIndex, int player, int enemy){
        /*int tmpX1,tmpX2, tmpY1, tmpY2;
        if(x!=y){
            tmpX1=x+1%3;
            tmpY1=y+1%3;
            tmpX2=x+2%3;
            tmpY2=x+2%3;
        }else{
            tmpX1=x+1;
            tmpY1=y+1;
            tmpX2=x-1;
            tmpY2=y-1;
        }


        if(tableIndex[tmpX1][tmpY1]==player && tableIndex[tmpX2][tmpY2]==player){
            return STATE.PLAYER;
        }else if(tableIndex[tmpX1][tmpY1]==enemy || tableIndex[tmpX2][tmpY2]==enemy){
            return STATE.TIE;
        }else{
            return STATE.NONE;
        }*/

        if(x == 0 && y == 0){
            int tmpX1,tmpX2, tmpY1, tmpY2;
            tmpX1 = x+1;
            tmpY1 = y+1;
            tmpX2 = x+2;
            tmpY2 = y+2;

            if(tableIndex[tmpX1][tmpY1]==player && tableIndex[tmpX2][tmpY2]==player){
                return STATE.PLAYER;
            }else if(tableIndex[tmpX1][tmpY1]==enemy || tableIndex[tmpX2][tmpY2]==enemy){
                return STATE.TIE;
            }else {
                return STATE.NONE;
            }
        }
        else if(x == 0 && y == 2){
            int tmpX1,tmpX2, tmpY1, tmpY2;
            tmpX1 = x+1;
            tmpY1 = y-1;
            tmpX2 = x+2;
            tmpY2 = y-2;

            if(tableIndex[tmpX1][tmpY1]==player && tableIndex[tmpX2][tmpY2]==player){
                return STATE.PLAYER;
            }else if(tableIndex[tmpX1][tmpY1]==enemy || tableIndex[tmpX2][tmpY2]==enemy){
                return STATE.TIE;
            }else {
                return STATE.NONE;
            }

        }

        else if (x == 2 && y == 0){
            int tmpX1,tmpX2, tmpY1, tmpY2;
            tmpX1 = x-1;
            tmpY1 = y+1;
            tmpX2 = x-2;
            tmpY2 = y+2;

            if(tableIndex[tmpX1][tmpY1]==player && tableIndex[tmpX2][tmpY2]==player){
                return STATE.PLAYER;
            }else if(tableIndex[tmpX1][tmpY1]==enemy || tableIndex[tmpX2][tmpY2]==enemy){
                return STATE.TIE;
            }else {
                return STATE.NONE;
            }
        }
        else if(x == 2 && y == 2){
            int tmpX1,tmpX2, tmpY1, tmpY2;
            tmpX1 = x-1;
            tmpY1 = y-1;
            tmpX2 = x-2;
            tmpY2 = y-2;

            if(tableIndex[tmpX1][tmpY1]==player && tableIndex[tmpX2][tmpY2]==player){
                return STATE.PLAYER;
            }else if(tableIndex[tmpX1][tmpY1]==enemy || tableIndex[tmpX2][tmpY2]==enemy){
                return STATE.TIE;
            }else {
                return STATE.NONE;
            }

        }

        else if(x == 1 && y ==1){
            int tmpX1,tmpX2,tmpY1,tmpY2,tmpX3,tmpX4,tmpY3,tmpY4;
            tmpX1 = x-1;
            tmpY1 = y-1;
            tmpX2 = x+1;
            tmpY2 = y+1;
            tmpX3 = x-1;
            tmpY3 = y+1;
            tmpX4 = x+1;
            tmpY4 = y-1;

            if((tableIndex[tmpX1][tmpY1]==player && tableIndex[tmpX2][tmpY2]==player) ||
                    (tableIndex[tmpX3][tmpY3]==player && tableIndex[tmpX4][tmpY4]==player)){
                return STATE.PLAYER;
            }else if((tableIndex[tmpX1][tmpY1]==enemy || tableIndex[tmpX2][tmpY2]==enemy) &&
                    (tableIndex[tmpX3][tmpY3]==enemy || tableIndex[tmpX4][tmpY4]==enemy)){
                return STATE.TIE;
            }else {
                return STATE.NONE;
            }

        }

        else{
            return STATE.NONE;
        }
    }

    /*private static STATE checkRow(int x,int y,int[][] tableIndex){
        Index.STATE currState=tableIndex[x][y].getState();
        if(currState==Index.STATE.PLAYER1 || currState==Index.STATE.PLAYER2){
            return checkRow(x, y, tableIndex, currState, (currState== Index.STATE.PLAYER1)? Index.STATE.PLAYER2: Index.STATE.PLAYER1);
        }else if(y<3){
            //increment y and run recusively
            return checkRow(x, y+1, tableIndex);
        }else{
            return Index.STATE.NONE;
        }
    }

    private static Index.STATE checkColumn(int x,int y,TableIndex[][] tableIndex){
        Index.STATE currState=tableIndex[x][y].getState();
        if(currState==Index.STATE.PLAYER1 || currState==Index.STATE.PLAYER2){
            return checkColumn(x, y, tableIndex, currState, (currState == Index.STATE.PLAYER1) ? Index.STATE.PLAYER2 : Index.STATE.PLAYER1);
        }else if(x<3){
            //increment x and run recusively
            return checkColumn(x + 1, y, tableIndex);
        }else{
            return Index.STATE.NONE;
        }
    }

    private static Index.STATE checkDiagonal(int x,int y,TableIndex[][] tableIndex){
        Index.STATE currState=tableIndex[x][y].getState();
        if(currState==Index.STATE.PLAYER1 || currState==Index.STATE.PLAYER2){
            return checkDiagonal(x, y, tableIndex, currState, (currState == Index.STATE.PLAYER1) ? Index.STATE.PLAYER2 : Index.STATE.PLAYER1);
        }else if(x<2){
            //increment and run recusively
            return checkDiagonal(x+1%3, y+1%3, tableIndex);
        }
        return Index.STATE.NONE;
    }*/

   /* private static STATE checkall(int[][] tableIndex){
        ArrayList<Boolean> tieArr = new ArrayList<Boolean>();
        for(int rownum=0;rownum<3;rownum++){
            if(checkRow(rownum,0,tableIndex) == Index.STATE.NONE){
                return Index.STATE.NONE;
            }
            if(checkColumn(0,rownum,tableIndex) == Index.STATE.NONE){//use reverse to check columns
                return Index.STATE.NONE;
            }
        }
        //check the two diagonals
        if(checkDiagonal(0,0,tableIndex) == Index.STATE.NONE){
            return Index.STATE.NONE;
        }
        if(checkDiagonal(0,2,tableIndex) == Index.STATE.NONE){
            return Index.STATE.NONE;
        }
        return Index.STATE.TIE;
    }*/





}