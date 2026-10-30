package com.jiran.app.ui.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.AccountService.AccountError;
import com.jiran.app.ui.auth.VerifyEmailViewModel.Resend;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class VerifyEmailViewModelTest {
    @Rule
    public final InstantTaskExecutorRule instantTasks = new InstantTaskExecutorRule();

    private FakeAccountService accounts;
    private VerifyEmailViewModel viewModel;

    @Before
    public void setUp() {
        accounts = new FakeAccountService();
        viewModel = new VerifyEmailViewModel(accounts);
    }

    @Test
    public void showsTheAddressTheLinkWasSentTo() {
        assertEquals("meiling@siswa.um.edu.my", viewModel.email());
    }

    @Test
    public void becomesVerifiedOnlyWhenTheServerSaysSo() {
        viewModel.checkVerified();
        accounts.pendingCheck.onResult(false);
        assertFalse(viewModel.verified().getValue());

        viewModel.checkVerified();
        accounts.pendingCheck.onResult(true);
        assertTrue(viewModel.verified().getValue());
    }

    @Test
    public void overlappingChecksAreDroppedAndAVerifiedAccountIsNotChecked() {
        viewModel.checkVerified();
        viewModel.checkVerified();
        assertEquals(1, accounts.checkCalls);
        accounts.pendingCheck.onResult(true);
        viewModel.checkVerified();
        assertEquals(1, accounts.checkCalls);
    }

    @Test
    public void aFailedCheckCanBeTriedAgain() {
        viewModel.checkVerified();
        accounts.pendingCheck.onError(AccountError.NETWORK);
        assertFalse(viewModel.verified().getValue());
        viewModel.checkVerified();
        assertEquals(2, accounts.checkCalls);
    }

    @Test
    public void resendReportsSentOrFailedAndIgnoresDoubleTaps() {
        viewModel.resendLink();
        viewModel.resendLink();
        assertEquals(1, accounts.resendCalls);
        assertEquals(Resend.SENDING, viewModel.resend().getValue());
        accounts.pendingResend.onSuccess();
        assertEquals(Resend.SENT, viewModel.resend().getValue());

        viewModel.resendLink();
        accounts.pendingResend.onError(AccountError.NETWORK);
        assertEquals(Resend.FAILED, viewModel.resend().getValue());
    }

    @Test
    public void changingTheEmailSignsTheUnverifiedAccountOut() {
        viewModel.useAnotherEmail();
        assertEquals(1, accounts.signOutCalls);
    }
}
