package assetmanagement.enumData;

import lombok.Getter;

@Getter
public enum YesOrNo {

    Yes("Yes"),
    No("No");
	private final String value;
	YesOrNo(String value) {
        this.value = value;
    }
}
