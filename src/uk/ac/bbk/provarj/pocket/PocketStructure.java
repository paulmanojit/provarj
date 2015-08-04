package uk.ac.bbk.provarj.pocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.AtomIterator;
import org.biojava.bio.structure.Structure;

public class PocketStructure {
	private List<Structure> structures;
	private SortedMap<Integer, Atom> atoms;

	/**
	 * 
	 * @param structures
	 */
	public PocketStructure(List<Structure> structures) {
		this.structures = structures;
		populateAtoms();
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<Integer> getAtomSerialNumbers() {
		return Collections.unmodifiableSet(atoms.keySet());
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Atom> getAtoms() {
		return Collections.unmodifiableList(new ArrayList<Atom>(atoms.values()));
	}
	
	/**
	 * 
	 * @param serialNumber
	 * @return
	 */
	public Atom getAtom(int serialNumber) {
		return atoms.get(serialNumber);
	}
	
	private void populateAtoms() {
		if (atoms != null) {
			return;
		}
		
		atoms = new TreeMap<Integer, Atom>();
		for (Structure structure : structures) {
			AtomIterator iterator = new AtomIterator(structure);
			while (iterator.hasNext()) {
				Atom atom = iterator.next();
				atoms.put(atom.getPDBserial(), atom);
			}
		}
	}
}
