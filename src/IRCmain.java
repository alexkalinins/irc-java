
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class IRCmain {

    private static String nick;
    private static String username;
    private static String realName;
    private static String userCommand; //what user types
    private static String userMessage; //what client sends
    private static PrintWriter out;
    private static Scanner in;
    private static Scanner console;

    public static void main(String[] args) throws IOException, InterruptedException {

        //make scanner for user input
        console = new Scanner(System.in);

        //asks for nick, username, and realName
        System.out.print("Enter a nickname: ");
        nick = console.nextLine();
        System.out.print("Enter a username: ");
        username = console.nextLine();
        System.out.print("Enter your real name: ");
        realName = console.nextLine();

        //socket to freenode
        Socket socket = new Socket("chat.freenode.net", 6667);

        //input and output stream
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

        //commands
        write("NICK", nick);
        write("USER", username + " 0 * :" + realName);
        write("JOIN", "#reddit-dailyprogrammer"); //replace #reddit-dailyprogramer with channel

        while (in.hasNext()) {
            String serverMessage = in.nextLine();
            System.out.println("<<< " + serverMessage);
            if (serverMessage.startsWith("PING")) { //check if message STARTS with PING
                //splits message after first space
                String pingContents = serverMessage.split("", 2)[1];
                //replies to the server with a pong
                write("PONG", pingContents);
            }
            userCommand = console.nextLine();      
            if (userCommand != "") {
                userMessage = userCommand + "\r\n";
                System.out.println(">>>" + userCommand);
                out.print(":" + userMessage);
                out.flush();
            }
        }

        /*
        More IRC commands (haven't tested) :
        
        /join #channel    // Joins a channel
        /part [#channel]  // Parts a specified channel, or the current output channel
        /quit             // Sends a QUIT message and closes the connection
        /msg user private message // Sends a message to a user directly
        /nicks [#channel] // Lists the nicks joined to a specified channel, or the current output channel
        /channel #channel // Changes the current output channel
         */
        in.close();
        out.close();
        socket.close();

        System.out.println("Done!");

    }

    private static void write(String command, String message) {
        String fullMessage = command + " " + message;
        //>>> for things send from client
        System.out.println(">>>" + fullMessage);
        out.print(fullMessage + "\r\n"); //all commands must end with \r\n
        out.flush();
    }
}
