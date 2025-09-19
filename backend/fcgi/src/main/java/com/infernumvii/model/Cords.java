package com.infernumvii.model;

import java.math.BigDecimal;

import com.infernumvii.Main;
import com.infernumvii.exception.CordsInvalidFormat;

public class Cords {
    private int x;
    private static final int maxX = 4;
    private static final int minX = -4;
    private BigDecimal y;
    private static final BigDecimal minY = new BigDecimal(-5);
    private static final BigDecimal maxY = new BigDecimal(3);
    private int R;
    private static final int minR = 1;
    private static final int maxR = 5;

    private Cords(int x, BigDecimal y, int R){
        this.x = x;
        this.y = y;
        this.R = R;
    }

    public int getX() {
        return x;
    }
    public BigDecimal getY() {
        return y;
    }
    public int getR() {
        return R;
    }

    public boolean checkX(){
        return (x >= minX && x <= maxX);
    }

    public boolean checkY(){
        return (y.compareTo(minY) >= 0 && y.compareTo(maxY) <= 0);
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
    

    /*
     * // boolean circleCond = (x >= 0 && y >= 0 && x * x + y * y <= R*R/4);
     // boolean triangleCond = (x <= 0 && y <= 0 && x >= -R && y >= -R && y >= x - R);
     // boolean squareCond = (x >= 0 && y <= 0 && x <= R && y >= -R);
     // return circleCond || triangleCond || squareCond;
     */
    public boolean IsPointInTheArea(){
        boolean circleCond = (x >= 0 && y.compareTo(new BigDecimal(0)) >= 0 && new BigDecimal(x * x).add(y.pow(2)).compareTo(new BigDecimal(R*R/4.0f)) <= 0);
        boolean triangleCond = (x <= 0 && y.compareTo(new BigDecimal(0)) <= 0 && y.compareTo(new BigDecimal(-0.5*x - R/2.0f)) >= 0);
        boolean squareCond = (x >= 0 && y.compareTo(new BigDecimal(0)) <= 0 && x <= R && y.compareTo(new BigDecimal(-R)) >= 0);
        return circleCond || triangleCond || squareCond;
    }

    public static int getMaxX() {
        return maxX;
    }
    public static int getMinX() {
        return minX;
    }
    public static BigDecimal getMinY() {
        return minY;
    }
    public static BigDecimal getMaxY() {
        return maxY;
    }
    public static int getMinR() {
        return minR;
    }
    public static int getMaxR() {
        return maxR;
    }


}
