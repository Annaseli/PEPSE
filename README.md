# PEPSE

Precise Environmental Procedural Simulator Extraordinaire - 
Simulator of an avatar in a world that can jump, run and stand.

Uses Object Oriented Programming

##########################################################################################

=============================
=      File description     =
=============================

src/pepse/util/ColorSupplier.java - Provides procedurally-generated colors around a pivot.
src/pepse/world/daynight/Night.java -  The class represents darkens the entire window.
src/pepse/world/daynight/Sun.java -  The class represents the sun - moves across the sky in an elliptical path.
src/pepse/world/daynight/SunHalo.java -  The class represents the halo sun.
src/pepse/world/trees/Leaf.java -  The class represents the leaf in the game.
src/pepse/world/trees/LeavesPerTree.java - The class represents the leaves per tree in the window.
src/pepse/world/trees/Tree.java - This class responsible for the creation and management of trees.
src/pepse/world/Avatar.java - The class represents the avatar object in the game.
src/pepse/world/Block.java - This class represents a single block (larger objects can be created from blocks).
src/pepse/world/Cloud.java - This function responsible for placing cloud in the game.
src/pepse/world/PerlinNoise.java - The class implement Perlin Noise Algorithm.
src/pepse/world/Sky.java - The class represents the sky.
src/pepse/world/Star.java - This class represents the start in the game that appears when the user presses 'S'.
src/pepse/world/Terrain.java - This class responsible for the creation and management of terrain.
src/pepse/PepseGameManager.java - This class manage the game.

UML explanation:
In the uml_before uml, we untended to:
1. Extend the Leaf from the Block object.
2. To create one class for the Trees creation that includes, creating all the random trunks in the window and
in addition responsible for creating all the leaves in the game.
The class FallingLeaves was supposed to be responsible for the falling leaves fetcher. We intended to create
dependency between that and the class Tree by sending each leaf in the FallingLeaves constructor that was
created in that class.
The class MivingLeavesInTheWind was supposed to be responsible for the leaves' movement in the wind fetcher.
We intended to create dependency between that and the class Tree by sending each leaf in the
MivingLeavesInTheWind constructor that was created in that class.
In the uml_after uml, we did instead:
1. The leaf extended from GameObject and not from Block Objects, because it was a different object than the
Block that was used in the tree's trunk.
2. We created a class Tree that responsible for creating the trunk and calling the class LeavesPerTree that
is independent from the class Tree. It just need the location of it's associated trunk that it gets in the
constructor.
3. The features: falling leaves and leave's movement is an extension of the leaves, so we decided to put it
there so that the class LeavesPerTree would be responsible for creating leaves and giving the any features.

Infinity World explanation:
Our world acts as an infinity world in that manner:
We created a larger window that the game's window that is wider than that window in 600 units to
the left and to the right of the game's window. Each time the GameManager's update method being called,
it checks whether the current game window reaches the corners of the larger window to the left or to the right.
If it does, it creates an extension of the random world to the left or to the right accordingly in 600 units to
that direction and removes all the object in the opposite direction in 600 units. So it moves the window by 600
units each time it gets to the larger units.

We created a class Tree that responsible for creating the trunk and calling the class LeavesPerTree that is
independent from the class Tree. It just needs the location of it's associated trunk that it gets in the constructor.
The features: falling leaves and leave's movement is an extantion of the leaves, so we decided to put it there
so that the class LeavesPerTree would be responsible for creating leaves and giving the any features.

Decision we took:
In the UML diagram before, we wanted to do the "Leaf" class extend from "Block" class. After that, we
decided that we prefer that the "Leaf" class would extend from the "GameObject" class.
The leaf extended from "GameObject" class and not from Block Objects, because it was a different object than
the Block that was used in the tree's trunk.
We wondered how to implement the Tree class. Whether to create leaves inside that, or to get each component:
trunk, one leave, all leaves, all trees, feathers as a different class. We though to let the the
all leaves class to call the leaf class, the all trees class to call the trunk class and get the features
class to extend from the leaf class.
Eventually we decided to do as described in the trees question, because in ned=eds less classes dependencies
and less inheritances but still each class has its own small responsibility.

Bonus Section:
1. We created random clouds in the window.
We created a class Cloud that responsible for creating clouds in the range of the large window
that larger than the current games window. It creates them in random location above the trees. Each times,
the window dimensions change, it creates new random clouds.
2. We created an option for a star that appears in the sky and falloff the sky each time the S key is pressed.
We created a class Star that creates this star in a constant location according to the new window location.
In the GameManager in the update function, it checks whether the S key was pressed. If so, it calls the class
Star. It allows maximum 10 times to press the key and doesn't allow creating a lot of stars in a quick press
on S by scheduling a flag that checks whether S was pressed.
