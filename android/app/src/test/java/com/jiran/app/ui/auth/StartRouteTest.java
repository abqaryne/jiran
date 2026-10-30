package com.jiran.app.ui.auth;

import static org.junit.Assert.assertEquals;

import com.jiran.app.data.AccountService.Session;
import org.junit.Test;

public final class StartRouteTest {
    @Test
    public void anUnverifiedAccountCanOnlyReachVerifyEmail() {
        assertEquals(StartRoute.VERIFY_EMAIL, StartRoute.of(Session.UNVERIFIED));
    }

    @Test
    public void signedOutGoesToWelcomeAndVerifiedGoesToTheProduct() {
        assertEquals(StartRoute.WELCOME, StartRoute.of(Session.SIGNED_OUT));
        assertEquals(StartRoute.PRODUCT, StartRoute.of(Session.VERIFIED));
    }
}
