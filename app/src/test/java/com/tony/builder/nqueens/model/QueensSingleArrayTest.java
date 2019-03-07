package com.tony.builder.nqueens.model;

import org.junit.Test;

public class QueensSingleArrayTest {
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
            }

            @Override
            public void onFinished(int solutionCount) {
                System.out.println("onFinished (" + solutionCount + ")");
            }
        });
        queensModel.onStart(8);
        for (int i = 0; i < 100; i++) {
            queensModel.onNext();
            queensModel.print();
            System.out.println("===============================");
        }
    }
}