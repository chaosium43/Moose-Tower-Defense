This was a game I made for my Grade 12 Computer Science ISU. It is inspired by Bloons Tower Defense 5, and plays similarly albeit with a couple modifications.

KNOWN BUGS:
Sometimes, the 4-0 Fire Tower upgrade's laser doesn't disappear at the end of a round. Not sure why that happens.
Also, there seems to be lag when you start a new game after one has already been played. I'm guessing
that's due to some sort of memory leak but I'm not sure about that either as I call the Java garbage collector
every time a new game is started.

HOW TO RUN: If you are running it from the command line, navigate to the directory where the repository is located.
You can check that you are in the correct directory by doing "pwd" and the last part of the path should say "Moose-Tower-Defense".

Then, run the following in your repository directory
<pre>
javac ISU\Game.java
java ISU.Game
</pre>

If you are running it in Visual Studio Code (the IDE I used to develop this program), ensure that you have "Microsoft's Extension Pack for Java" extension installed (https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and "Debugger for Java" installed (https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug).