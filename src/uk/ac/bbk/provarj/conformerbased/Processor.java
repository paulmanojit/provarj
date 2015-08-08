package uk.ac.bbk.provarj.conformerbased;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileReader;
import uk.ac.bbk.provarj.ProteinStructure;
import uk.ac.bbk.provarj.pocket.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

public class Processor {
    private File referenceStructureFile;
    private File conformersDirectory;
    private List<PocketProgramSpecification> pocketPrograms;

    public Processor(String referenceStructurePath, String conformersDirectoryPath, List<PocketProgramSpecification> pocketPrograms) {
        if (referenceStructurePath == null) {
            throw new IllegalArgumentException("referenceStructurePath is null");
        }

        if (referenceStructurePath.length() == 0) {
            throw new IllegalArgumentException("referenceStructurePath is empty string");
        }

        if (conformersDirectoryPath == null) {
            throw new IllegalArgumentException("conformersDirectoryPath is null");
        }

        if (conformersDirectoryPath.length() == 0) {
            throw new IllegalArgumentException("conformersDirectoryPath is empty string");
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


        this.conformersDirectory = new File(conformersDirectoryPath);
        if (!this.conformersDirectory.exists()) {
            throw new IllegalArgumentException(String.format("Conformers directory '%1s' does not exist", conformersDirectoryPath));
        }

        if (this.conformersDirectory.isFile()) {
            throw new IllegalArgumentException(String.format("Path '%1s' is not directory", conformersDirectoryPath));
        }

        this.pocketPrograms = pocketPrograms;
    }

    public void process() throws Exception {
        ProteinStructure refStructure = loadReferenceStructure();
        Map<String, ProteinStructure> conformerStructures = loadConformerProteinStructures();
        for (PocketProgramSpecification pocketProgram : pocketPrograms) {
            PocketPredictionLoader pocketPredictionLoader = PocketPredictionLoaderFactory.create(
                    pocketProgram.getPocketPredictionProgram(),
                    pocketProgram.getDirectory().getAbsolutePath());
            ArrayList<PocketLiningData> allStructureLiningData = new ArrayList<PocketLiningData>();
            for (String key : conformerStructures.keySet()) {
                List<Structure> pocketStructures = pocketPredictionLoader.load(key);
                PocketStructure wrappedPocketStructure = new PocketStructure(pocketStructures);
                ProteinStructure conformerStructure = conformerStructures.get(key);
                PocketLiningData pocketLiningData = StructurePocketLinker.link(conformerStructure,
                        wrappedPocketStructure, 3.75, pocketProgram.isSearchSpaceLimitationRequired());
                allStructureLiningData.add(pocketLiningData);
            }

            PocketLiningStatsContainer pocketLiningStatsContainer = new PocketLiningStatsContainer(allStructureLiningData);
            int structureCount = pocketLiningStatsContainer.getStructureCount();
            ProbabilityCalculator atomProbabilityCalculator = new ProbabilityCalculator(pocketLiningStatsContainer.getAtomCounts(), structureCount);
            SortedMap<Integer, Double> atomProbabilities = atomProbabilityCalculator.getProbabilities();

            ProbabilityCalculator aminoProbabilityCalculator = new ProbabilityCalculator(pocketLiningStatsContainer.getAminoCounts(), structureCount);
            SortedMap<Integer, Double> aminoProbabilities = aminoProbabilityCalculator.getProbabilities();

            ProbabilityCalculator averageAtomsProbabilityCalculator = new ProbabilityCalculator(pocketLiningStatsContainer.getAverageAtomsPerAminoAcid(), structureCount);
            SortedMap<Integer, Double> averageAtomsProbabilities = averageAtomsProbabilityCalculator.getProbabilities();
        }
    }

    private Map<String, ProteinStructure> loadConformerProteinStructures() {
        File[] pdbFiles = conformersDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("*.pdb");
            }
        });

        Map<String, ProteinStructure> proteinStructures = new LinkedHashMap<String, ProteinStructure>();
        for (File pdbFile : pdbFiles) {
            try {
                ProteinStructure structure = loadProteinStructure(pdbFile);
                String fileName = pdbFile.getName();
                String structureInitial = fileName.substring(0, fileName.length() - 4);
                proteinStructures.put(structureInitial, structure);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return proteinStructures;
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
