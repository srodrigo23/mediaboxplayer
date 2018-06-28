package com.umss.rodres.mbp2;

import com.umss.rodres.mbp2.login.LoginPresenter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        LoginPresenter presenter = new LoginPresenter(null);
        presenter.login();
        //assertEquals(4, 2 + 2);
    }
}