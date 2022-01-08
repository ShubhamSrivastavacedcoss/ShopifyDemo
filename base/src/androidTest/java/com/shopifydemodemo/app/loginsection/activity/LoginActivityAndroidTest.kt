package com.shopifydemodemo.app.loginsection.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import com.shopifydemodemo.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityAndroidTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun onSignUpClickedTest() {
        onView((withId(R.id.username))).perform(typeText("abhi@gmail.com"))
        onView((withId(R.id.password))).perform(typeText("password"))
        onView(withId(R.id.login)).perform(click())
    }
}