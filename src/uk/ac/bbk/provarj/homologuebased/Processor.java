package uk.ac.bbk.provarj.homologuebased;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.ResidueNumber;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileReader;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import uk.ac.bbk.provarj.ProteinStructure;
import uk.ac.bbk.provarj.alignment.AlignedProteinSequences;
import uk.ac.bbk.provarj.alignment.AlignmentReferenceItem;
import uk.ac.bbk.provarj.alignment.AlignmentReferences;
import uk.ac.bbk.provarj.alignment.StructureAlignmentReferenceMapper;
import uk.ac.bbk.provarj.pocket.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Processor {
    private File referenceStructureFile;
    private File homologueDirectory;
    private List<PocketProgramSpecification> pocketPrograms;
    private final File sequenceAlignmentFile;

    public Processor(String referenceStructurePath, String homologousStructurePath, String sequenceAlignmentFile, List<PocketProgramSpecification> pocketPrograms) {
        if (referenceStructurePath == null) {
            throw new IllegalArgumentException("referenceStructurePath is null");
        }

        if (referenceStructurePath.length() == 0) {
            throw new IllegalArgumentException("referenceStructurePath is empty string");
        }

        if (homologousStructurePath == null) {
            throw new IllegalArgumentException("homologousStructurePath is null");
        }

        if (homologousStructurePath.length() == 0) {
            throw new IllegalArgumentException("homologousStructurePath is empty string");
        }

        if (sequenceAlignmentFile == null) {
            throw new IllegalArgumentException("sequenceAlignmentFile is null");
        }

        if (sequenceAlignmentFile.length() == 0) {
            throw new IllegalArgumentException("sequenceAlignmentFile is empty string");
        }

        if (pocketPrograms == null) {
            throw new IllegalArgumentException("pocketPrograms is null");
        }

        if (pocketPrograms.size() == 0) {
            throw new IllegalArgumentException("At least one pocket program is required");
        }

        this.referenceStructureFile = new File(referenceStructurePath);
        if (!this.referenceStructureFile.exists()) {
            throw new IllegalArgumentException(String.format("Reference structure file '%1s' does not exist", referenceStructurePath));
        }

        if (!this.referenceStructureFile.isFile()) {
            throw new IllegalArgumentException(String.format("Path '%1s' is not a file", referenceStructurePath));
        }

        if (!this.referenceStructureFile.getName().endsWith(".pdb")) {
            throw new IllegalArgumentException(String.format("Reference structure file '%1s' is not a pdb file", referenceStructurePath));
        }

        if (this.referenceStructureFile.length() == 0) {
            throw new IllegalArgumentException(String.format("Reference structure file '%1s' is empty", referenceStructurePath));
        }


        this.homologueDirectory = new File(homologousStructurePath);
        if (!this.homologueDirectory.exists()) {
            throw new IllegalArgumentException(String.format("Homologous directory '%1s' does not exist", homologousStructurePath));
        }

        if (this.homologueDirectory.isFile()) {
            throw new IllegalArgumentException(String.format("Path '%1s' is not directory", homologousStructurePath));
        }

        this.sequenceAlignmentFile = new File(sequenceAlignmentFile);
        if (!this.sequenceAlignmentFile.exists()) {
            throw new IllegalArgumentException(String.format("Sequence alignment file '%1s' does not exist", sequenceAlignmentFile));
        }

        if (!this.sequenceAlignmentFile.isFile()) {
            throw new IllegalArgumentException(String.format("Path '%1s' is not file", sequenceAlignmentFile));
        }

        if (!this.sequenceAlignmentFile.getName().endsWith(".fasta")) {
            throw new IllegalArgumentException(String.format("Sequence alignment file '%1s' is not a fasta file", sequenceAlignmentFile));
        }

        if (this.sequenceAlignmentFile.length() == 0) {
            throw new IllegalArgumentException(String.format("Sequence alignment file '%1s' is empty", sequenceAlignmentFile));
        }

        this.pocketPrograms = pocketPrograms;
    }

    public void process() throws Exception {
        PDBFileReader reader = new PDBFileReader();
        ProteinStructure refStructure = loadReferenceStructure();

        AlignedProteinSequences alignedProteinSequences = loadAlignedProteinSequences();

        ProteinSequence refAlignedSequence = alignedProteinSequences.getAlignedSequence(refStructure);

        AlignmentReferences refStructureAlignmentReferences = StructureAlignmentReferenceMapper.map(refStructure, refAlignedSequence);
        if (refStructureAlignmentReferences.isEmpty()) {
            System.out.println("Invalid alignment for structure: " + refStructure.getPDBCode() + refStructure.getChain(0).getChainID());
            return;
        }

        Map<String, ProteinStructure> homologueStructures = loadHomologueStructures();
        Map<String, AlignmentReferences> homologueAlignments = mapHomologueAlignment(homologueStructures, alignedProteinSequences);
        removeHomologuesWithoutAlignment(homologueStructures, homologueAlignments);

        for (PocketProgramSpecification pocketProgram : pocketPrograms) {
            PocketPredictionLoader pocketPredictionLoader = PocketPredictionLoaderFactory.create(
                    pocketProgram.getPocketPredictionProgram(),
                    pocketProgram.getDirectory().getAbsolutePath());
            SortedMap<Integer, Double> residueAlignmentCounts = new TreeMap<Integer, Double>();
            SortedMap<Integer, Double> residuePocketLiningCounts = new TreeMap<Integer, Double>();
            SortedMap<Integer, Double> avgAtomPerResidueLiningCounts = new TreeMap<Integer, Double>();

            for (String key : homologueStructures.keySet()) {
                List<Structure> pocketStructures = pocketPredictionLoader.load(key);
                PocketStructure wrappedPocketStructure = new PocketStructure(pocketStructures);
                ProteinStructure structure = homologueStructures.get(key);
                PocketLiningData pocketLiningData = StructurePocketLinker.link(structure,
                        wrappedPocketStructure, 3.75, pocketProgram.isSearchSpaceLimitationRequired());

                AlignmentReferences alignmentReferences = homologueAlignments.get(key);

                for (Group group : structure.getAminoAcids()) {
                    AminoAcid aminoAcid = (AminoAcid) group;
                    ResidueNumber residueNumber = aminoAcid.getResidueNumber();
                    Integer seqNum = residueNumber.getSeqNum();
                    if (!residueAlignmentCounts.containsKey(seqNum)) {
                        residueAlignmentCounts.put(seqNum, 0.0);
                    }

                    if (!residuePocketLiningCounts.containsKey(seqNum)) {
                        residuePocketLiningCounts.put(seqNum, 0.0);
                    }

                    if (!avgAtomPerResidueLiningCounts.containsKey(seqNum)) {
                        avgAtomPerResidueLiningCounts.put(seqNum, 0.0);
                    }

                    AlignmentReferenceItem alignmentReferenceItem = alignmentReferences.getByPdbResidueNumber(seqNum);
                    if (!alignmentReferenceItem.isEmpty()) {
                        double updatedCount = residueAlignmentCounts.get(seqNum) + 1;
                        residueAlignmentCounts.put(seqNum, updatedCount);

                        AminoPocketLiningItem aminoPocketLiningItem = pocketLiningData.getPocketLiningAminoAcids().get(seqNum);
                        if (aminoPocketLiningItem.isPocketLining()) {
                            Double pocketLiningCount = residuePocketLiningCounts.get(seqNum) + 1;
                            residuePocketLiningCounts.put(seqNum, pocketLiningCount);

                            Double averagePocketLiningAtoms = avgAtomPerResidueLiningCounts.get(seqNum);
                            averagePocketLiningAtoms = averagePocketLiningAtoms + aminoPocketLiningItem.getAveragePocketLiningAtomCount();

                            avgAtomPerResidueLiningCounts.put(seqNum, averagePocketLiningAtoms);
                        }
                    }
                }
            }

            SortedMap<Integer, Double> aminoProbabilities = new TreeMap<Integer, Double>();
            for (Integer residueNumber : residueAlignmentCounts.keySet()) {
                Double alignmentCount = residueAlignmentCounts.get(residueNumber);
                if (alignmentCount == 0) {
                    continue;
                }

                Double pocketLiningCount = residuePocketLiningCounts.get(residueNumber);
                double probability = pocketLiningCount / alignmentCount;

                aminoProbabilities.put(residueNumber, probability);
            }
        }
    }

    private void removeHomologuesWithoutAlignment(Map<String, ProteinStructure> homologueStructures, Map<String, AlignmentReferences> homologueAlignments) {
        List<String> homologueWithoutAlignment = new ArrayList<String>();
        for (String key : homologueStructures.keySet()) {
            if (!homologueAlignments.containsKey(key)) {
                homologueWithoutAlignment.add(key);
            }
        }

        for (String key : homologueWithoutAlignment) {
            homologueStructures.remove(key);
        }
    }

    private Map<String, ProteinStructure> loadHomologueStructures() {
        Map<String, ProteinStructure> homologueStructures = new LinkedHashMap<String, ProteinStructure>();
        Path homologousStructureDirectoryPath = Paths.get(homologueDirectory.getAbsolutePath());
        try {
            DirectoryStream<Path> homologousStructureDirectory = Files.newDirectoryStream(homologousStructureDirectoryPath, "*.pdb");
            for (Path path : homologousStructureDirectory) {
                try
                {
                    if (Files.size(path) == 0) {
                        System.out.println("Empty structure file : " + path);
                        continue;
                    }

                    File file = path.toFile();
                    ProteinStructure homologueStructure = loadProteinStructure(file);

                    String fileName = file.getName();
                    String structureInitial = fileName.substring(0, fileName.length() - 4);
                    homologueStructures.put(structureInitial, homologueStructure);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return homologueStructures;
    }

    private Map<String, AlignmentReferences> mapHomologueAlignment(Map<String, ProteinStructure> homologueStructures, AlignedProteinSequences alignedProteinSequences) {
        Map<String, AlignmentReferences> homologueAlignments = new LinkedHashMap<String, AlignmentReferences>();
        for (String key : homologueStructures.keySet()) {
            ProteinStructure structure = homologueStructures.get(key);

            ProteinSequence homologousAlignedSequence = alignedProteinSequences.getAlignedSequence(structure);
            AlignmentReferences homologueAlignmentReferences
                    = StructureAlignmentReferenceMapper.map(structure, homologousAlignedSequence);
            if (homologueAlignmentReferences.isEmpty()) {
                System.out.println("Invalid alignment for structure: " + structure.getPDBCode() + structure.getChain(0).getChainID());
                continue;
            }

            homologueAlignments.put(key, homologueAlignmentReferences);
        }

        return homologueAlignments;
    }

    private AlignedProteinSequences loadAlignedProteinSequences() {
        LinkedHashMap<String, ProteinSequence> proteinSequences = null;
        try {
            proteinSequences = FastaReaderHelper.readFastaProteinSequence(this.sequenceAlignmentFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AlignedProteinSequences(proteinSequences);
    }

    private ProteinStructure loadReferenceStructure() throws Exception {
        try {
            return loadProteinStructure(referenceStructureFile);
        } catch (IOException e) {
            throw new Exception("Failed to load reference structure", e);
        }
    }

    private ProteinStructure loadProteinStructure(File file) throws IOException {
        PDBFileReader reader = new PDBFileReader();
        return new ProteinStructure(reader.getStructure(file));
    }
}
