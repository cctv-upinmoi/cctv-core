package init.upinmcse.cctvcore.model.enums;

/**
 * Java Enum which represents streaming status for particular camera.
 */
public enum Status {

    ACTIVE(1),
    IN_ACTIVE(2);

    private final Integer value;

    Status(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
