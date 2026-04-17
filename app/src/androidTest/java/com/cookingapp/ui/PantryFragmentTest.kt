package com.cookingapp.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.cookingapp.MainActivity
import com.cookingapp.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PantryFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        // Clear all items before each test so database is clean
        onView(withId(R.id.clearAllButton)).perform(click())
        try {
            onView(withText("Yes, Clear All")).perform(click())
        } catch (e: Exception) {
            // No dialog shown if already empty
        }
        Thread.sleep(500)
    }

    @Test
    fun testAddNewPantryItem_displaysInList() {
        onView(withId(R.id.editTextName))
            .perform(typeText("Test Milk"), closeSoftKeyboard())

        onView(withId(R.id.editTextQuantity))
            .perform(typeText("2"), closeSoftKeyboard())

        onView(withId(R.id.editTextUnit))
            .perform(typeText("liters"), closeSoftKeyboard())

        onView(withId(R.id.buttonAdd)).perform(click())

        Thread.sleep(500)

        onView(withText("Test Milk"))
            .check(matches(isDisplayed()))

        onView(withText("2.0 liters"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testIncreaseQuantityUsingPlusButton() {
        onView(withId(R.id.editTextName))
            .perform(typeText("Test Eggs"), closeSoftKeyboard())
        onView(withId(R.id.editTextQuantity))
            .perform(typeText("3"), closeSoftKeyboard())
        onView(withId(R.id.editTextUnit))
            .perform(typeText("pieces"), closeSoftKeyboard())
        onView(withId(R.id.buttonAdd)).perform(click())

        Thread.sleep(500)

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                clickChildViewWithId(R.id.btnPlus)
            ))

        Thread.sleep(500)

        onView(withText("4.0 pieces"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDeleteItem_removesFromList() {
        onView(withId(R.id.editTextName))
            .perform(typeText("Test Delete Item"), closeSoftKeyboard())
        onView(withId(R.id.editTextQuantity))
            .perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.editTextUnit))
            .perform(typeText("each"), closeSoftKeyboard())
        onView(withId(R.id.buttonAdd)).perform(click())

        Thread.sleep(500)

        onView(withText("Test Delete Item"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                clickChildViewWithId(R.id.btnDelete)
            ))

        Thread.sleep(500)

        onView(withText("Test Delete Item"))
            .check(doesNotExist())
    }
}

fun clickChildViewWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints() = null
        override fun getDescription() = "Click child view with id"
        override fun perform(uiController: UiController, view: View) {
            view.findViewById<View>(id).performClick()
        }
    }
}