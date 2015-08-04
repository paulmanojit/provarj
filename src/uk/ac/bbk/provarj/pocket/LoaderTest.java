package uk.ac.bbk.provarj.pocket;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public class LoaderTest {
    public static void main(String[] args) {
        List<PocketPredictionLoader> loaders = new ArrayList<PocketPredictionLoader>();
        loaders.add(PocketPredictionLoaderFactory.create(PocketPredictionProgram.LIGSITE, "/Birkbeck/Project/Provar_v4/example2_homologues/Ligsite_pocket_files/"));
        loaders.add(PocketPredictionLoaderFactory.create(PocketPredictionProgram.FPOCKET, "/Birkbeck/Project/Provar_v4/example2_homologues/fPocket_pocket_files/"));
        loaders.add(PocketPredictionLoaderFactory.create(PocketPredictionProgram.PASS, "/Birkbeck/Project/Provar_v4/example2_homologues/PASS_pocket_files/"));

        List<List<Structure>> structuresPerLoader = new ArrayList<List<Structure>>();
        try {
            for (PocketPredictionLoader loader : loaders) {
                List<Structure> structures = loader.load("1BGCA");
                structuresPerLoader.add(structures);
            }
        } catch (PocketPredictionLoadException e) {
            e.printStackTrace();
        }

        for (List<Structure> structures : structuresPerLoader) {
            PocketStructure pocketStructure = new PocketStructure(structures);
            List<Atom> atoms = pocketStructure.getAtoms();
            System.out.println("Printing atoms");
            for (Atom atom : atoms) {
                System.out.println(atom.getPDBserial() + " " + atom.getX() + " " + atom.getY() + " " + atom.getZ());
            }
        }
    }
}
