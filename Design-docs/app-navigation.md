```mermaid
flowchart TB
  %% ================
  %% COLOR STYLES
  %% ================
  classDef screen fill:#E3F2FD,stroke:#1565C0,stroke-width:2px,color:#0D47A1;
  classDef decision fill:#FFF3E0,stroke:#EF6C00,stroke-width:2px,color:#E65100;
  classDef logic fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px,color:#1B5E20;
  classDef data fill:#F3E5F5,stroke:#6A1B9A,stroke-width:2px,color:#4A148C;
  classDef remote fill:#E0F7FA,stroke:#00838F,stroke-width:2px,color:#006064;
  classDef audit fill:#FFEBEE,stroke:#C62828,stroke-width:2px,color:#B71C1C;

  %% ================
  %% AUTH + APP ENTRY
  %% ================
  Launch["Launch"]:::screen --> AuthCheck{"Authenticated?"}:::decision

  AuthCheck -->|No| Login["Login"]:::screen
  Login --> Signup["Sign Up"]:::screen
  Login --> Forgot["Forgot Password"]:::screen
  Signup --> Login
  Forgot --> Login

  AuthCheck -->|Yes| Home["Home\n- Pantry\n- Find Recipes\n- History\n- My Recipes\n- Account"]:::screen
  Login -->|Success| Home
  Signup -->|Success| Home

  %% ================
  %% UI SCREENS
  %% ================
  Pantry["Pantry Screen\n- list items\n- add/edit quantities"]:::screen
  Search["Find Recipes\n- rank by minimal missing\n- show almost-there"]:::screen
  Detail["Recipe Detail\n- ingredients\n- steps\n- Cook"]:::screen
  CookSetup["Cook Setup\n- scale servings"]:::screen
  CookMode["Cooking Mode\n- follow steps\n- Finish"]:::screen
  Confirm["Confirm Usage\n(default scaled, editable)\n- Used\n- Missing\n- Substitute"]:::screen
  History["History\n- sessions list\n- session detail"]:::screen
  MyRecipes["My Recipes\n- create/edit\n- import URL"]:::screen
  Account["Account\n- Profile\n- Logout\n- Sync status"]:::screen
  Done["✅ Success\n(updated pantry summary)"]:::screen

  Home --> Pantry
  Home --> Search
  Home --> History
  Home --> MyRecipes
  Home --> Account

  Search --> Detail
  Detail --> CookQ{"Cook?"}:::decision
  CookQ -->|Yes| CookSetup
  CookQ -->|No| Search

  CookSetup --> CookMode
  CookMode --> FinishQ{"Finish cooking?"}:::decision
  FinishQ -->|No| CookMode
  FinishQ -->|Yes| Confirm

  Confirm --> Done
  Done --> History
  Done --> Pantry
  Done --> Detail

  Account --> LogoutQ{"Logout?"}:::decision
  LogoutQ -->|Yes| DoLogout["Clear tokens\nReturn to Login"]:::logic
  DoLogout --> Login
  LogoutQ -->|No| Home

  %% ================
  %% CORE LOGIC
  %% ================
  Match["Recipe Matching & Ranking\n(sort by missingCount asc)"]:::logic
  Finalize["Finalize Cook Session (Room transaction)\n- write history\n- compute decrements\n- write audit\n- apply pantry updates"]:::logic
  Importer["URL Import Parser\n-> Recipe Draft -> Save"]:::logic

  Search --> Match
  Confirm --> Finalize
  MyRecipes --> Importer

  %% ================
  %% LOCAL DATA (ROOM)
  %% ================
  subgraph DB["Local Storage (Room DB, offline-first)"]
    PantryTbl["PantryItem (+userId)"]:::data
    RecipeTbl["Recipe (+userId)\nsourceType USER|URL|API"]:::data
    StepsTbl["RecipeIngredient & RecipeStep (+userId)"]:::data
    CookTbl["CookSession (+userId)"]:::data
    UsageTbl["CookIngredientUsage (+userId)"]:::data
    AuditTbl["InventoryAdjustment (+userId)\nAudit Log"]:::audit
    TokenStore["Token Storage (Encrypted)\naccess/refresh or session"]:::data
  end

  Pantry --> PantryTbl
  Search --> RecipeTbl
  Detail --> StepsTbl
  History --> CookTbl
  History --> UsageTbl
  History --> AuditTbl
  Login --> TokenStore
  DoLogout --> TokenStore

  Finalize --> CookTbl
  Finalize --> UsageTbl
  Finalize --> AuditTbl
  Finalize --> PantryTbl

  %% ================
  %% OPTIONAL ONLINE (AUTHENTICATED)
  %% ================
  subgraph Remote["Remote (Authenticated)"]
    Online{"Online?"}:::decision
    Sync["Sync Service\n- backup/restore\n- fetch API recipes\n- upload user recipes"]:::remote
    AuthAPI["Auth Provider\n(OAuth/Firebase/custom)"]:::remote
    AppAPI["App Backend\n(user data sync)"]:::remote
    FreeRecipeAPI["Free Recipe API\n(optional)"]:::remote
  end

  Account --> Online
  Search --> Online

  Online -->|Yes| Sync
  Online -->|No| Search

  Sync --> AuthAPI
  Sync --> AppAPI
  Sync --> FreeRecipeAPI

  Sync --> RecipeTbl
  Sync --> PantryTbl
  Sync --> CookTbl
