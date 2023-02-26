# flappyAI
Flappy Bird game clone with game playing AI using evolutionary algorithms

# Neural Network
The neural network AI for deciding when to jump is very simple and not fully reflective of a proper neural network (no activation function, back propagation). It consists of one input layer(4 nodes) and one output layer (one node). The four input nodes are x-distance (width) from the pipes, y-distance from top and bottom pipe (height), and current velocity. The weighting for x-distance and velocity is scaled linearly, whereas the y-distances are scaled reciprocally. This is because a small y-distance from one of the pipes would mean that the bird is close to the pipe, thus should increase the neuron's activation. On the other hand, a higher velocity would mean that the AI should be more inclined to jump to maintain a certain level.

All weights are randomly initiated at a certain range. The threshold for jumping is also randomly initiated

# Evolutionary Algorithm 
35 AI birds were created and play the game concurrently. If a bird hits the pipe, it is eliminated and it's performance (passing each pipe increases the score by 1) is recorded. Once all the birds are eliminated, then the next generation begins. The top 5 best performing birds (based on score) are able to pass on their weights (with a smaller random variation) to the new 30 birds of the next generation. The other 5 birds will be randomly initialized with new weights to provide sufficient mutation/genetic variation in the simulation. 

# To Run
please run the flappybird.java file which includes the main method. Press enter to run the simulation and watch the various birds learn to fly! 

# Next steps
Making the game progressively harder or introducing new mechanics (moving pipes?) would increase the generalizability and performance of the AI! Additionally, I think I could add some sort of reinforcement learning into the simulation, where each jump through a pair of pipes can be dynamically graded based on the bird's y distances and velocities. This could help teach the AI to aim to always be in the middle of the two pipes.

