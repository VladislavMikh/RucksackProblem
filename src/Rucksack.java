import java.util.*;

class Rucksack {
    private int capacity;
    private int[] items;
    private int[] weight;
    private int mutationCount;
    private int sumCost;

    Rucksack(int capacity, int[] items, int[] weight, int mutationCount) {


        if (items.length != weight.length) {
            throw new IllegalArgumentException("у каждого предмета должен быть вес, и наоборот");
        }

        this.capacity = capacity;
        this.items = items;
        this.weight = weight;
        this.mutationCount = mutationCount;
        int cost = 0;
        for (int element : items) {
            cost += element;
        }
        this.sumCost = cost;
    }

    // Сам генетический алгоритм поиска решения

    private class SortedByEstimate implements Comparator {
        @Override
        public int compare(Object first, Object second) {
            return Double.compare(estimateFunc((boolean[]) first), estimateFunc((boolean[]) second));
        }
    }

    boolean[] findBest() {
        boolean[][] generation = firstGeneration();
        boolean[] bestSingle = generation[0];
        double bestEst = estimateFunc(bestSingle);
        for (int i = 1; i < generation.length; i++) {
            if (estimateFunc(generation[i]) > bestEst) {
                bestSingle = generation[i];
                bestEst = estimateFunc(generation[i]);
            }
        }

        while (bestEst != 1.0 && mutationCount > 0) {
            double generationAvg = averageFunc(generation);
            generation = nextGeneration(generation);
            double newAvg = averageFunc(generation);

            if (newAvg <= generationAvg) {
                Random random = new Random();
                int index = random.nextInt(items.length - 1);
                generation[index] = mutation(generation[index]);
            }

            if (averageFunc(generation) <= generationAvg) {
                Random random = new Random();
                int index = random.nextInt(items.length - 1);
                generation[index] = randomSingle();
            }

            boolean[] bestThisGeneration = generation[0];
            double est = estimateFunc(bestThisGeneration);
            for (int i = 1; i < generation.length; i++) {
                if (estimateFunc(generation[i]) > est) {
                    bestThisGeneration = generation[i];
                    est = estimateFunc(generation[i]);
                }
            }

            double estimationOfMask = estimateFunc(bestThisGeneration);
            if (estimationOfMask > bestEst) {
                bestSingle = bestThisGeneration;
                bestEst = estimationOfMask;
            }
        }
        return bestSingle;
    }

    private double estimateFunc(boolean[] single) {
        int weightOfSingle = 0;

        for (int i = 0; i < single.length; i++) {
            if (single[i]) {
                weightOfSingle += weight[i];
            }
        }

        int cost = 0;

        for (int i = 0; i < single.length; i++) {
            if (single[i]) {
                cost += items[i];
            }
        }

        if (weightOfSingle > capacity) {
            return 0;
        }
        else {
            return (double) cost / sumCost;
        }
    }

    private double averageFunc(boolean[][] generation) {
        double average = 0;
        for (boolean[] element : generation) {
            average += estimateFunc(element);
        }
        return average / items.length;
    }

    private boolean[][] firstGeneration() {
        boolean[][] generation = new boolean[items.length][items.length];
        for (int i = 0; i < items.length; i++) {
            for (int j = 0; i < items.length; i++) {
                Random r = new Random();
                generation[i][j] = r.nextBoolean();
            }
        }
        return generation;
    }

    private boolean[][] nextGeneration(boolean[][] prev) {
        int needed = factorialCount(prev.length);
        boolean[][] generation = new boolean[items.length][items.length];
        boolean[][] survivors = generation.clone();
        Arrays.sort(survivors, new SortedByEstimate());
        survivors = Arrays.copyOf(survivors, needed);
        Random random = new Random();
        for (int i = 0; i < prev.length - 1; i++) {
            int fatherIndex = random.nextInt(needed - 1);
            int motherIndex = random.nextInt(needed - 1);
            while (fatherIndex == motherIndex) {
                motherIndex = random.nextInt(needed - 1);
            }
            int shift = 1 + random.nextInt(prev.length - 2);
            boolean[] arr1 = Arrays.copyOfRange(survivors[fatherIndex], 0, shift);
            boolean[] arr2 = Arrays.copyOfRange(survivors[motherIndex], shift + 1, survivors[motherIndex].length - 1);
            generation[i] = new boolean[arr1.length + arr2.length];
            System.arraycopy(arr1, 0, generation[i], 0, arr1.length);
            System.arraycopy(arr2, 0, generation[i], arr1.length, arr2.length);
        }
        return generation;
    }

    private int factorialCount(int more){
        int prev = 0;
        int value = 1;
        while (value < more) {
            prev++;
            value *= prev;
        }
        return prev;
    }

    // мутация
    private boolean[] mutation(boolean[] single) {
        boolean[] mutant = single.clone();
        //System.out.println("mutant = " + mutant.length);
        //System.out.println("single = " + single.length);
        Random random = new Random();
        int pos = random.nextInt(single.length - 1);
        mutant[pos] = !mutant[pos];
        mutationCount--;
        return mutant;
    }

    // появление случайной особи
    private boolean[] randomSingle() {
        boolean[] rand = new boolean[items.length];
        Random random = new Random();
        for (int i = 0; i < rand.length; i++) {
            rand[i] = random.nextBoolean();
        }
        return rand;
    }
}