# Egg to Elite

![Egg to Elite Logo](app/src/main/resources/icon.jpg)

## Description

Egg to Elite is a virtual pet simulation game inspired by classic Tamagotchi and Duck Life games. Players care for a virtual duck, nurturing it from an egg to an elite champion through training and care. Feed your duck, ensure it gets enough sleep, and train it!

The game features:
- Pet care mechanics (feeding, sleeping, monitoring health and happiness)
- Skill training in three disciplines (running, swimming, flying)
- Interactive mini-games for each skill
- Pet customization options
- A shop to purchase food and gifts
- Save/load system for multiple game saves

## Required Libraries and Tools

To run or build Egg to Elite, you'll need:

- Java Development Kit (JDK) 17 or newer
- JavaFX 17.0.2 or newer
- Gradle (for building from source)

All graphics, sounds, and other resources are included in the application package.

## Building from Source

Follow these steps to build Egg to Elite from source code:

### Install Prerequisites

#### Install JDK 17+
1. Download JDK 17 or newer from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
2. Follow the installation instructions for your operating system
3. Verify installation by opening a command prompt/terminal and typing: `java -version`

#### Install Gradle
1. Download Gradle from the [Gradle website](https://gradle.org/releases/)
2. Extract the archive to a directory of your choice
3. Add Gradle's bin directory to your system PATH
   - **Windows**: Edit Environment Variables and add to Path
   - **macOS/Linux**: Add to your shell profile (e.g., `export PATH=$PATH:/path/to/gradle/bin`)
4. Verify installation by typing: `gradle -version`


### Build the Application

In the project root directory (where the build.gradle file is located), run:
```
gradle clean build
```

This command will:
- Download all necessary dependencies
- Compile the source code
- Run tests
- Package the application into an executable JAR file

The built application will be in the `build/libs` directory as `app.jar`.

## Running the built file
- Execute the app.jar file by using this command:
```
java --module-path "PATH_TO_JAVAFX_LIB_FILES" --add-modules javafx.controls,javafx.fxml,javafx.media -jar build/libs/app.jar
```

Replacing PATH_TO_JAVAFX_LIB_FILES with the actual location of your javafx library files.

## User Guide

### Getting Started

1. **Main Menu**: When you first launch the game, you'll see the main menu with options to start a new game, load a saved game, view instructions, access parental controls, or exit the game.

2. **Creating Your Duck**: After selecting "New Game", you'll choose a duck type and give it a name.

3. **Game Screen**: The main game screen displays your duck's stats (health, happiness, fullness, sleepiness) and skills (running, swimming, flying). From this screen you'll be able to access all the things you need to care for your duck.

### Caring for Your Duck

- **Feeding**: Purchase food in the store to feed to your duck to keep its fullness up. If the fullness gets to 0, the pet's health will start to decrease.
- **Sleeping**: Click the "Sleep" button to put your duck to sleep. Sleeping reduces sleepiness. If sleepiness gets to 0, the pet will pass out.
- **Gifts**: Give your duck gifts to boost its happiness. If your pet gets too unhappy, they'll become sad and will refuse certain commands. 

### Training Skills

Your duck has three trainable skills:

1. **Running**
2. **Swimming**
3. **Flying**

For each skill, you can either start a training session or view a tutorial first. The tutorial will not net you any coins or experience, nor will it cost any fullness and sleepiness to do. It provides a simpler version of the real training minigame so that you can get up to speed. 

### Shop

Visit the shop to purchase:
- Different foods that restore varying amounts of fullness
- Gifts to boost your duck's happiness

### Saving and Loading

- Save your progress using the "Save" button in the Game Screen
- Up to three save slots are available
- Load a saved game from the main menu

## Parental Controls

Parental controls allow setting limits on gameplay time and accessing game statistics. To access the parental controls, a password is required. The password is as follows:
```
hello
```
