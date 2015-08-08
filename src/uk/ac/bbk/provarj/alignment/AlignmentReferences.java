package uk.ac.bbk.provarj.alignment;

import java.util.LinkedHashMap;

public class AlignmentReferences {
	private LinkedHashMap<Integer, Integer> pdbResidueToAlignmentReferences = new LinkedHashMap<Integer, Integer>();
	
	private LinkedHashMap<Integer, Integer> alignmentToPdbResidueReferences = new LinkedHashMap<Integer, Integer>();
	
	private LinkedHashMap<Integer, Character> pdbResidues = new LinkedHashMap<Integer, Character>();
	
	public void add(AlignmentReferenceItem item) {
		pdbResidueToAlignmentReferences.put(item.getPdbResidueNumber(), item.getAlignmentIndex());
		alignmentToPdbResidueReferences.put(item.getAlignmentIndex(), item.getPdbResidueNumber());
		pdbResidues.put(item.getPdbResidueNumber(), item.getOneLetterResidueType());
	}
	
	public AlignmentReferenceItem getByPdbResidueNumber(int pdbResidueNumber) {
		if (pdbResidueToAlignmentReferences.containsKey(pdbResidueNumber)) {
			return new AlignmentReferenceItem(
					pdbResidueNumber,
					pdbResidueToAlignmentReferences.get(pdbResidueNumber),
					pdbResidues.get(pdbResidueNumber));
		}
		
		return AlignmentReferenceItem.createEmptyItem();
	}
	
	public AlignmentReferenceItem getByAlignmentIndex(int alignmentIndex) {
		if (alignmentToPdbResidueReferences.containsKey(alignmentIndex)) {
			int pdbResidueNumber = alignmentToPdbResidueReferences.get(alignmentIndex);
			return new AlignmentReferenceItem(
					pdbResidueNumber,
					alignmentIndex,
					pdbResidues.get(pdbResidueNumber));
		}
		
		return AlignmentReferenceItem.createEmptyItem();
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public int size() {
		return pdbResidueToAlignmentReferences.size();
	}
}
