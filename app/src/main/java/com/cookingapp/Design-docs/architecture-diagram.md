```mermaid
flowchart LR
  %% =====================
  %% CLASS DEFINITIONS
  %% =====================
  classDef screen fill:#E3F2FD,stroke:#1565C0,stroke-width:2px,color:#0D47A1;
  classDef logic fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px,color:#1B5E20;
  classDef data fill:#F3E5F5,stroke:#6A1B9A,stroke-width:2px,color:#4A148C;
  classDef remote fill:#E0F7FA,stroke:#00838F,stroke-width:2px,color:#006064;

  subgraph UI["UI"]
    AuthUI["Login / Sign-up / Account"]:::screen
    AppUI["Home + Pantry + Search + Detail + Cook + History"]:::screen
  end

  subgraph VM["ViewModels"]
    AuthVM["AuthViewModel"]:::logic
    PantryVM["PantryViewModel"]:::logic
    SearchVM["RecipeSearchViewModel"]:::logic
    CookVM["CookSessionViewModel"]:::logic
    HistoryVM["HistoryViewModel"]:::logic
    MyRecipesVM["MyRecipesViewModel"]:::logic
    SyncVM["SyncViewModel"]:::logic
  end

  subgraph Repo["Repositories"]
    AuthRepo["AuthRepository"]:::logic
    PantryRepo["PantryRepository"]:::logic
    RecipeRepo["RecipeRepository"]:::logic
    CookRepo["CookRepository"]:::logic
    SyncRepo["SyncRepository"]:::logic
  end

  subgraph Local["Local Persistence"]
    RoomDB["Room Database"]:::data
    DAOs["DAOs (scoped by userId)"]:::data
    TokenStore["Encrypted Token Store\n(DataStore/Keystore)"]:::data
  end

  subgraph Remote["Remote Services"]
    AuthProvider["Auth Provider\n(OAuth/Firebase/custom)"]:::remote
    AppBackend["App Backend\n(sync user data)"]:::remote
    FreeRecipeAPI["Free Recipe API\n(optional)"]:::remote
  end

  subgraph BG["Background"]
    WorkMgr["WorkManager\n(periodic/one-shot sync)"]:::logic
  end

  AuthUI --> AuthVM
  AppUI --> PantryVM
  AppUI --> SearchVM
  AppUI --> CookVM
  AppUI --> HistoryVM
  AppUI --> MyRecipesVM
  AppUI --> SyncVM

  AuthVM --> AuthRepo
  PantryVM --> PantryRepo
  SearchVM --> RecipeRepo
  CookVM --> CookRepo
  HistoryVM --> CookRepo
  MyRecipesVM --> RecipeRepo
  SyncVM --> SyncRepo

  AuthRepo --> AuthProvider
  AuthRepo --> TokenStore

  PantryRepo --> DAOs
  RecipeRepo --> DAOs
  CookRepo --> DAOs
  SyncRepo --> DAOs
  DAOs --> RoomDB

  SyncRepo --> AppBackend
  RecipeRepo --> FreeRecipeAPI

  WorkMgr --> SyncRepo
  TokenStore --> SyncRepo
