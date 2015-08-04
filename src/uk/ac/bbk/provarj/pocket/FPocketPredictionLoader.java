package uk.ac.bbk.provarj.pocket;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FPocketPredictionLoader extends PocketPredictionLoader {
    private static final String directorySuffix = "_out";

    public FPocketPredictionLoader(String parentDirectory) {
        super(parentDirectory);
    }
    
    @Override
    public List<Structure> load(String structureInitial) throws PocketPredictionLoadException {
        String expectedDirectoryName = String.format("%1s%2s", structureInitial, directorySuffix);
        File structurePocketDirectory = new File(getPocketDirectoryPath(), expectedDirectoryName);
        if (!structurePocketDirectory.exists()) {
            throw new PocketPredictionLoadException(structureInitial,
                    new FileNotFoundException(
                            String.format("Pocket prediction directory does not exist in %1s", structurePocketDirectory.getAbsolutePath())));
        }

        File structurePocketSubDirectory = new File(structurePocketDirectory, "pockets");
        if (!structurePocketSubDirectory.exists()) {
            throw new PocketPredictionLoadException(structureInitial,
                    new FileNotFoundException(
                            String.format("Pocket prediction sub-directory does not exist in %1s", structurePocketSubDirectory.getAbsolutePath())));
        }

        PDBFileReader reader = new PDBFileReader();
        List<Structure> structures = new ArrayList<Structure>();
        for(File pocketFile : structurePocketSubDirectory.listFiles()) {
            if (!pocketFile.isFile() || !pocketFile.getName().endsWith(".pdb")) {
                continue;
            }

            try {
                Structure structure = reader.getStructure(pocketFile);
                structures.add(structure);
            } catch (IOException e) {
                throw new PocketPredictionLoadException(structureInitial, e);
            }
        }

        return structures;
    }
}
