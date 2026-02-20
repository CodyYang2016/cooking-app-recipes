```mermaid
flowchart TD
  L["App Launch"] --> T{"Valid session token exists?"}
  T -->|No| A["Auth Screens\n(Login/Sign-up)"]
  T -->|Yes| H["Main App (Home)"]
  A -->|Login success| H
  H --> O{"Logout?"}
  O -->|Yes| C["Clear tokens + cached user session\nReturn to Login"]
  O -->|No| H
