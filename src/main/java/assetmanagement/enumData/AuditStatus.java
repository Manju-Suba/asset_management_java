package assetmanagement.enumData;

import lombok.Getter;

@Getter
public enum AuditStatus {
    Created("Created"),
    Completed("Completed"),
    Disposed("Disposed"),
    Approved("Approved"),
    Waiting("Waiting"),
    Rejected("Rejected"),
    Pending("Pending");

    private final String value;

    AuditStatus(String value) {
        this.value = value;
    }
}
