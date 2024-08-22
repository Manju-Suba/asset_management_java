package assetmanagement.enumData;

import lombok.Getter;

@Getter
public enum TransferStatus {
    Created("Created"),
    Rejected("Rejected"),
    Approved("Approved"),
    Pending("Pending");
    private final String value;
    TransferStatus(String value) {
        this.value = value;
    }
}
