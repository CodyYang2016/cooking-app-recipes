```mermaid
flowchart TD
  A["User opens Find Recipes"] --> B["Load cached recipes from Room"]
  B --> C["Load pantry items from Room"]
  C --> D["Compute match for each recipe:\nfor each required ingredient:\n- find pantry match by name\n- compare quantity"]
  D --> E["Compute missingCount + coveredCount"]
  E --> F["Sort by missingCount ascending"]
  F --> G["Render results + show missing ingredients summary"]