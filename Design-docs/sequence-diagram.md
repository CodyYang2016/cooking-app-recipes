```mermaid
sequenceDiagram
  participant UI as Cook Confirm UI
  participant VM as CookSessionViewModel
  participant Repo as CookRepository
  participant DB as Room (DAOs)

  UI->>VM: Tap "Finish" (servingsScale, edited amounts, missing/subs)
  VM->>Repo: finalizeCookSession(payload)
  Repo->>DB: BEGIN TRANSACTION
  Repo->>DB: insert CookSession
  Repo->>DB: insert CookIngredientUsage (for each ingredient)
  Repo->>DB: for each usage with status USED/SUBSTITUTED:
  Repo->>DB:   decrement PantryItem.quantity
  Repo->>DB:   insert InventoryAdjustment (delta negative)
  Repo->>DB: COMMIT
  Repo-->>VM: success + updated pantry snapshot
  VM-->>UI: Show success + updated pantry