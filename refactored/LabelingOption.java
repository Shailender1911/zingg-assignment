package zingg.labeling;

public enum LabelingOption {
    
    NOT_A_MATCH(0),
    MATCH(1),
    NOT_SURE(2),
    QUIT(9);

    private final int value;

    LabelingOption(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LabelingOption fromValue(int val) {
        for (LabelingOption opt : values()) {
            if (opt.value == val) {
                return opt;
            }
        }
        throw new IllegalArgumentException("Invalid option: " + val);
    }

    public static boolean isValid(String input) {
        if (input == null || input.length() != 1) return false;
        char c = input.charAt(0);
        return c == '0' || c == '1' || c == '2' || c == '9';
    }
}

