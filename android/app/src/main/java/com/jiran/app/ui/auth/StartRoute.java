package com.jiran.app.ui.auth;

import com.jiran.app.data.AccountService.Session;

/**
 * Where the app opens. Anyone who is not signed in sees Welcome; a signed-in but unverified account can only reach
 * Verify email (FR1.1); a verified account goes straight to the product.
 */
enum StartRoute {
    WELCOME, VERIFY_EMAIL, PRODUCT;

    static StartRoute of(Session session) {
        switch (session) {
            case VERIFIED:
                return PRODUCT;
            case UNVERIFIED:
                return VERIFY_EMAIL;
            default:
                return WELCOME;
        }
    }
}
