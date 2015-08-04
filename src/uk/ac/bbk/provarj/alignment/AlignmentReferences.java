package uk.ac.bbk.provarj.alignment;

import java.util.LinkedHashMap;

public class AlignmentReferences {
	private LinkedHashMap<Integer, Integer> _pdbResidueToAlignmentReferences = new LinkedHashMap<Integer, Integer>();
	
	private LinkedHashMap<Integer, Integer> _alignmentToPdbResidueReferences = new LinkedHashMap<Integer, Integer>();
	
	private LinkedHashMap<Integer, Character> _pdbResidues = new LinkedHashMap<Integer, Character>();
	
	public void add(AlignmentReferenceItem item) {
		_pdbResidueToAlignmentReferences.put(item.getPdbResidueNumber(), item.getAlignmentIndex());
		_alignmentToPdbResidueReferences.put(item.getAlignmentIndex(), item.getPdbResidueNumber());
		_pdbResidues.put(item.getPdbResidueNumber(), item.getOneLetterResidueType());
	}
	
	public AlignmentReferenceItem getByPdbResidueNumber(int pdbResidueNumber) {
		if (_pdbResidueToAlignmentReferences.containsKey(pdbResidueNumber)) {
			return new AlignmentReferenceItem(
					pdbResidueNumber,
					_pdbResidueToAlignmentReferences.get(pdbResidueNumber),
					_pdbResidues.get(pdbResidueNumber));
		}
		
		return AlignmentReferenceItem.createEmptyItem();
	}
	
	public AlignmentReferenceItem getByAlignmentIndex(int alignmentIndex) {
		if (_alignmentToPdbResidueReferences.containsKey(alignmentIndex)) {
			int pdbResidueNumber = _alignmentToPdbResidueReferences.get(alignmentIndex);
			return new AlignmentReferenceItem(
					pdbResidueNumber,
					alignmentIndex,
					_pdbResidues.get(pdbResidueNumber));
		}
		
		return AlignmentReferenceItem.createEmptyItem();
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public int size() {
		return _pdbResidueToAlignmentReferences.size();
	}
}
