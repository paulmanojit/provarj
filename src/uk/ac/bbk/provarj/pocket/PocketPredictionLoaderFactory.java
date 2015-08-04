package uk.ac.bbk.provarj.pocket;

public class PocketPredictionLoaderFactory {

    public static PocketPredictionLoader create(PocketPredictionProgram program, String parentDirectory) {
        PocketPredictionLoader loader = null;
        switch (program) {
            case LIGSITE:
                loader = new LigsitePocketPredictionLoader(parentDirectory);
                break;
            case FPOCKET:
                loader = new FPocketPredictionLoader(parentDirectory);
                break;
            case PASS:
                loader = new PassPocketPredictionLoader(parentDirectory);
                break;
        }

        return loader;
    }
}
