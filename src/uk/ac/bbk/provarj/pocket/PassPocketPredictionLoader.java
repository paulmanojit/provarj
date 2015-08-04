package uk.ac.bbk.provarj.pocket;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PassPocketPredictionLoader extends PocketPredictionLoader {
    private static final String fileSuffix = "_probes.pdb";

    public PassPocketPredictionLoader(String parentDirectory) {
        super(parentDirectory);
    }

    @Override
    public List<Structure> load(String structureInitial) throws PocketPredictionLoadException {
        String expectedFileName = String.format("%1s%2s", structureInitial, fileSuffix);
        File pocketFile = new File(getPocketDirectoryPath(), expectedFileName);
        if (!pocketFile.exists()) {
            throw new PocketPredictionLoadException(structureInitial,
                    new FileNotFoundException(String.format("Pocket prediction file does not exist in the path %1s", pocketFile.getAbsolutePath())));
        }

        PDBFileReader pdbReader = new PDBFileReader();
        try {
            Structure structure = pdbReader.getStructure(pocketFile);
            ArrayList<Structure> structures = new ArrayList<Structure>();
            structures.add(structure);
            return Collections.unmodifiableList(structures);
        } catch (IOException e) {
            throw new PocketPredictionLoadException(structureInitial, e);
        }
    }
}
