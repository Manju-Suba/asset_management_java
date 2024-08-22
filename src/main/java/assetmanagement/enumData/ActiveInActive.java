package assetmanagement.enumData;

import lombok.Getter;

@Getter
public enum ActiveInActive {

	INACTIVE("Inactive"),
    EXPIRED("Expired"),
    DELETED("Deleted"),
    UPDATED("Updated"),
    ACTIVE("Active");
	private final String value;
	ActiveInActive(String value) {
        this.value = value;
    }
}
