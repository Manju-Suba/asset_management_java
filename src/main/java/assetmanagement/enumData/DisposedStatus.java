package assetmanagement.enumData;

import lombok.Getter;

@Getter
public enum DisposedStatus {
    Created("Created"),
    Disposed("Disposed"),
    Renewed("Renewed"),
    Replaced("Replaced"),
    ClosedToDispose("Close To Dispose"),
    Pending("Pending");
    private final String value;
    DisposedStatus(String value) {
        this.value = value;
    }

}
