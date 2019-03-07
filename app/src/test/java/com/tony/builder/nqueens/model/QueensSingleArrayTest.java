package com.tony.builder.nqueens.model;

import org.junit.Test;

public class QueensSingleArrayTest {
    boolean isSolved = false;
    boolean isFinished = false;
    @Test
    public void testQueensModel() {
        QueensModel queensModel = new QueensSingleArray();
        queensModel.setEventListener(new QueensModel.EventListener() {
            @Override
            public void onMoveChess(int x, int y) {
                System.out.println("onMoveChess (" + x + "," + y + ")");
            }

            @Override
            public void onMoveLayer(int oldLayer, int newLayer) {
                System.out.println("onMoveLayer (" + oldLayer + "->" + newLayer + ")");
            }

            @Override
            public void onSolution(int count, int[] position) {
                System.out.println("onSolution (" + count + ")");
                isSolved = true;
            }

            @Override
            public void onFinished(int solutionCount) {
                System.out.println("onFinished (" + solutionCount + ")");
                isFinished = true;
            }
        });
        queensModel.onStart(8);
        int stepCount = 0;
        while (!isFinished) {
            stepCount++;
            queensModel.onNext();
            queensModel.print();
            System.out.println("===========step : "+stepCount+"====================");
        }
    }
}