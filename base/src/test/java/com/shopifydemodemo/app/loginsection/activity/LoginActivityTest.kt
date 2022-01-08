package com.shopifydemodemo.app.loginsection.activity

import android.widget.EditText
import com.shopifydemodemo.app.databinding.MLoginBinding
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class LoginActivityTest {
    @Mock
    lateinit var mLoginPageBinding: MLoginBinding

    @Mock
    lateinit var activity: LoginActivity
    lateinit var username: EditText
    lateinit var password: EditText

    @Test
    fun onSignUpClickedTest() {
//        assertEquals("abhi@gmail.com", username.text)
//        assertEquals("password", password.text)
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        activity = mock(LoginActivity::class.java)
//        username = EditText().findViewById(R.id.username)
//        password = activity.findViewById(R.id.password)
    }

    @After
    fun tearDown() {
    }
}