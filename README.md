# mathKeyboard
With this keyboard you can input mathematical formulas in your Android app to LaTEX format:

![alt text](https://github.com/drEast/mathKeyboard/blob/master/sample_image_01.png)
![alt text](https://github.com/drEast/mathKeyboard/blob/master/sample_image_02.png)

I created this keyboard for my Android app [Derivate Quiz](https://play.google.com/store/apps/details?id=appleitung.feuerkoenig.appleitung), since I was not able to find an easy and elegant way to input formulas to a displayable format.

In order to save you the hustle, I uploaded the keyboard with a sample activity. Please, feel free to use or expand any part of it.

## Usage
Java files: \
java/feuerkoenig/mathKeyboard/sampleActivity.java &ensp;&ensp;&ensp; Activity to display the keyboard \
java/feuerkoenig/mathKeyboard/Keyboard.java &ensp;&ensp;&ensp; Logic behind the keyboard 

XML files: \
mathKeyboard/res/drawable-v24/... &ensp;&ensp;&ensp; Graphic design for the keyboard buttons \
mathKeyboard/res/layout/activity_sample.xml &ensp;&ensp;&ensp; Activity to display the keyboard \
mathKeyboard/res/layout/keyboard.xml &ensp;&ensp;&ensp; Keyboard layout with button alignment \
mathKeyboard/res/values/colors.xml &ensp;&ensp;&ensp; Color theme of the app and buttons \
mathKeyboard/res/values/strings.xml &ensp;&ensp;&ensp; Button symbols \
mathKeyboard/res/values/styles.xml &ensp;&ensp;&ensp; Contains the layout values for the keyboard buttons 

## Shortcomings
Although many forbidden inputs are handled such as double signs (e.g. "++") or empty brackets (e.g. "ln()"), not all syntax errors are detected such as brackets without sensible input (e.g. "sin(-)"). You can either incorporate it, if needed, or let it to the user's intelligence depending on you application.

## Requirements
In this sample the [MathView](https://github.com/jianzhongli/MathView) is used to display the formula. If you want to use the sample provided above, you need to add the dependency as described to your project. There exist several various similar versions with small differences in usage and features. In theory the keyboard should be usable for every View that can display a LaTEX formula, although I did not test it.
