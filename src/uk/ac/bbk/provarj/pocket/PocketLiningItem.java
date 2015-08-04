package uk.ac.bbk.provarj.pocket;

public class PocketLiningItem {
	public int serialNumber;
	public String componentName;
	public boolean pocketLining;
	
	public PocketLiningItem(
			int serialNumber,
			String componentName,
			boolean pocketLining) {
		this.serialNumber = serialNumber;
		this.componentName = componentName;
		this.pocketLining = pocketLining;
	}
	
	/**
	 * @return the serialNumber
	 */
	public int getSerialNumber() {
		return serialNumber;
	}
	
	/**
	 * @return the componentName
	 */
	public String getComponentName() {
		return componentName;
	}
	
	/**
	 * @return the pocketLining
	 */
	public boolean isPocketLining() {
		return pocketLining;
	}
	
	/**
	 * @param pocketLining the pocketLining to set
	 */
	public void setPocketLining(boolean pocketLining) {
		this.pocketLining = pocketLining;
	}
}