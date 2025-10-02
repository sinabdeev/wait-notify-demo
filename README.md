#### A test project to demonstrate how the Object.wait/notifyAll methods work.

How to use:
1. Run the main method in the Main class.
2. Send several GET requests to localhost:8080/factorial. The requests will pause and wait.
3. Send exactly the same number of POST requests to localhost:8080/produce. The requests from the previous step will complete one after another.