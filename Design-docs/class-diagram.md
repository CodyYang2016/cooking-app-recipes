```mermaid
classDiagram
  direction LR

  %% =========================
  %% PURE DOMAIN MODEL
  %% (No UI / ViewModels / DAOs / Repos / Clients)
  %% =========================

  class User {
    +id: String
    +email: String
    +displayName: String
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
    +missingItems: List~MissingIngredient~
    +canMake: Boolean
  }

  class MissingIngredient {
    +ingredientName: String
    +requiredAmount: Decimal
    +unit: String
    +availableAmount: Decimal
    +shortfallAmount: Decimal
  }

  class FinalizeCookPayload {
    +cookSessionId: String?
    +recipeId: String
    +userId: String
    +originalServings: Int
    +cookedServings: Int
    +scaleFactor: Decimal
    +usages: List~CookIngredientUsageInput~
    +cookedAt: Instant
    +notes: String?
  }

  class CookIngredientUsageInput {
    +recipeIngredientId: String
    +suggestedAmount: Decimal
    +actualAmountUsed: Decimal
    +unit: String
    +status: UsageStatus
    +substitutedPantryItemId: String?
  }

  class CookSessionDetail {
    +session: CookSession
    +recipe: Recipe
    +ingredients: List~RecipeIngredient~
    +steps: List~RecipeStep~
    +usages: List~CookIngredientUsage~
    +adjustments: List~InventoryAdjustment~
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
    MANUAL_EDIT
    IMPORT_CORRECTION
  }

  %% =========================
  %% DOMAIN RELATIONSHIPS
  %% =========================

  User "1" o-- "*" PantryItem : owns
  User "1" o-- "*" Recipe : owns
  User "1" o-- "*" CookSession : performs

  Recipe "1" o-- "*" RecipeIngredient : requires
  Recipe "1" o-- "*" RecipeStep : steps
  Recipe "1" o-- "*" CookSession : cookedAs

  CookSession "1" o-- "*" CookIngredientUsage : records
  CookSession "1" o-- "*" InventoryAdjustment : produces

  PantryItem "1" o-- "*" InventoryAdjustment : adjustedBy

  %% Match result is derived from Pantry + Recipe
  Recipe "1" ..> "0..*" RecipeMatchResult : "evaluated as"
  RecipeMatchResult "1" o-- "*" MissingIngredient : includes

  %% Finalize cook input produces cook session + usage + adjustments
  FinalizeCookPayload "1" o-- "*" CookIngredientUsageInput : includes
  CookIngredientUsageInput ..> UsageStatus
  InventoryAdjustment ..> AdjustmentReason
  Recipe ..> SourceType

  %% Detail aggregate
  CookSessionDetail "1" o-- "1" CookSession
  CookSessionDetail "1" o-- "1" Recipe
  CookSessionDetail "1" o-- "*" RecipeIngredient
  CookSessionDetail "1" o-- "*" RecipeStep
  CookSessionDetail "1" o-- "*" CookIngredientUsage
  CookSessionDetail "1" o-- "*" InventoryAdjustment
