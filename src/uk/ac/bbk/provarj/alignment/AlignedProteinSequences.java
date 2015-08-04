package uk.ac.bbk.provarj.alignment;

import java.util.LinkedHashMap;
import java.util.List;

import org.biojava.bio.structure.Chain;
import org.biojava3.core.sequence.ProteinSequence;
import uk.ac.bbk.provarj.ProteinStructure;

public class AlignedProteinSequences {
	private LinkedHashMap<String, ProteinSequence> proteinSequences = null;
	
	/**
	 * 
	 * @param proteinSequences
	 */
	public AlignedProteinSequences(LinkedHashMap<String, ProteinSequence> proteinSequences) {
		if (proteinSequences == null) {
			throw new IllegalArgumentException("proteinSequences is null");
		}
		
		if (proteinSequences.isEmpty()) {
			throw new IllegalArgumentException("proteinSequences is empty");
		}
		
		this.proteinSequences = proteinSequences;
	}
	
	public ProteinSequence getAlignedSequence(ProteinStructure proteinStructure) {
		if (proteinStructure == null) {
			throw new IllegalArgumentException("proteinStructure is null");
		}
		
		List<Chain> chains = proteinStructure.getChains();
		if (chains.isEmpty()) {
			throw new IllegalArgumentException("proteinStructure does not have any chain");
		}
		
		if (chains.size() > 1) {
			throw new IllegalArgumentException("proteinStructure has more than chain");
		}
		
		Chain chain = chains.get(0);
		String pdbCodeWithChain = proteinStructure.getPDBCode() + chain.getChainID() + ".pdb";
		
		return proteinSequences.get(pdbCodeWithChain);
	}
}
