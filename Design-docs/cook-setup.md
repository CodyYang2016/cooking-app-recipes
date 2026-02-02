```mermaid
stateDiagram-v2
  [*] --> Suggested
  Suggested --> Edited: user changes amount
  Suggested --> Missing: mark missing
  Suggested --> Substituted: choose substitute pantry item
  Edited --> Suggested: reset
  Edited --> Missing
  Edited --> Substituted
  Missing --> Suggested: unmark
  Missing --> Substituted
  Substituted --> Suggested: remove substitute
  Substituted --> Edited: edit amount used
  Suggested --> [*]: finish cooking confirmed