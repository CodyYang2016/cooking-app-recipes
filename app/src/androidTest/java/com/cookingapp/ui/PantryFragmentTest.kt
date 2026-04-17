package com.cookingapp.ui

package com.cookingapp.ui

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
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
        // Give the app time to load
        Thread.sleep(1000)
    }

    @Test
    fun testAddNewPantryItem_displaysInList() {
        // Type item name
        onView(withId(R.id.editTextName))
            .perform(typeText("Test Milk"), closeSoftKeyboard())

        // Type quantity
        onView(withId(R.id.editTextQuantity))
            .perform(typeText("2"), closeSoftKeyboard())

        // Type unit
        onView(withId(R.id.editTextUnit))
            .perform(typeText("liters"), closeSoftKeyboard())

        // Click Add button
        onView(withId(R.id.buttonAdd)).perform(click())

        // Wait for the item to appear
        Thread.sleep(500)

        // Verify item appears in the list
        onView(withText("Test Milk"))
            .check(matches(isDisplayed()))

        onView(withText("2.0 liters"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testIncreaseQuantityUsingPlusButton() {
        // First add an item
        onView(withId(R.id.editTextName))
            .perform(typeText("Test Eggs"), closeSoftKeyboard())
        onView(withId(R.id.editTextQuantity))
            .perform(typeText("3"), closeSoftKeyboard())
        onView(withId(R.id.editTextUnit))
            .perform(typeText("pieces"), closeSoftKeyboard())
        onView(withId(R.id.buttonAdd)).perform(click())

        // Wait for item to appear
        Thread.sleep(500)

        // Click the plus button on the first item
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                clickChildViewWithId(R.id.btnPlus)
            ))

        // Wait for update
        Thread.sleep(500)

        // Verify quantity increased to 4.0
        onView(withText("4.0 pieces"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDeleteItem_removesFromList() {
        // First add an item to delete
        onView(withId(R.id.editTextName))
            .perform(typeText("Test Delete Item"), closeSoftKeyboard())
        onView(withId(R.id.editTextQuantity))
            .perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.editTextUnit))
            .perform(typeText("each"), closeSoftKeyboard())
        onView(withId(R.id.buttonAdd)).perform(click())

        // Wait for item to appear
        Thread.sleep(500)

        // Verify item is visible
        onView(withText("Test Delete Item"))
            .check(matches(isDisplayed()))

        // Click delete button on the item
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                clickChildViewWithId(R.id.btnDelete)
            ))

        // Wait for deletion
        Thread.sleep(500)

        // Verify item is removed
        onView(withText("Test Delete Item"))
            .check(doesNotExist())
    }
}

// Helper function to click a child view inside a RecyclerView item
fun clickChildViewWithId(viewId: Int) = RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
    hasDescendant(withId(viewId)),
    click()
)

// Helper matcher for RecyclerView actions
fun hasDescendant(matcher: org.hamcrest.Matcher<android.view.View>) =
    object : org.hamcrest.TypeSafeMatcher<RecyclerView.ViewHolder>() {
        override fun describeTo(description: org.hamcrest.Description) {
            description.appendText("ViewHolder with descendant: ")
            matcher.describeTo(description)
        }

        override fun matchesSafely(item: RecyclerView.ViewHolder): Boolean {
            return matcher.matches(item.itemView)
        }
    }