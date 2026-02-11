```mermaid
erDiagram
  User ||--o{ PantryItem : owns
  User ||--o{ Recipe : owns
  User ||--o{ CookSession : owns

  Recipe ||--o{ RecipeIngredient : requires
  Recipe ||--o{ RecipeStep : has
  Recipe ||--o{ CookSession : cooked_as

  CookSession ||--o{ CookIngredientUsage : records
  CookSession ||--o{ InventoryAdjustment : audit
  PantryItem ||--o{ InventoryAdjustment : adjusted

  User {
    string id PK
    string email
    string displayName
    string createdAt
  }

  PantryItem {
    string id PK
    string userId FK
    string ingredientName
    decimal quantity
    string unit
    string updatedAt
    string syncStatus "LOCAL_ONLY|SYNCED|DIRTY"
    string serverUpdatedAt
  }

  Recipe {
    string id PK
    string userId FK
    string title
    string sourceType "USER|URL|API"
    string sourceRef
    int defaultServings
    string imageUrl
    string imageLocalPath
    string createdAt
    string updatedAt
    string syncStatus "LOCAL_ONLY|SYNCED|DIRTY"
    string serverUpdatedAt
  }

  RecipeIngredient {
    string id PK
    string recipeId FK
    string userId FK
    string ingredientName
    decimal amount
    string unit
    bool optional
    int position
  }

  RecipeStep {
    string id PK
    string recipeId FK
    string userId FK
    int stepNumber
    string instruction
  }

  CookSession {
    string id PK
    string userId FK
    string recipeId FK
    int originalServings
    int cookedServings
    decimal scaleFactor
    string cookedAt
    string notes
    string syncStatus "LOCAL_ONLY|SYNCED|DIRTY"
    string serverUpdatedAt
  }

  CookIngredientUsage {
    string id PK
    string cookSessionId FK
    string userId FK
    string recipeIngredientId FK
    decimal suggestedAmount
    decimal actualAmountUsed
    string unit
    string status "USED|MISSING|SUBSTITUTED"
    string substitutedPantryItemId
  }

  InventoryAdjustment {
    string id PK
    string cookSessionId FK
    string userId FK
    string pantryItemId FK
    decimal delta
    string reason "COOK_DEDUCT"
    string createdAt
  }
