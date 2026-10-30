package com.jiran.app.ui.auth;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.AccountService.AccountError;
import com.jiran.app.domain.auth.SignUpCheck.Problem;
import com.jiran.app.ui.auth.ResetPasswordViewModel.State;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class ResetPasswordViewModelTest {
    @Rule
    public final InstantTaskExecutorRule instantTasks = new InstantTaskExecutorRule();

    private FakeAccountService accounts;
    private ResetPasswordViewModel viewModel;

    @Before
    public void setUp() {
        accounts = new FakeAccountService();
        viewModel = new ResetPasswordViewModel(accounts);
    }

    @Test
    public void aBadAddressNeverReachesTheBackend() {
        viewModel.submit("not-an-email");
        assertEquals(0, accounts.resetCalls);
        assertEquals(Problem.NOT_AN_EMAIL, viewModel.emailProblem().getValue());
        assertEquals(State.IDLE, viewModel.state().getValue());
    }

    @Test
    public void aGoodAddressLoadsThenReportsSent() {
        viewModel.submit("meiling@siswa.um.edu.my");
        assertEquals(State.LOADING, viewModel.state().getValue());
        accounts.pendingReset.onSuccess();
        assertEquals(State.SENT, viewModel.state().getValue());
    }

    @Test
    public void mapsBackendErrorsToStates() {
        viewModel.submit("meiling@siswa.um.edu.my");
        accounts.pendingReset.onError(AccountError.NETWORK);
        assertEquals(State.NETWORK, viewModel.state().getValue());

        viewModel.submit("meiling@siswa.um.edu.my");
        accounts.pendingReset.onError(AccountError.TOO_MANY_ATTEMPTS);
        assertEquals(State.TOO_MANY_ATTEMPTS, viewModel.state().getValue());

        viewModel.submit("meiling@siswa.um.edu.my");
        accounts.pendingReset.onError(AccountError.UNKNOWN);
        assertEquals(State.UNKNOWN, viewModel.state().getValue());
    }

    @Test
    public void aSecondTapWhileLoadingIsIgnoredButASentLinkCanBeSentAgain() {
        viewModel.submit("meiling@siswa.um.edu.my");
        viewModel.submit("meiling@siswa.um.edu.my");
        assertEquals(1, accounts.resetCalls);
        accounts.pendingReset.onSuccess();
        viewModel.submit("meiling@siswa.um.edu.my");
        assertEquals(2, accounts.resetCalls);
    }
}
