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
public class EamonStrategy implements UserLoadBalanceStrategy {

    private static EamonStrategy strategy = new EamonStrategy();

    public static EamonStrategy getInstance() {
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

    private static final int GRAB_NUM = (int)(TOTAL_INIT_WEIGHT * 0.03 / 3);

    private static final int ALPHA = 9;

    private HashMap<Integer, Integer> numberMap = new HashMap<>();

    private static final boolean IS_DEBUG = Boolean.parseBoolean(System.getProperty("debug"));

    private EamonStrategy() {
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

                if (IS_DEBUG){
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
                    System.out.println("GRAB_NUM: " + GRAB_NUM + " GATE_NUM: " + GRAB_NUM * ALPHA + " SMALL_WEIGHT: " + smallWeight + " MEDIUM_WEIGHT: " + mediumWeight + " LARGE_WEIGHT: " + largeWeight);
                    numberMap.put(NUM_SMALL_OLD, numberMap.get(NUM_SMALL));
                    numberMap.put(NUM_MEDIUM_OLD, numberMap.get(NUM_MEDIUM));
                    numberMap.put(NUM_LARGE_OLD, numberMap.get(NUM_LARGE));
                    numberMap.put(NUM_TOTAL_OLD, numberMap.get(NUM_TOTAL));
                }

                weightChange();

            }
        }, 100, 100);
    }

    @Override
    public int select(URL url, Invocation invocation) {
        int targetMachine = getTargetMachineB();
        numberMap.put(targetMachine, numberMap.get(targetMachine) + 1);
        return targetMachine;
    }

    private int getTargetMachineA(){
        Random rand = new Random();

        int targetMachine = 2;
        int randNumber = rand.nextInt(Constants.activeThreadCount.get("small") + Constants.activeThreadCount.get("medium") + Constants.activeThreadCount.get("large"));
        if (randNumber < Constants.activeThreadCount.get("small")) {
            targetMachine = 0;
        } else if (randNumber >= Constants.activeThreadCount.get("small") && randNumber < Constants.activeThreadCount.get("small") + Constants.activeThreadCount.get("medium")) {
            targetMachine = 1;
        }
        return targetMachine;
    }

    private void weightChange(){
        // 避免线程冲突
        int smallWeightLocal = this.smallWeight;
        int mediumWeightLocal = this.mediumWeight;
        int largeWeightLocal = this.largeWeight;

        // 抢占权重
        int grabTotal = 0;
        if (smallWeightLocal > ALPHA * GRAB_NUM){
            smallWeightLocal = smallWeightLocal - GRAB_NUM;
            grabTotal += GRAB_NUM;
        }

        if (mediumWeightLocal > ALPHA * GRAB_NUM){
            mediumWeightLocal = mediumWeightLocal - GRAB_NUM;
            grabTotal += GRAB_NUM;
        }

        if (largeWeightLocal > ALPHA * GRAB_NUM){
            grabTotal += GRAB_NUM;
        }

        int totalActive = Constants.activeThreadCount.get("small") + Constants.activeThreadCount.get("medium") + Constants.activeThreadCount.get("large");
        double smallRatio = Constants.activeThreadCount.get("small") / (double)totalActive;
        double mediumRatio = Constants.activeThreadCount.get("medium") / (double)totalActive;
        smallWeightLocal = (int) (smallWeightLocal + grabTotal* smallRatio);
        mediumWeightLocal = (int) (mediumWeightLocal + grabTotal* mediumRatio);
        largeWeightLocal = TOTAL_INIT_WEIGHT - smallWeightLocal - mediumWeightLocal;

        if (smallWeightLocal != this.smallWeight){
            this.write(smallWeightLocal, mediumWeightLocal, largeWeightLocal);
        }

    }

    private int getTargetMachineB(){
        int targetMachine = 2;

        Random rand = new Random();
        int randNumber = rand.nextInt(TOTAL_INIT_WEIGHT);
        if (randNumber < this.smallWeight) {
            targetMachine = 0;
        } else if (randNumber < this.smallWeight + this.mediumWeight) {
            targetMachine = 1;
        }
        return targetMachine;
    }

    private void write(int smallWeight, int mediumWeight, int largeWeight){
        this.smallWeight = smallWeight;
        this.mediumWeight = mediumWeight;
        this.largeWeight = largeWeight;
    }



}
