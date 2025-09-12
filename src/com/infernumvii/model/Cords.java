package com.infernumvii.model;

import com.infernumvii.Main;
import com.infernumvii.exception.CordsInvalidFormat;

public class Cords {
    private int x;
    private static final int maxX = 4;
    private static final int minX = -4;
    private float y; //TODO float
    private static final float minY = -5;
    private static final float maxY = 3;
    private int R;
    private static final int minR = 1;
    private static final int maxR = 5;

    public int getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public int getR() {
        return R;
    }

    public boolean checkX(){
        return (x >= minX && x <= maxX);
    }

    public boolean checkY(){
        return (y >= minY && y <= maxY);
    }

    public boolean checkR(){
        return (R >= minR && R <= maxR);
    }

    public void validateCords() throws CordsInvalidFormat{
        if (!(checkX() && checkY() && checkR())){
            throw new CordsInvalidFormat();
        }
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

    public static int getMaxX() {
        return maxX;
    }
    public static int getMinX() {
        return minX;
    }
    public static float getMinY() {
        return minY;
    }
    public static float getMaxY() {
        return maxY;
    }
    public static int getMinR() {
        return minR;
    }
    public static int getMaxR() {
        return maxR;
    }
}
