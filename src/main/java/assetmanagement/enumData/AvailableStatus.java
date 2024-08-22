package assetmanagement.enumData;

import lombok.Getter;

@Getter
public enum AvailableStatus {

	Scrap("Scrapped"),
	Rejected("Rejected"),
	Damaged("Damaged"),
	Retrial("Retrial"),
	Stock("Stock"),
	Allocate("Allocate"),
	Waiting("Waiting"),
	Online("Online"),
	Offline("Offline"),
	Maintenance("Maintenance");
	private final String value;

	AvailableStatus(String value) {
		this.value = value;
	}
}
