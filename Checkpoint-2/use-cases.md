# Categorized Use Cases – Pantry & Recipe Mobile App

## 1. Account Management  
1. **Create Account.**  
   The user taps the *Create Account* button. The app prompts the user to enter email/username and password. The app validates the input, securely hashes the password, and creates a user account in the backend database.

2. **Login.**  
   The user enters their email/username and password and taps *Login*. The app authenticates the credentials against the backend and logs the user in if valid; otherwise, it displays an error message.

3. **Logout.**  
   The user taps the *Logout* button. The app clears the authentication token and returns the user to the login screen.

4. **Reset Password.**  
   The user taps *Forgot Password*. The app prompts for an email address and sends a password reset link or verification code.

5. **Edit Profile.**  
   The user opens the *Profile* screen and updates personal info such as display name, dietary preferences, and allergies. The app saves changes to the backend.

---

## 2. Pantry Management  
1. **Add Ingredient to Pantry.**  
   The user taps *Add Item*, enters the ingredient name, quantity, and unit (e.g., “2 cups of rice”). The app stores the ingredient in the user’s pantry database.

2. **Update Ingredient Quantity.**  
   The user edits an existing pantry item and changes the quantity. The app updates the stored value.

3. **Remove Ingredient from Pantry.**  
   The user selects an ingredient and taps *Delete*. The app removes the item from the pantry list.

4. **View Pantry Inventory.**  
   The user opens the *Pantry* screen. The app displays all logged ingredients with quantities and expiration dates (if available).

5. **Low Stock Notification.**  
   The app detects when an ingredient quantity falls below a defined threshold and notifies the user.

---

## 3. Recipe Search  
1. **Search Recipes by Pantry Ingredients.**  
   The user taps *Find Recipes*. The app sends the user’s pantry items to the recipe API and displays matching recipes.

2. **Filter Recipes.**  
   The user applies filters such as cuisine type, cooking time, dietary preferences, or difficulty.

3. **View Recipe Details.**  
   The user selects a recipe. The app displays ingredients, quantities, cooking steps, nutritional info, and estimated time.

4. **Suggest Ingredient Alternatives.**  
   The app suggests substitutes or highlights missing ingredients.

---

## 4. Cooking & Pantry Auto-Update  
1. **Mark Recipe as Started.**  
   The user taps *Start Cooking*. The app locks in required ingredient quantities.

2. **Mark Recipe as Completed.**  
   The user taps *Complete Recipe*. The app subtracts used quantities from pantry inventory.

3. **Partial Ingredient Adjustment.**  
   The user can adjust quantities before the pantry is updated.

---

## 5. Bookmarking Recipes 
1. **Bookmark Recipe.**  
   The user taps the bookmark icon to save a recipe.

2. **View Bookmarked Recipes.**  
   The user opens the *Favorites* screen.

3. **Remove Bookmark.**  
   The user removes a recipe from favorites.

---

## 6. Custom Recipe Management  
1. **Create Custom Recipe.**  
   The user adds a recipe with ingredients, quantities, and steps.

2. **Edit Custom Recipe.**  
   The user updates their custom recipe.

3. **Delete Custom Recipe.**  
   The user removes a custom recipe.

4. **Use Custom Recipe for Pantry Updates.**  
   Completing a custom recipe updates pantry quantities.

---

## 7. Data Storage & Sync  
1. **Sync Pantry Across Devices.**  
   The app loads user data on a new device.

2. **Offline Mode.**  
   The app allows access to cached data offline and syncs later.

3. **Backup & Restore User Data.**  
   The app backs up and restores user data.

---

## 8. Error Handling & Validation  
1. **Invalid Login Attempt.**  
   The app displays an error message for failed logins.

2. **API Failure Handling.**  
   The app shows an error and offers retry if the recipe API fails.

3. **Invalid Ingredient Input.**  
   The app validates input and shows errors for invalid quantities or units.
