```mermaid
classDiagram
  direction LR

  %% ==========================================================
  %% Cooking Recipes App — Domain Class Diagram (based on your spec)
  %% - Auth enabled
  %% - Offline-first w/ cached recipes (pre-load)
  %% - Pantry decremented after Finish Cooking (with audit log)
  %% - "Almost there" search prioritized by minimal missing ingredients
  %% - User can scale servings + confirm/edit usage + mark missing/substitute
  %% - User can create/edit recipes + import from URL + pull from free API
  %% - Cooked history required
  %% ==========================================================

  class User {
    +id: String
    +email: String
    +createdAt: Instant
  }

  class PantryItem {
    +id: String
    +userId: String
    +ingredientName: String
    +quantity: Decimal
    +unit: String
    +updatedAt: Instant
  }

  class Recipe {
    +id: String
    +userId: String
    +title: String
    +sourceType: SourceType
    +sourceRef: String?
    +defaultServings: Int
    +imageUrl: String?
    +imageLocalPath: String?
    +createdAt: Instant
    +updatedAt: Instant
  }

  class RecipeIngredient {
    +id: String
    +recipeId: String
    +ingredientName: String
    +amount: Decimal
    +unit: String
    +optional: Boolean
    +position: Int
  }

  class RecipeStep {
    +id: String
    +recipeId: String
    +stepNumber: Int
    +instruction: String
  }

  class CookSession {
    +id: String
    +userId: String
    +recipeId: String
    +originalServings: Int
    +cookedServings: Int
    +scaleFactor: Decimal
    +cookedAt: Instant
    +notes: String?
  }

  class CookIngredientUsage {
    +id: String
    +cookSessionId: String
    +recipeIngredientId: String
    +suggestedAmount: Decimal
    +actualAmountUsed: Decimal
    +unit: String
    +status: UsageStatus
    +substitutedPantryItemId: String?
  }

  class InventoryAdjustment {
    +id: String
    +cookSessionId: String
    +pantryItemId: String
    +delta: Decimal
    +reason: AdjustmentReason
    +createdAt: Instant
  }

  class RecipeMatchResult {
    +recipeId: String
    +missingCount: Int
    +coveredCount: Int
    +canMake: Boolean
  }

  class MissingIngredient {
    +ingredientName: String
    +requiredAmount: Decimal
    +unit: String
    +availableAmount: Decimal
    +shortfallAmount: Decimal
  }

  class AuthSession {
    +userId: String
    +accessToken: String
    +refreshToken: String?
    +expiresAt: Instant
  }

  class SourceType {
    <<enumeration>>
    USER
    URL
    API
  }

  class UsageStatus {
    <<enumeration>>
    USED
    MISSING
    SUBSTITUTED
  }

  class AdjustmentReason {
    <<enumeration>>
    COOK_DEDUCT
    MANUAL_PANTRY_EDIT
  }

  %% ======================
  %% Relationships
  %% ======================

  User "1" o-- "0..1" AuthSession : has
  User "1" o-- "*" PantryItem : owns
  User "1" o-- "*" Recipe : owns
  User "1" o-- "*" CookSession : cooks

  Recipe "1" o-- "*" RecipeIngredient : requires
  Recipe "1" o-- "*" RecipeStep : steps
  Recipe "1" o-- "*" CookSession : cookedAs

  CookSession "1" o-- "*" CookIngredientUsage : recordsUsage
  CookSession "1" o-- "*" InventoryAdjustment : producesAuditLog

  PantryItem "1" o-- "*" InventoryAdjustment : adjustedBy

  %% Substitution uses a PantryItem instead of the "as-written" ingredient
  CookIngredientUsage "0..*" --> "0..1" PantryItem : substitutedPantryItem

  %% Search results derived from pantry + recipe
  RecipeMatchResult "1" o-- "*" MissingIngredient : missingItems
  Recipe "1" ..> RecipeMatchResult : evaluatedAs

  %% Enumerations usage
  Recipe ..> SourceType
  CookIngredientUsage ..> UsageStatus
  InventoryAdjustment ..> AdjustmentReason
