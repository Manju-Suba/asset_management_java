package assetmanagement.response;

import javax.crypto.SecretKey;

public class ResetLinkAndKey {
    private String resetLink;
    private SecretKey secretKey;

    public ResetLinkAndKey(String resetLink, SecretKey secretKey) {
        this.resetLink = resetLink;
        this.secretKey = secretKey;
    }

    public String getResetLink() {
        return resetLink;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
