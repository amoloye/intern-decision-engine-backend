package ee.taltech.inbankbackend.entity;

public enum Segmentation {
    DEBT(0),
    SEG_ONE(100),
    SEG_TWO(300),
    SEG_THREE(1000);

    private final int value;

    Segmentation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Segmentation fromPersonalCode(String personalCode) {
        int segment = Integer.parseInt(personalCode.substring(personalCode.length() - 4));

        if (segment < 2500) return DEBT;
        if (segment < 5000) return SEG_ONE;
        if (segment < 7500) return SEG_TWO;
        return SEG_THREE;
    }
}
