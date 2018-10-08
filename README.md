# Marvel-Api
A project for android using the public marvel API

# Build
- Put your own apikeys on [ApiCall](./app/src/main/java/me/maxcostadev/desafio/helper/ApiCall.kt) Line 20 and 21
- Build your project with Android studio, no command or external tools required, graddle will install everything you need

# Usage
- On the main page it will load a list with the characters, clicking on it will open a page with more info and links to the character

# Structure
```
app/src/main
├── res
│  ├── layout
│  │  └── [layout and itens for the list]
│  └── menu
│     └── [layout of the menu in the toolbar]
├── java\me\maxcostadev\desafio
│  ├── [Adapter and Activity classes]
│  ├── helper
│  │  └── [Class for calling the api]
│  └── model
│     └── [Class used for storing data from the api]
└──
```
