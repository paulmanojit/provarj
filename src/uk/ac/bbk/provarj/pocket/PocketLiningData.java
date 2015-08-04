package uk.ac.bbk.provarj.pocket;

import java.util.Collections;
import java.util.SortedMap;

public class PocketLiningData {
	private SortedMap<Integer, AminoPocketLiningItem> pocketLiningAminoAcids;
	private SortedMap<Integer, AtomPocketLiningItem> pocketLiningAtoms;

	public PocketLiningData(
			SortedMap<Integer, AtomPocketLiningItem> pocketLiningAtoms,
			SortedMap<Integer, AminoPocketLiningItem> pocketLiningAminoAcids) {
		this.pocketLiningAminoAcids = pocketLiningAminoAcids;
		this.pocketLiningAtoms = pocketLiningAtoms;
	}

	public SortedMap<Integer, AminoPocketLiningItem> getPocketLiningAminoAcids() {
		return Collections.unmodifiableSortedMap(pocketLiningAminoAcids);
	}

	public SortedMap<Integer, AtomPocketLiningItem> getPocketLiningAtoms() {
		return Collections.unmodifiableSortedMap(pocketLiningAtoms);
	}
}