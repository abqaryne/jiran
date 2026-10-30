package com.jiran.app.ui.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.AccountService.AccountError;
import com.jiran.app.domain.auth.SignUpCheck.Problem;
import com.jiran.app.ui.auth.LogInViewModel.State;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class LogInViewModelTest {
    @Rule
    public final InstantTaskExecutorRule instantTasks = new InstantTaskExecutorRule();

    private FakeAccountService accounts;
    private LogInViewModel viewModel;

    @Before
    public void setUp() {
        accounts = new FakeAccountService();
        viewModel = new LogInViewModel(accounts);
    }

    @Test
    public void startsIdleWithNoProblems() {
        assertEquals(State.IDLE, viewModel.state().getValue());
        assertNull(viewModel.fieldProblems().getValue());
    }

    @Test
    public void anInvalidFormNeverReachesTheBackend() {
        viewModel.submit("", "");
        assertEquals(0, accounts.signInCalls);
        assertEquals(Problem.REQUIRED, viewModel.fieldProblems().getValue().email());
        assertEquals(Problem.REQUIRED, viewModel.fieldProblems().getValue().password());
    }

    @Test
    public void aVerifiedAccountGoesToTheProduct() {
        viewModel.submit("meiling@siswa.um.edu.my", "12345678");
        assertEquals(State.LOADING, viewModel.state().getValue());
        accounts.pendingSignIn.onResult(true);
        assertEquals(State.VERIFIED, viewModel.state().getValue());
    }

    @Test
    public void anUnverifiedAccountMustVerifyFirst() {
        viewModel.submit("meiling@siswa.um.edu.my", "12345678");
        accounts.pendingSignIn.onResult(false);
        assertEquals(State.NEEDS_VERIFICATION, viewModel.state().getValue());
    }

    @Test
    public void mapsBackendErrorsToStates() {
        viewModel.submit("meiling@siswa.um.edu.my", "wrong");
        accounts.pendingSignIn.onError(AccountError.INVALID_CREDENTIALS);
        assertEquals(State.INVALID_CREDENTIALS, viewModel.state().getValue());

        viewModel.consumeResult();
        viewModel.submit("meiling@siswa.um.edu.my", "wrong");
        accounts.pendingSignIn.onError(AccountError.TOO_MANY_ATTEMPTS);
        assertEquals(State.TOO_MANY_ATTEMPTS, viewModel.state().getValue());

        viewModel.consumeResult();
        viewModel.submit("meiling@siswa.um.edu.my", "wrong");
        accounts.pendingSignIn.onError(AccountError.NETWORK);
        assertEquals(State.NETWORK, viewModel.state().getValue());

        viewModel.consumeResult();
        viewModel.submit("meiling@siswa.um.edu.my", "wrong");
        accounts.pendingSignIn.onError(AccountError.UNKNOWN);
        assertEquals(State.UNKNOWN, viewModel.state().getValue());
    }

    @Test
    public void aSecondTapWhileLoadingIsIgnored() {
        viewModel.submit("meiling@siswa.um.edu.my", "12345678");
        viewModel.submit("meiling@siswa.um.edu.my", "12345678");
        assertEquals(1, accounts.signInCalls);
    }

    @Test
    public void consumingNeverInterruptsLoading() {
        viewModel.submit("meiling@siswa.um.edu.my", "12345678");
        viewModel.consumeResult();
        assertEquals(State.LOADING, viewModel.state().getValue());
    }
}
