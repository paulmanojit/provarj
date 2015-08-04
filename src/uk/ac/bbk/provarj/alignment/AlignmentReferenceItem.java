package uk.ac.bbk.provarj.alignment;

public class AlignmentReferenceItem {
	private int pdbResidueNumber;
	private int alignmentIndex;
	private char oneLetterResidueType;

	/**
	 * 
	 * @param pdbResidueNumber
	 * @param alignmentIndex
	 * @param oneLetterResidueType
	 */
	public AlignmentReferenceItem(int pdbResidueNumber,
			int alignmentIndex,
			char oneLetterResidueType) {
		this.pdbResidueNumber = pdbResidueNumber;
		this.alignmentIndex = alignmentIndex;
		this.oneLetterResidueType = oneLetterResidueType;
	}

	/**
	 * @return the pdbResidueNumber
	 */
	public int getPdbResidueNumber() {
		return pdbResidueNumber;
	}

	/**
	 * @return the alignmentIndex
	 */
	public int getAlignmentIndex() {
		return alignmentIndex;
	}

	/**
	 * @return the oneLetterResidueType
	 */
	public char getOneLetterResidueType() {
		return oneLetterResidueType;
	}
	
	/**
	 * 
	 * @return
	 */
	public static AlignmentReferenceItem createEmptyItem() {
		return new AlignmentReferenceItem(-1, -1, '*');
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return pdbResidueNumber == -1 || alignmentIndex == -1 || oneLetterResidueType == '*';
	}
}