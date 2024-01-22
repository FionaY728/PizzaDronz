package uk.ac.ed.inf.Constant;
    /**
     * Enum representing compass directions with corresponding degree values.
     * Each direction is associated with a specific angle value in degrees.
     */
public enum CompassDirection {
    E(0),
    ENE(22.5),
    NE(45),
    NNE(67.5),
    N(90),
    NNW(112.5),
    NW(135),
    WNW(157.5),
    W(180),
    WSW(202.5),
    SW(225),
    SSW(247.5),
    S(270),
    SSE(292.5),
    SE(315),
    ESE(337.5);

    private final double value;
    /**
     * Constructs a compass direction with the specified degree value.
     *
     * @param value The degree value associated with the compass direction.
     */
    CompassDirection(double value) {
        this.value = value;
    }
    /**
     * Gets the degree value associated with the compass direction.
     *
     * @return The degree value.
     */
    public double getVal() {
        return value;
    }

}

