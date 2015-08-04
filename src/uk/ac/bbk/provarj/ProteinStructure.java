package uk.ac.bbk.provarj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;

public class ProteinStructure {
	private Structure structure;

	/**
	 * 
	 * @param structure
	 */
	public ProteinStructure(Structure structure) {
		this.structure = structure;
	}
	
	/**
	 * @return the aminoAcids
	 */
	public List<Group> getAminoAcids() {
		List<Group> aminoAcids = new ArrayList<Group>();
		for (Chain chain : getChains()) {
			aminoAcids.addAll(chain.getAtomGroups("amino"));
		}
		
		return aminoAcids;
	}
	
	/**
	 * 
	 * @param chainIndex
	 * @return
	 */
	public List<Group> getAminoAcids(int chainIndex) {
		if(chainIndex >= getChains().size()) {
			throw new IndexOutOfBoundsException("Structure does not have any chain with index " + chainIndex);
		}
		
		Chain chain = getChain(chainIndex);
		return chain.getAtomGroups("amino");		
	}

	/**
	 * 
	 * @return
	 */
	public List<Chain> getChains() {
		return structure.getChains();
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public Chain getChain(int index) {
		return structure.getChain(index);
	}

	/**
	 * 
	 * @return
	 */
	public String getPDBCode() {
		return structure.getPDBCode();
	}

	/**
	 * 
	 * @return
	 */
	public List<Atom> getAtoms() {
		SortedMap<Integer, Atom> atoms = new TreeMap<Integer, Atom>();
		for (Chain chain : getChains()) {
			for (Group amino : chain.getAtomGroups("amino")) {
				for (Atom atom : amino.getAtoms()) {
					atoms.put(atom.getPDBserial(), atom);
				}
			}
		}
		
		return Collections.unmodifiableList(new ArrayList<Atom>(atoms.values()));
	}
	
	/**
	 * 
	 * @param chainIndex
	 * @return
	 */
	public List<Atom> getAtoms(int chainIndex) {
		if(chainIndex >= getChains().size()) {
			throw new IndexOutOfBoundsException("Structure does not have any chain with index " + chainIndex);
		}
		
		Chain chain = getChain(chainIndex);
		SortedMap<Integer, Atom> atoms = new TreeMap<Integer, Atom>();
		for (Group amino : chain.getAtomGroups("amino")) {
			for (Atom atom : amino.getAtoms()) {
				atoms.put(atom.getPDBserial(), atom);
			}
		}
		
		return Collections.unmodifiableList(new ArrayList<Atom>(atoms.values()));
	}
}
