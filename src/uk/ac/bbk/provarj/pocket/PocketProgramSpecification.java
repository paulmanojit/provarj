package uk.ac.bbk.provarj.pocket;

import java.io.File;

public class PocketProgramSpecification {
    private PocketPredictionProgram pocketPredictionProgram;
    private File directory;
    private boolean searchSpaceLimitationRequired;

    public PocketProgramSpecification(
            PocketPredictionProgram pocketPredictionProgram,
            String directory,
            boolean searchSpaceLimitationRequired) {
        if (directory == null) {
            throw new IllegalArgumentException("directory is null");
        }

        if (directory.length() == 0) {
            throw new IllegalArgumentException("directory is empty string");
        }

        this.directory = new File(directory);
        if (!this.directory.exists()) {
            throw new IllegalArgumentException(String.format("Pocket program directory '%1s' does not exist", directory));
        }

        if (this.directory.isFile()) {
            throw new IllegalArgumentException(String.format("Pocket program path '%1s' is not a directory", directory));
        }

        this.pocketPredictionProgram = pocketPredictionProgram;
        this.searchSpaceLimitationRequired = searchSpaceLimitationRequired;
    }

    public PocketPredictionProgram getPocketPredictionProgram() {
        return pocketPredictionProgram;
    }

    public File getDirectory() {
        return directory;
    }

    public boolean isSearchSpaceLimitationRequired() {
        return searchSpaceLimitationRequired;
    }
}
