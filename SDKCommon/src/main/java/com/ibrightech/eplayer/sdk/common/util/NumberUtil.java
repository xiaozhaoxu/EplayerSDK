package com.ibrightech.eplayer.sdk.common.util;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by zhaoxu2014 on 15/7/29.
 */
public class NumberUtil {

    private static final String TAG = NumberUtil.class.getSimpleName();

    public NumberUtil() {
    }

    public static BigDecimal long2Decimal(long money) {
        return BigDecimal.valueOf(money, 2);
    }

    public static Long decimal2Long(BigDecimal money) {
        return Long.valueOf(money.multiply(BigDecimal.valueOf(100L)).longValue());
    }

    public static String getMoneyYuanStr(long moneyFen) {
        return long2Decimal(moneyFen).toString();
    }

    public static Float getFloatFromFloatRoundHalfUp(float sourceNum, int scale) {
        BigDecimal bigDecimal = new BigDecimal((double)sourceNum);
        return Float.valueOf(bigDecimal.setScale(scale, 4).floatValue());
    }

    public static Float getDoubleFromDoubletRoundHalfUp(double sourceNum, int scale) {
        BigDecimal bigDecimal = new BigDecimal(sourceNum);
        return Float.valueOf(bigDecimal.setScale(scale, 4).floatValue());
    }

    public static Double getDoubleFromDoubletRoundHalfUp1(double sourceNum, int scale) {
        BigDecimal bigDecimal = new BigDecimal(sourceNum);
        return Double.valueOf(bigDecimal.setScale(scale, 4).doubleValue());
    }

    public static int convertFloatToInt(float sourceNum) {
        BigDecimal bigDecimal = new BigDecimal((double)sourceNum);
        return bigDecimal.setScale(0, 4).intValue();
    }

    public static int convertDoubleToInt(double sourceNum) {
        BigDecimal bigDecimal = new BigDecimal(sourceNum);
        return bigDecimal.setScale(0, 4).intValue();
    }

    public static int parseInt(String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException var3) {
            Log.d(TAG, "String to Int Error! Use def value! ");
            return def;
        }
    }

    public static long parseLong(String str, long def) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException var4) {
            Log.d(TAG, "String to Long Error! Use def value!");
            return def;
        }
    }

    public static float parseFloat(String str, float def) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException var3) {
            Log.d(TAG, "String to Float Error! Use def value!");
            return def;
        }
    }

    public static Double parseDouble(String str, double def) {
        try {
            return Double.valueOf(Double.parseDouble(str));
        } catch (NumberFormatException var4) {
            Log.d(TAG, "String to Double Error! Use def value!");
            return Double.valueOf(def);
        }
    }

    public static String getToFormatNum(int num) {
        String numStr = "" + num;
        if(num < 10 && num != 0) {
            numStr = "0" + num;
        }

        return numStr;
    }

    public static int getRandom(int startNum, int endNum) {
        int num = (int) Math.round(Math.random() * (double) (endNum - startNum) + (double) startNum);
        return num;
    }

    public static int[] getRandom(int startNum, int endNum, int needNum) {
        int[] nums = new int[needNum];
        HashSet integerSet = new HashSet();

        while(integerSet.size() < needNum) {
            int ints = (int) Math.round(Math.random() * (double) (endNum - startNum) + (double) startNum);
            integerSet.add(Integer.valueOf(ints));
        }

        Object[] var7 = integerSet.toArray();

        for(int i = 0; i < var7.length; ++i) {
            nums[i] = Integer.parseInt(var7[i].toString());
        }

        return nums;
    }

    public static int[] getMachineSelectionNum(int size, int[] numbers) {
        int length = numbers.length;
        int[] num = new int[size];
        Random random = new Random();
        HashSet set = new HashSet();

        while(set.size() < size) {
            int ints = numbers[random.nextInt(length)];
            boolean i = false;
            if(!i) {
                set.add(Integer.valueOf(ints));
            }
        }

        Object[] var8 = set.toArray();

        for(int var9 = 0; var9 < var8.length; ++var9) {
            num[var9] = Integer.parseInt(var8[var9].toString());
        }

        return num;
    }

    public static int[] getMachineSelectionNumNo(int size, int[] numbers) {
        int length = numbers.length;
        int[] num = new int[size];
        Random random = new Random();
        ArrayList list = new ArrayList();

        int i;
        while(list.size() < size) {
            i = numbers[random.nextInt(length)];
            list.add(Integer.valueOf(i));
        }

        for(i = 0; i < list.size(); ++i) {
            num[i] = ((Integer)list.get(i)).intValue();
        }

        return num;
    }

    public static int[] getUseNums(int[] allNums, int[] havaNums) {
        ArrayList integers = new ArrayList();
        int[] numbers = allNums;
        int i = allNums.length;

        for(int i$ = 0; i$ < i; ++i$) {
            int allNum = numbers[i$];
            int n = 0;
            int[] arr$ = havaNums;
            int len$ = havaNums.length;

            for(int i$1 = 0; i$1 < len$; ++i$1) {
                int havaNum = arr$[i$1];
                if(allNum == havaNum) {
                    ++n;
                }
            }

            if(n == 0) {
                integers.add(Integer.valueOf(allNum));
            }
        }

        numbers = new int[integers.size()];

        for(i = 0; i < integers.size(); ++i) {
            numbers[i] = ((Integer)integers.get(i)).intValue();
        }

        return numbers;
    }


    public static void main(String[] args) {
        System.out.println(getMoneyYuanStr(123456789L));
        System.out.println(getMoneyYuanStr(0L));
        System.out.println(getMoneyYuanStr(10L));
        System.out.println(getMoneyYuanStr(0L));
        System.out.println(getMoneyYuanStr(123L));
        System.out.println(getMoneyYuanStr(1234L));
        System.out.println(getMoneyYuanStr(1L));
    }
}
