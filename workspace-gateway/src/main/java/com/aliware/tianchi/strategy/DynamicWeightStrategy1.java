package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 16:46:35
 */
public class DynamicWeightStrategy1 extends AbstractStrategy {

    private static DynamicWeightStrategy1 strategy = new DynamicWeightStrategy1();

    public static DynamicWeightStrategy1 getInstance() {
        return strategy;
    }

    public static DynamicWeightStrategy1 getInstance(String dataFrom) {
        strategy.dataFrom=dataFrom;
        return strategy;
    }

    private static final int NUM_SMALL = 0;
    private static final int NUM_MEDIUM = 1;
    private static final int NUM_LARGE = 2;


    private static final int NUM_SMALL_OLD = 10;
    private static final int NUM_MEDIUM_OLD = 11;
    private static final int NUM_LARGE_OLD = 12;


    private static final int NUM_TOTAL = 20;
    private static final int NUM_TOTAL_OLD = 21;


    private static final int ACTIVE_SMALL = 30;

    private static final int ACTIVE_MEDIUM = 31;

    private static final int ACTIVE_LARGE = 32;

    private static final int ACTIVE_SMALL_OLD = 40;

    private static final int ACTIVE_MEDIUM_OLD = 41;

    private static final int ACTIVE_LARGE_OLD = 42;

    private static final int SMALL_INIT_WEIGHT = 200;

    private static final int MEDIUM_INIT_WEIGHT = 450;

    private static final int LARGE_INIT_WEIGHT = 650;

    private static final int TOTAL_INIT_WEIGHT = SMALL_INIT_WEIGHT + MEDIUM_INIT_WEIGHT + LARGE_INIT_WEIGHT;

    private int smallWeight = SMALL_INIT_WEIGHT;

    private int mediumWeight = MEDIUM_INIT_WEIGHT;

    private int largeWeight = LARGE_INIT_WEIGHT;

    // 活跃门槛
    private static final int ALPHA_MAX = 80;

    private static final int ALPHA_LOW = 10;

    // 权重抢占参数
    private static final int GRAB_NUM = (int) (TOTAL_INIT_WEIGHT * 0.04);

    // 权重平滑过渡参数
    private static final int BETA = 1;

    // 权重调整滑动窗口时间 单位毫秒
    private static final int SLIDING_WINDOW_TIME = 100;

    private HashMap<Integer, Integer> numberMap = new HashMap<>();

    private static final boolean IS_DEBUG = Boolean.parseBoolean(System.getProperty("debug"));

