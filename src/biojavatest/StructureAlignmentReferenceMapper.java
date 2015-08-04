package biojavatest;

import java.util.List;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.ResidueNumber;
import org.biojava3.core.sequence.ProteinSequence;

public class StructureAlignmentReferenceMapper {
	public static AlignmentReferences map(ProteinStructure proteinStructure, ProteinSequence alignment) {
		if (proteinStructure == null) {
			throw new IllegalArgumentException("proteinStructure is null");
		}
		
		if (alignment == null) {
			throw new IllegalArgumentException("alignment is null");
		}
		
		List<Chain> chains = proteinStructure.getChains();
		if (chains.isEmpty()) {
			throw new IllegalArgumentException("proteinStructure does not have any chain");
		}
		
		if (chains.size() > 1) {
			throw new IllegalArgumentException("proteinStructure has more than chain");
		}

        AlignmentReferences references = new AlignmentReferences();
        			
		String proteinSeqAsString = alignment.getSequenceAsString();
		
		List<Group> atomGroups = proteinStructure.getAminoAcids();
		int startIndex = 0;
		for (Group group : atomGroups) {
			AminoAcid aminoAcid = (AminoAcid) group;
			Character aminoType = aminoAcid.getAminoType();
			ResidueNumber residueNumber = aminoAcid.getResidueNumber();
			
			int matchIndex = proteinSeqAsString.indexOf(aminoType, startIndex);
			if (matchIndex != -1) {
				startIndex = matchIndex + 1;
				AlignmentReferenceItem item = new AlignmentReferenceItem(residueNumber.getSeqNum(), startIndex, aminoType);
				references.add(item);
			}
		}
		
		return references;
	}
}
