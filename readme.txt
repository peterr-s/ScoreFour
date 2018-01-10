Notes for Assignment 9 (Sogo AI)
--------------------------------------------------------------------------------

o We recommend using the eclipse IDE for working on this assignment. Create a
  new Java project and import the "src" folder.

o The game gui can be started with the main method of the SogoGui class. To be
  able to use your agent in gui, you will need to add it to the list of agents
  in this class.

o For testing your agent, please use the command line option "-Xmx2g" which
  limits the size of the JVM heap to 2GB (in eclipse you can add this under
  "Run -> Run Configurations -> Sogo -> Arguments -> VM arguments"). If your
  agents crosses this threshold, an OutOfMemoryError will be thrown which can be
  caught if you want.

o For experimenting, you can change the time per move by adjusting the constant 
  PLAYER_TIMEOUT in the class SogoGui (time in ms). Please note that for
  the evaluation, the value 10000 will be used.

o If your agent is still running after the time limit has passed, your agent will
  lose unless you have registered a preliminary move with updateMove (see next
  point). The thread running your agent will also receive an interrupt signal.
  This does not automatically kill the thread, but it will set a flag which can
  be checked with

    Thread.currentThread().isInterrupted()

  After receiving the interrupt, your agent must stop running at the next
  convenient time to free up resources.

o Your submission should consist of (the source code for) a class implementing
  the SogoPlayer interface, you are not allowed to modify any other file in the
  program, so please make sure your agent works with the base version of the
  game as distributed.

  Important: please include some documentation for your agent (either as
  comments in the java file or as a separate document). Document which algorithm
  you are using, what the idea behind your heuristic/evaluation function is, etc.

o The most important part of the agent is the method generateNextMove. The
  argument to this method is an instance of the SogoGameConsole interface. This
  interface gives access to the following methods:

  - getGame: returns the current game state

  - getTimeLeft: returns the milliseconds left for the current turn

  - updateMove: registers the move you want to play. This can be called multiple
    times to update the selected move and it is advisable to call this method at
    least once early in the turn to avoid forfeiting the game if the computation
    takes longer than expected.

o If possible, please avoid splitting your implementation across multiple files.
  If you need to create additional classes, please create them as inner classes
  (even if this might not be software engineering best practice).

o Your code cannot rely on any libraries other than the java class libraries for
  Java 1.8

o Your agent should be single-threaded. While a multi-threaded agent will get
  marks for the assignment, it will be disqualified from the tournament.

o The class SogoGame contains several methods that provide information on the
  current state of the game, which can of course be used by your agent.

o The game board is represented as a simple 3D array of elements indicating whether
  a position is empty or occupied by Player1 or Player2. The last axis of the
  array is the one along which the pieces are stacked


o The human player is controlled with the mouse:

  - Player 1 is black and has the first move

  - A grey preview piece indicates the position that would be selected by a click

  - you can rotate the view with the right mouse button to get a better look
    at the game board

  - the last places bead is a slightly different color (if you want to change any
    the colors for the game, you the are defined as static in SogoGamePanel.java
    and Statuspanel.java)

o Two agents are included in this framework to allow you to test your agent:

  - MrRandom: A very primitive agent that selects its moves randomly from the
    list of legal moves. Basically any agent should be able to beat this since
    it doesn't even react to an imminent four-in-a-row.

  - MrNovice: A minimax agent with a simple cutoff heuristic. After reaching a
    given depth in the gametree (or finding a leaf node) the current position
    will be evaluated by looking at how many beads each player has on lines
    that are still open. For a depth larger than 4, this agent is very likely
    to run out of time.

o If you find any bugs in the code, please report them to one of the TAs

--------------------------------------------------------------------------------

Author:
Hauke Neitzel
