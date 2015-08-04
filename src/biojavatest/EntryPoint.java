package biojavatest;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileReader;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class EntryPoint {

	public static void main(String[] args) {
		
		PDBFileReader reader = new PDBFileReader();
		ProteinStructure refStructure = null;
		try {
			Structure refPdbStructure = reader.getStructure("/Birkbeck/Project/Provar_v4/example2_homologues/2B5IA.pdb");
			refStructure = new ProteinStructure(refPdbStructure);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LinkedHashMap<String, ProteinSequence> proteinSequences = null;
		try {
	        proteinSequences = FastaReaderHelper.readFastaProteinSequence(new File("/Birkbeck/Project/Provar_v4/example2_homologues/Mustang_alignment_apo_holo_receptor.fasta"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AlignedProteinSequences alignedProteinSequences = new AlignedProteinSequences(proteinSequences);
		ProteinSequence refAlignedSequence = alignedProteinSequences.getAlignedSequence(refStructure);
		
		AlignmentReferences refStructureAlignmentReferences = StructureAlignmentReferenceMapper.map(refStructure, refAlignedSequence);
		if (refStructureAlignmentReferences.isEmpty()) {
			System.out.println("Invalid alignment for structure: " + refStructure.getPDBCode() + refStructure.getChain(0).getChainID());
	    	return;
	    }
		
		Path homologousStructureDirectoryPath = Paths.get("/Birkbeck/Project/Provar_v4/example2_homologues/structures/");
		try {
			DirectoryStream<Path> homologousStructureDirectory = Files.newDirectoryStream(homologousStructureDirectoryPath, "*.pdb");
			for (Path path : homologousStructureDirectory) {
				try
				{
					if (Files.size(path) == 0) {
						System.out.println("Empty structure file : " + path);
						continue;
					}
					
				    Structure homologousPdbStructure = reader.getStructure(path.toFile());
				    ProteinStructure homologousStructure = new ProteinStructure(homologousPdbStructure);
				    ProteinSequence homologousAlignedSequence = alignedProteinSequences.getAlignedSequence(homologousStructure);
				    AlignmentReferences homologousStructureAlignmentReferences
				        = StructureAlignmentReferenceMapper.map(homologousStructure, homologousAlignedSequence);
				    if (homologousStructureAlignmentReferences.isEmpty()) {
				    	System.out.println("Invalid alignment for structure: " + homologousStructure.getPDBCode() + homologousStructure.getChain(0).getChainID());
				    	continue;
				    }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
