package uk.ac.bbk.provarj.pocket;

public class PocketPredictionLoadException extends Exception {

    public PocketPredictionLoadException(String structureName) {
        this(structureName, null);
    }

    public PocketPredictionLoadException(String structureName, Throwable innerException) {
        super(String.format("Failed to load pocket prediction data for structure: %1s", structureName), innerException);
    }
}
