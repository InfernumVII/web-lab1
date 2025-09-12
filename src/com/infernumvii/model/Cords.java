package com.infernumvii.model;

public class Cords {
    private int x;
    private int y; //TODO float
    private int R;

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getR() {
        return R;
    }
    
    @Override
    public String toString() {
        return "Cords [x=" + x + ", y=" + y + ", R=" + R + "]";
    }
    
    public boolean IsPointInTheArea(){
        boolean circleCond = (x >= 0 && y >= 0 && x * x + y * y <= R*R/4);
        boolean triangleCond = (x <= 0 && y <= 0 && x >= -R && y >= -R && y >= x - R);
        boolean squareCond = (x >= 0 && y <= 0 && x <= R && y >= -R);
        return circleCond || triangleCond || squareCond;
    }
}
