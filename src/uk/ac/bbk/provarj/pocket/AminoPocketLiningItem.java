package uk.ac.bbk.provarj.pocket;

public class AminoPocketLiningItem extends PocketLiningItem {

	private int atomCount;
	private int pocketLiningAtomCount = 0;
	
	public AminoPocketLiningItem(int serialNumber, String componentName, int atomCount) {
		
		super(serialNumber, componentName, false);
		this.atomCount = atomCount;
	}

	/**
	 * @return the atomCount
	 */
	public int getAtomCount() {
		return atomCount;
	}

	/**
	 * @return the pocketLiningAtomCount
	 */
	public int getPocketLiningAtomCount() {
		return pocketLiningAtomCount;
	}

	/**
	 * 
	 */
	public void increasePocketLiningAtomCount() {
		pocketLiningAtomCount++;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getAveragePocketLiningAtomCount() {
		return (double) pocketLiningAtomCount/atomCount;
	}
}
