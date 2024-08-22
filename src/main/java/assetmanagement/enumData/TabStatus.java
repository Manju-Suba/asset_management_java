package assetmanagement.enumData;

import lombok.Getter;

@Getter
public enum TabStatus {

    PendingAuditor("PendingAuditor"),
    WaitingForAdminApproval("WaitingForAdminApproval"),
    DisposedAuditor("DisposedAuditor"),
    ApprovedAuditor("ApprovedAuditor");
    private final String value;
    TabStatus(String value) {
        this.value = value;
    }
}
