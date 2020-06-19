# N-Body-Simulation
Simulating celestial body movements in the universe using the Barnes-Hut-Algorithm.
The complexity for calculating the forces on the bodies with direct sum is O(n^2) but we can take advantage of the fact that bodies with a specific distance to quad area can be approximated by calculating the total mass of the quad area and calculate its center of gravity. It is not 100% accurate but for bodies that are far enough, it is barely noticeable. 

The algorithm reduces the complexity to O(n log n).
To accelerate the simulation, the Leapfrog Method has been used.


![](https://im3.ezgif.com/tmp/ezgif-3-1213630c77e9.gif)
