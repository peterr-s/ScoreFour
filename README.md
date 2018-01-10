# Sogo/Score Four (3D Connect 4) AI

This is a simple AI that I wrote for a longer homework/end of semester assignment (Artificial Intelligence, WS 2017, Andreas Zell).

A working project was provided, so the assignment was just to add another AI type (mine is the one named after me). The base project is almost entirely as provided, though I did make a few small tweaks for the purposes of my testing. I also wrote the Mr. Savant AI to simulate the AI that I knew they would be grading against (not provided). It is essentially just a combination of the Mr. Novice AI's heuristic with my alpha-beta pruned minmax implementation.

## approach

My submission was a basic minmax with alpha-beta pruning. The heuristic it uses is based on how close the player is to controlling each possible winning line in the game, with very heavy weight given to a win and the rest being a cubic progression. This is because the difficulty of lengthening an attempted line is often worse than exponential, with each turn not only offering the opponent a chance to block but also increasing their incentive to do so. Lines on which the opponent has lain a piece are no use, but it is still important to hinder their progress, so those lines are incentivized with the same weighting.

The AI also tries to adapt its search depth to the hardware on which it is running and the time limit provided; if it notices that it used only a small enough fraction (based on the branching factor) of the allowed time on a turn, it will search deeper in the next one. If it runs out of time it will reduce its search depth. However, it will always register a move immediately so it has something to fall back on.

## use

There is a runnable JAR provided. The submission was required to be an Eclipse project for whatever reason so that is what it is.
