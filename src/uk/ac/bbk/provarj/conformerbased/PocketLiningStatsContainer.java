package uk.ac.bbk.provarj.conformerbased;

import uk.ac.bbk.provarj.pocket.AminoPocketLiningItem;
import uk.ac.bbk.provarj.pocket.AtomPocketLiningItem;
import uk.ac.bbk.provarj.pocket.PocketLiningData;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class PocketLiningStatsContainer {
    private List<PocketLiningData> pocketLiningData;

    private SortedMap<Integer, Double> atomCounts = new TreeMap<Integer, Double>();
    private SortedMap<Integer, Double> aminoCounts = new TreeMap<Integer, Double>();
    private SortedMap<Integer, Double>  averageAtomsPerAminoAcid = new TreeMap<Integer, Double>();

    public PocketLiningStatsContainer(List<PocketLiningData> pocketLiningData) {
        this.pocketLiningData = pocketLiningData;
        calculateTotals();
    }

    public List<PocketLiningData> getPocketLiningData() {
        return pocketLiningData;
    }

    public SortedMap<Integer, Double> getAtomCounts() {
        return atomCounts;
    }

    public SortedMap<Integer, Double> getAminoCounts() {
        return aminoCounts;
    }

    public SortedMap<Integer, Double> getAverageAtomsPerAminoAcid() {
        return averageAtomsPerAminoAcid;
    }

    public int getStructureCount() {
        return pocketLiningData.size();
    }

    private void calculateTotals() {
        for (PocketLiningData data : pocketLiningData) {
            SortedMap<Integer, AtomPocketLiningItem> pocketLiningAtoms = data.getPocketLiningAtoms();
            for (Integer index : pocketLiningAtoms.keySet()) {
                if (!atomCounts.containsKey(index)) {
                    atomCounts.put(index, 0.0);
                }

                AtomPocketLiningItem atomPocketLiningItem = pocketLiningAtoms.get(index);
                if (atomPocketLiningItem.isPocketLining()) {
                    Double updatedCount = atomCounts.get(index) + 1;
                    atomCounts.put(index, updatedCount);
                }
            }

            SortedMap<Integer, AminoPocketLiningItem> pocketLiningAminoAcids = data.getPocketLiningAminoAcids();
            for (Integer index : pocketLiningAminoAcids.keySet()) {
                if (!aminoCounts.containsKey(index)) {
                    aminoCounts.put(index, 0.0);
                }

                AminoPocketLiningItem aminoPocketLiningItem = pocketLiningAminoAcids.get(index);
                if (averageAtomsPerAminoAcid.containsKey(index)) {
                    double updatedValue = averageAtomsPerAminoAcid.get(index) + aminoPocketLiningItem.getAveragePocketLiningAtomCount();
                    averageAtomsPerAminoAcid.put(index, updatedValue);
                } else {
                    averageAtomsPerAminoAcid.put(index, aminoPocketLiningItem.getAveragePocketLiningAtomCount());
                }

                if (aminoPocketLiningItem.isPocketLining()) {
                    Double updatedCount = aminoCounts.get(index) + 1;
                    aminoCounts.put(index, updatedCount);
                }
            }
        }
    }
}
