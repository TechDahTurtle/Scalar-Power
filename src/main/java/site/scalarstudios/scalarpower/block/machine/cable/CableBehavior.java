package site.scalarstudios.scalarpower.block.machine.cable;

public enum CableBehavior {
    INPUT_OUTPUT("Input/Output"),
    INPUT("Input"),
    OUTPUT("Output"),
    DISABLED("Disabled");

    private final String displayName;

    CableBehavior(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public CableBehavior next() {
        CableBehavior[] values = CableBehavior.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public static CableBehavior fromOrdinal(int ordinal) {
        CableBehavior[] values = CableBehavior.values();
        return values[ordinal % values.length];
    }
}

