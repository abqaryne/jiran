package com.jiran.app.ui.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.AccountService.AccountError;
import com.jiran.app.domain.auth.SignUpCheck.Problem;
import com.jiran.app.ui.auth.SignUpViewModel.State;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class SignUpViewModelTest {
    @Rule
    public final InstantTaskExecutorRule instantTasks = new InstantTaskExecutorRule();

    private FakeAccountService accounts;
    private SignUpViewModel viewModel;

    @Before
    public void setUp() {
        accounts = new FakeAccountService();
        viewModel = new SignUpViewModel(accounts);
    }

    @Test
    public void startsIdleWithNoFieldProblems() {
        assertEquals(State.IDLE, viewModel.state().getValue());
        assertNull(viewModel.fieldProblems().getValue());
    }

    @Test
    public void anInvalidFormNeverReachesTheBackend() {
        viewModel.submit("Mei Ling", "meiling-at-gmail.com", "12345678");
        assertEquals(0, accounts.signUpCalls);
        assertEquals(Problem.NOT_AN_EMAIL, viewModel.fieldProblems().getValue().email());
        assertEquals(State.IDLE, viewModel.state().getValue());
    }

    @Test
    public void aValidFormLoadsThenSucceeds() {
        viewModel.submit("Mei Ling", "meiling@siswa.um.edu.my", "12345678");
        assertEquals(State.LOADING, viewModel.state().getValue());
        assertTrue(viewModel.fieldProblems().getValue().isValid());
        accounts.pendingSignUp.onSuccess();
        assertEquals(State.SUCCESS, viewModel.state().getValue());
    }

    @Test
    public void anyWellFormedEmailMaySignUp() {
        viewModel.submit("Mei Ling", "meiling@gmail.com", "12345678");
        assertEquals(1, accounts.signUpCalls);
        assertEquals(State.LOADING, viewModel.state().getValue());
    }

    @Test
    public void aSecondTapWhileLoadingIsIgnored() {
        viewModel.submit("Mei Ling", "meiling@siswa.um.edu.my", "12345678");
        viewModel.submit("Mei Ling", "meiling@siswa.um.edu.my", "12345678");
        assertEquals(1, accounts.signUpCalls);
    }

    @Test
    public void mapsBackendErrorsToStates() {
        viewModel.submit("Mei Ling", "meiling@siswa.um.edu.my", "12345678");
        accounts.pendingSignUp.onError(AccountError.EMAIL_TAKEN);
        assertEquals(State.EMAIL_TAKEN, viewModel.state().getValue());

        viewModel.consumeResult();
        viewModel.submit("Mei Ling", "meiling@siswa.um.edu.my", "12345678");
        accounts.pendingSignUp.onError(AccountError.NETWORK);
        assertEquals(State.NETWORK, viewModel.state().getValue());

        viewModel.consumeResult();
        viewModel.submit("Mei Ling", "meiling@siswa.um.edu.my", "12345678");
        accounts.pendingSignUp.onError(AccountError.UNKNOWN);
        assertEquals(State.UNKNOWN, viewModel.state().getValue());
    }

    @Test
    public void consumingAResultGoesBackToIdleButNeverInterruptsLoading() {
        viewModel.submit("Mei Ling", "meiling@siswa.um.edu.my", "12345678");
        viewModel.consumeResult();
        assertEquals(State.LOADING, viewModel.state().getValue());
        accounts.pendingSignUp.onError(AccountError.NETWORK);
        viewModel.consumeResult();
        assertEquals(State.IDLE, viewModel.state().getValue());
    }
}
