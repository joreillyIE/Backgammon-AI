# Backgammon-Bot

By Joanne Reilly, University College Dublin

This was a month-long assignment to create a bot that would play backgammon
against other players and AI. The bot generates all possible plays and rates each
possible play based on the probability that it will lead to a win, based on several
weighted scores for each feature. It then picks the play with the highest probability.
Weights were decided and tweaked during testing, during which the bot played
against itself. Ranked fifth in our class-wide Backgammon tournament.

Backgammon-Bot.java was written to be run along side the included version of that Backgammon
application.

The main idea was to try and program the bot to pick the best play at all times.
Naturally this is much easier to do in some board positions than others. In the
bear off situation it is very easy for the bot to make a decision whereas during
mid-game it is challenging for the bot to make the “best decision” as there are
significantly more moves available. I tried altering the code several times and
using different weights for the bot to prioritise some decisions over others in
order to get the most efficient playstyle.

The most basic thing to do first of all was to generate a list of all legal plays.
From there I generated all possible resulting board positions from each play.
Then, the bot looks through the board positions and generates a score for each
possible play. Finally I wanted the bot to then pick the play with the highest
score. The highest score being the score most likely to result in a win.

In order for the bot to make decisions I had to look at factors such as:
- If a hit is available
- If the bot has a checker on the bar or not
- What moves the opponent may have available (such as moving onto an
empty pip our bot can get hit off of).

I set up a method called “probabilityOfWin”. This method reads the board in its
current state and calculates the probability of winning from that position. It does
this by looking at a number of different features:
- Pip count difference
- Block-blot difference
- Number of home board blocks
- Length of prime with captured checker
- Anchors in opponents home board
- Number of escaped checkers (not in opponents home board)
- Number of checkers in own home board
- Number of points covered
- Number of checkers taken off

The objective of all of these methods was to calculate what move the bot should
make.
