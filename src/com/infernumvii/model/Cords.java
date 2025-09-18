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

    public static void main(String[] args) {
        Cords cords1 = new Cords(-1, new BigDecimal(-1), 1);
        System.out.println("Test 1 - Point (-1, -1) with R=1: " + cords1.IsPointInTheArea());
        
        // Test 2: Point in circle quadrant
        Cords cords2 = new Cords(0, new BigDecimal(0), 4);
        System.out.println("Test 2 - Point (0, 0) with R=4: " + cords2.IsPointInTheArea());
        
        // Test 3: Point in triangle quadrant
        Cords cords3 = new Cords(-2, new BigDecimal(-2), 4);
        System.out.println("Test 3 - Point (-2, -2) with R=4: " + cords3.IsPointInTheArea());
        
        // Test 4: Point in square quadrant
        Cords cords4 = new Cords(2, new BigDecimal(-2), 4);
        System.out.println("Test 4 - Point (2, -2) with R=4: " + cords4.IsPointInTheArea());
        
        // Test 5: Point outside all areas (second quadrant)
        Cords cords5 = new Cords(-2, new BigDecimal(2), 4);
        System.out.println("Test 5 - Point (-2, 2) with R=4: " + cords5.IsPointInTheArea());
        
        // Test 6: Point on circle boundary
        Cords cords6 = new Cords(2, new BigDecimal(0), 4);
        System.out.println("Test 6 - Point (2, 0) with R=4: " + cords6.IsPointInTheArea());
        
        // Test 7: Point on triangle boundary
        Cords cords7 = new Cords(-3, new BigDecimal(-1), 4);
        System.out.println("Test 7 - Point (-3, -1) with R=4: " + cords7.IsPointInTheArea());
        
        // Test 8: Different radius
        Cords cords8 = new Cords(1, new BigDecimal(1), 2);
        System.out.println("Test 8 - Point (1, 1) with R=2: " + cords8.IsPointInTheArea());
        
        // Test 9: Edge case - origin
        Cords cords9 = new Cords(0, new BigDecimal(0), 5);
        System.out.println("Test 9 - Point (0, 0) with R=5: " + cords9.IsPointInTheArea());
        
        // Test 10: Point just outside circle
        Cords cords10 = new Cords(3, new BigDecimal(3), 4);
        System.out.println("Test 10 - Point (3, 3) with R=4: " + cords10.IsPointInTheArea());
    }
}
