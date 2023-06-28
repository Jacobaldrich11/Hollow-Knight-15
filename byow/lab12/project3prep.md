# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: The given algorithm compared to mine worked at a much higher level.
I should've tried to write helper functions for my program to make things easier.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: Both of these forms of world generation are complex and should be handled using helper functions. One is 
more like mashed potatoes, while the other is a scalloped potatoes meal. They both require care and attention, but one 
is higher level.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: I should try and write methods to create individual rooms and hallways before I handled the random 
implementation of the rooms and such.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: A hallway is a 1-2 tile width slab of tiles that you can walk down, while a room is more flat and 
evenly distributed. Both of the areas have tiles you can walk on, and walls that prevent you from moving out of bounds.
