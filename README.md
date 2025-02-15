# Arimaa Game Implementation in Java

## Overview
This project is a **Java-based implementation of Arimaa**, a strategic board game designed to be challenging for computers. The game follows the **official Arimaa rules** and includes full rule enforcement, game timers, and save/load functionality.

## Features

### 🎮 Gameplay Mechanics
- **Fully implemented Arimaa rules**, including:
  - Movement with **1 to 4 steps per turn**.
  - **Pushing and pulling** opponent’s pieces.
  - **Traps** that remove pieces from the board.
  - **Freezing mechanics** based on piece strength.
  - **Win conditions**: rabbit reaching the goal or opponent immobilized.
  - **Custom piece setup** before the game starts.

### 🕹️ Game Modes
- **Local Multiplayer** – Two players can play on the same computer.
- **Computer Opponent** – A simple AI that generates **random legal moves**.

### 🔧 Additional Features
- **Move Timer** – Tracks the time each player spends thinking.
- **Game Save/Load & Replay** – Uses **official Arimaa notation** for saving and replaying moves.
- **Swing GUI** – Interactive interface for playing the game.

## 🚀 How to Play
1. **Run the main class** to start the application.
2. A **dialog menu** will appear—press **"Launch Game"** to begin.
3. Set up pieces and start playing according to Arimaa rules.

## 🛠️ Technical Details
- **Java Swing GUI** for rendering the board.
- **JUnit tests** for validating game mechanics.

