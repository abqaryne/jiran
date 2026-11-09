package com.jiran.app.domain.auth;

/** FR2.1/FR1.1: a Post form belongs to one verified Siswa Mail account. Rules remain authoritative. */
public final class VerifiedHostPolicy {
    private VerifiedHostPolicy() { }

    public static boolean permits(String formUid, String currentUid, String email, boolean verified) {
        return formUid != null && !formUid.isEmpty() && formUid.equals(currentUid)
                && verified && UmEmailPolicy.isUmEmail(email);
    }
}
