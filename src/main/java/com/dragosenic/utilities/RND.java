package com.dragosenic.utilities;

public class RND {

    public static int generateNewAccountNumber() { return generateRandomInteger(1000000000, 2000000000); }

    public static int generateNewAccountHolderId() { return generateRandomInteger(100000000, 999999999); }

    public static int generateRandomInteger(int minIncluded, int maxIncluded) {
        return (int) (Math.random() * (maxIncluded + 1 - minIncluded)) + minIncluded;
    }

}
