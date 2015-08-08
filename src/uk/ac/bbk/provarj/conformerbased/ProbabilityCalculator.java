package uk.ac.bbk.provarj.conformerbased;

import java.util.*;

public class ProbabilityCalculator {
    private SortedMap<Integer, Double> probabilities = new TreeMap<Integer, Double>();

    public ProbabilityCalculator(SortedMap<Integer, Double> totalCounts, int structureCount) {
        for (Integer key : totalCounts.keySet()) {
            Double value = totalCounts.get(key);
            double probability = value / structureCount;
            probabilities.put(key, probability);
        }
    }

    public SortedMap<Integer, Double> getProbabilities() {
        return probabilities;
    }
}