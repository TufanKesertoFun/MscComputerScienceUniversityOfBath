import java.io.*;

class Main {

  // main method
  // reads in input from the command line
  // and passes this input to the processCommand method in SRPN 

  public static void main(String[] args) {
    // Code to take input from the command line 
    // This input is passed to the processCommand
    // method in SRPN.java 
    SRPN sprn = new SRPN();

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
    try {
      //Keep on accepting input from the command-line
      while(true) {
        String command = reader.readLine();
        //Close on an End-of-file (EOF) (Ctrl-D on the terminal)
        if(command == null){
          //Exit code 0 for a graceful exit
          System.exit(0);
        }        
        //Otherwise, (attempt to) process the character
        sprn.processCommand(command);          
      }
    } 
    catch(IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  } 
}