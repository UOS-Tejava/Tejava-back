package com.sogong.tejava.validate.config.util;

public class EmployeeCapacity {
    private static int chef = 5;
    private static int delivery = 5;


    public static int getChef() {
        return EmployeeCapacity.chef;
    }

    public static int getDelivery() {
        return EmployeeCapacity.delivery;
    }

    public static void decreaseChef() {
        chef -= 1;
    }

    public static void increaseChef() {
        chef += 1;
    }

    public static void decreaseDelivery() {
        delivery -= 1;
    }

    public static void increaseDelivery() {
        delivery += 1;
    }
}
