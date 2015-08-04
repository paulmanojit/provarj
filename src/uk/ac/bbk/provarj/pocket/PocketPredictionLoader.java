package uk.ac.bbk.provarj.pocket;

import org.biojava.bio.structure.Structure;

import java.util.List;

public abstract class PocketPredictionLoader {
	private String pocketDirectoryPath;
	
	public PocketPredictionLoader(String parentDirectoryPath) {
		this.pocketDirectoryPath = parentDirectoryPath;
	}
	
	public abstract List<Structure> load(String structureInitial) throws PocketPredictionLoadException;

	/**
	 * @return the pocketDirectoryPath
	 */
	public String getPocketDirectoryPath() {
		return pocketDirectoryPath;
	}
}