    private DynamicWeightStrategy1() {
        numberMap.put(NUM_SMALL, 0);
        numberMap.put(NUM_MEDIUM, 0);
        numberMap.put(NUM_LARGE, 0);
        numberMap.put(NUM_SMALL_OLD, 0);
        numberMap.put(NUM_MEDIUM_OLD, 0);
        numberMap.put(NUM_LARGE_OLD, 0);

        numberMap.put(NUM_TOTAL, 0);
        numberMap.put(NUM_TOTAL_OLD, 0);

        numberMap.put(ACTIVE_SMALL, 0);
        numberMap.put(ACTIVE_MEDIUM, 0);
        numberMap.put(ACTIVE_LARGE, 0);

        numberMap.put(ACTIVE_SMALL_OLD, 0);
        numberMap.put(ACTIVE_MEDIUM_OLD, 0);
        numberMap.put(ACTIVE_LARGE_OLD, 0);


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                if (IS_DEBUG) {
                    numberMap.put(NUM_TOTAL, numberMap.get(NUM_SMALL) + numberMap.get(NUM_MEDIUM) + numberMap.get(NUM_LARGE));
                    System.out.println(
                            " NUM_SMALL: " + numberMap.get(NUM_SMALL) +
                                    " NUM_MEDIUM: " + numberMap.get(NUM_MEDIUM) +
                                    " NUM_LARGE: " + numberMap.get(NUM_LARGE) +
                                    " NUM_TOTAL: " + numberMap.get(NUM_TOTAL) +
                                    " D_SMALL: " + (numberMap.get(NUM_SMALL) - numberMap.get(NUM_SMALL_OLD)) +
                                    " D_MEDIUM: " + (numberMap.get(NUM_MEDIUM) - numberMap.get(NUM_MEDIUM_OLD)) +
                                    " D_LARGE: " + (numberMap.get(NUM_LARGE) - numberMap.get(NUM_LARGE_OLD)) +
                                    " D_TOTAL: " + (numberMap.get(NUM_TOTAL) - numberMap.get(NUM_TOTAL_OLD))
                    );
                    System.out.println("GRAB_NUM: " + GRAB_NUM + " GATE_NUM: " + GRAB_NUM * BETA + " SMALL_WEIGHT: " + smallWeight + " MEDIUM_WEIGHT: " + mediumWeight + " LARGE_WEIGHT: " + largeWeight);
                    numberMap.put(NUM_SMALL_OLD, numberMap.get(NUM_SMALL));
                    numberMap.put(NUM_MEDIUM_OLD, numberMap.get(NUM_MEDIUM));
                    numberMap.put(NUM_LARGE_OLD, numberMap.get(NUM_LARGE));
                    numberMap.put(NUM_TOTAL_OLD, numberMap.get(NUM_TOTAL));
                }

                weightChange();

            }
        }, SLIDING_WINDOW_TIME, SLIDING_WINDOW_TIME);
    }

    @Override
    public int select(URL url, Invocation invocation) {
        int targetMachine = getTargetMachineB();
        numberMap.put(targetMachine, numberMap.get(targetMachine) + 1);
        return targetMachine;
    }

    private int getTargetMachineA() {
        Random rand = new Random();

        int targetMachine = 2;
        int randNumber = rand.nextInt((int)(Constants.longAdderSmall.longValue() + Constants.longAdderMedium.longValue() + Constants.longAdderLarge.longValue()));
        if (randNumber < Constants.longAdderSmall.longValue()) {
            targetMachine = 0;
        } else if (randNumber >= Constants.longAdderSmall.longValue() && randNumber < Constants.longAdderSmall.longValue() + Constants.longAdderMedium.longValue()) {
            targetMachine = 1;
        }
        return targetMachine;
    }

    private int getTargetMachineB() {


        int smallWeightLocal = this.smallWeight;
        int mediumWeightLocal = this.mediumWeight;
        int largeWeightLocal = this.largeWeight;

        // 低活跃门槛保护
        smallWeightLocal = (int) (smallWeightLocal * ratioA(Constants.longAdderSmall.longValue(), ALPHA_MAX * smallWeightLocal / (double)largeWeightLocal, ALPHA_LOW* smallWeightLocal / (double)largeWeightLocal));
        mediumWeightLocal = (int) (mediumWeightLocal * ratioA(Constants.longAdderMedium.longValue(), ALPHA_MAX * mediumWeightLocal / (double)largeWeightLocal, ALPHA_LOW* mediumWeightLocal / (double)largeWeightLocal));
        largeWeightLocal = (int) (largeWeightLocal * ratioA(Constants.longAdderLarge.longValue(), ALPHA_MAX , ALPHA_LOW));


        int targetMachine = 2;
        Random rand = new Random();
        int randNumber = rand.nextInt(smallWeightLocal + mediumWeightLocal + largeWeightLocal);
        if (randNumber < smallWeightLocal) {
            targetMachine = 0;
        } else if (randNumber < smallWeightLocal + mediumWeightLocal) {
            targetMachine = 1;
        }
        return targetMachine;
    }


    private void weightChange() {
        // 避免线程冲突
        int smallWeightLocal = this.smallWeight;
        int mediumWeightLocal = this.mediumWeight;
        int largeWeightLocal = this.largeWeight;

        // 抢占权重
        int grabTotal = 0;
        if (smallWeightLocal > BETA * GRAB_NUM) {
            smallWeightLocal = smallWeightLocal - GRAB_NUM;
            grabTotal += GRAB_NUM;
        }

        if (mediumWeightLocal > BETA * GRAB_NUM) {
            mediumWeightLocal = mediumWeightLocal - GRAB_NUM;
            grabTotal += GRAB_NUM;
        }

        if (largeWeightLocal > BETA * GRAB_NUM) {
            grabTotal += GRAB_NUM;
        }

        // 分配权重
        long totalActive = Constants.longAdderSmall.longValue() + Constants.longAdderMedium.longValue() + Constants.longAdderLarge.longValue();
        double smallRatio = Constants.longAdderSmall.longValue() / (double) totalActive;
        double mediumRatio = Constants.longAdderMedium.longValue() / (double) totalActive;
        smallWeightLocal = (int) (smallWeightLocal + grabTotal * smallRatio);
        mediumWeightLocal = (int) (mediumWeightLocal + grabTotal * mediumRatio);
        largeWeightLocal = TOTAL_INIT_WEIGHT - smallWeightLocal - mediumWeightLocal;

        // 写出权重，不管线程如何抢占，始终保持总值稳定
        if (smallWeightLocal != this.smallWeight) {
            this.write(smallWeightLocal, mediumWeightLocal, largeWeightLocal);
        }

    }


    private void write(int smallWeight, int mediumWeight, int largeWeight) {
        this.smallWeight = smallWeight;
        this.mediumWeight = mediumWeight;
        this.largeWeight = largeWeight;
    }

    private double ratioA(double activeNum, double max, double min){
        if (activeNum > max){
            return 1;
        }else if (activeNum < min){
            return 0;
        }
        return - Math.pow((activeNum - max), 2) / Math.pow((max - min), 2) + 1;
    }

    private double ratioB(double activeNum, double max, double min){
        if (activeNum > max){
            return 1;
        }else if (activeNum < min){
            return 0;
        }
        return (activeNum - max)/ (max - min) + 1;
    }

}
