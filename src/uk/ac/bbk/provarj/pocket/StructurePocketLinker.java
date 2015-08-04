package uk.ac.bbk.provarj.pocket;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.ac.bbk.provarj.ProteinStructure;
import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Calc;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.ResidueNumber;
import org.biojava.bio.structure.StructureException;

public class StructurePocketLinker {
	public static PocketLiningData link(
			ProteinStructure proteinStructure,
			PocketStructure pocketStructure,
			double pocketRadius,
			boolean searchSpaceLimitationRequired) {
		if (proteinStructure == null) {
			throw new IllegalArgumentException("proteinStructure is null");
		}
		
		if (pocketStructure == null) {
			throw new IllegalArgumentException("pocketStructure is null");
		}
		
		List<Chain> chains = proteinStructure.getChains();
		if (chains.isEmpty()) {
			throw new IllegalArgumentException("proteinStructure does not have any chain");
		}
		
		if (chains.size() > 1) {
			throw new IllegalArgumentException("proteinStructure has more than chain");
		}
		
		SortedMap<Integer, AtomPocketLiningItem> pocketLiningAtoms = new TreeMap<Integer, AtomPocketLiningItem>();
		SortedMap<Integer, AminoPocketLiningItem> pocketLiningAminoAcids = new TreeMap<Integer, AminoPocketLiningItem>();
		
		List<Atom> atoms = proteinStructure.getAtoms();
		for (Atom atom : atoms) {
			Group group = atom.getGroup();
			if (!group.getType().equals("amino")) {
				continue;
			}

			AminoAcid aminoAcid = (AminoAcid) atom.getGroup();
			boolean isAtomPocketLining = checkAtomPocketLining(atom,
					pocketStructure, pocketRadius,
					searchSpaceLimitationRequired);

			AtomPocketLiningItem atomPocketLiningItem = new AtomPocketLiningItem(
					atom.getPDBserial(), atom.getName(), isAtomPocketLining);
			pocketLiningAtoms.put(atom.getPDBserial(), atomPocketLiningItem);

			ResidueNumber residueNumber = aminoAcid.getResidueNumber();
			if (!pocketLiningAminoAcids.containsKey(residueNumber.getSeqNum())) {
				AminoPocketLiningItem aminoPocketLiningItem
				    = new AminoPocketLiningItem(
				    		residueNumber.getSeqNum(),
				    		aminoAcid.getAminoType().toString(),
				    		aminoAcid.getAtoms().size());
				
				pocketLiningAminoAcids.put(residueNumber.getSeqNum(), aminoPocketLiningItem);
			}
			
			if (isAtomPocketLining) {
				AminoPocketLiningItem aminoPocketLiningItem = pocketLiningAminoAcids.get(residueNumber.getSeqNum());
				aminoPocketLiningItem.setPocketLining(true);
				aminoPocketLiningItem.increasePocketLiningAtomCount();
			}
		}
		
		return new PocketLiningData(pocketLiningAtoms, pocketLiningAminoAcids);
	}

	private static boolean checkAtomPocketLining(Atom atom,
			PocketStructure pocketStructure, double pocketRadius,
			boolean searchSpaceLimitationRequired) {
		if (searchSpaceLimitationRequired) {
			return checkBasedOnSearchSpaceLimitation(atom, pocketStructure, pocketRadius);
		}
		
		return checkBasedOnPrediction(atom, pocketStructure);
	}

	private static boolean checkBasedOnPrediction(
			Atom atom,
			PocketStructure pocketStructure) {
		
		int atomSerialNo = atom.getPDBserial();
		Atom pocketAtom = pocketStructure.getAtom(atomSerialNo);
		
		return pocketAtom != null;
	}

	private static boolean checkBasedOnSearchSpaceLimitation(
			Atom originalAtom,
			PocketStructure pocketStructure,
			double pocketRadius) {
		List<Atom> searchSpaceAtoms = applySearchSpaceLimits(originalAtom, pocketStructure.getAtoms(), pocketRadius);
		
		for (Atom searchSpaceAtom : searchSpaceAtoms) {
			try {
				double distance = Calc.getDistance(originalAtom, searchSpaceAtom);
				if (distance < pocketRadius) {
					return true;
				}
			} catch (StructureException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	private static List<Atom> applySearchSpaceLimits(Atom originalAtom,
			List<Atom> pocketAtoms, double pocketRadius) {
		//List<double[]> searchSpaceCoords = new ArrayList<double[]>();
		List<Atom> searchSpaceAtoms = new ArrayList<Atom>();
		
		double[] originalAtomCoords = originalAtom.getCoords();
		
		// Ensure that the edges of the search space included
		double radius = pocketRadius * 1.01;

		// Search for atoms that falls within the radius along the XYZ planes in
		// both positive and negative direction
		// If they do add them to a list of potential pocket lining atoms
		for (Atom pocketAtom : pocketAtoms) {
			double[] pocketCoords = pocketAtom.getCoords();
			// Calculate distances between the location of the atom on the
			// protein and all the pocket lining atoms
			if ((pocketCoords[0] < (originalAtomCoords[0] + radius))
					&& (pocketCoords[1] < (originalAtomCoords[1] + radius))
					&& (pocketCoords[2] < (originalAtomCoords[2] + radius))) {
				searchSpaceAtoms.add(pocketAtom);
			}

			if ((pocketCoords[0] > (originalAtomCoords[0] - radius))
					&& (pocketCoords[1] > (originalAtomCoords[1] - radius))
					&& (pocketCoords[2] > (originalAtomCoords[2] - radius))) {
				searchSpaceAtoms.add(pocketAtom);
			}
		}
		
		// Return the list that represents all the atoms that
		// fall within the pocket radius
		return searchSpaceAtoms;
	}
}