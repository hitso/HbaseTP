import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by Wilson on 21/10/2016.
 */
public class HbaseTP{
    Put p;
    public static void main(String[] args) throws IOException {
        // Initial config for hbase
        Configuration config = HBaseConfiguration.create();
        // We then create a new table based on the table "wlua" created before
        HTable table = new HTable(config, "wlua");

        // This scanner is used to get the input from the console
        Scanner sc = new Scanner(System.in);
        // These variables are the data we ask the user to give : first is the firstname, second are the information about him/her,
        // third are the friends name
        String firstName = "";

        String mail = "", address = "";

        String bestFriend = "";
        String[] friendList = null;
        int friendCount = 0;

        // This variable will serve to know if yes or no the user has entered its name at the beginning
        boolean hasEnteredFirstName = false;

        // This variable is the choice the user made to enter the information asked OR the information (for the friends which is a list)
        String choice = "";
        int choiceNumber = 0;

        // This variable will let us know if the choice made is correct
        boolean correctChoice = false;

        // First variable is for one person entry in the table, and the second is for everyone, meaning if it is set to true, the program will end
        boolean hasFinishedFillingOnePersonData = false;
        boolean hasFinishedFillingEveryOneData = false;

        // This is for the mandatory best friend
        boolean hasBestFriend = false;

        // This will be the input to the table, and we'll set it when we will have the first name
        Put p;

        // Now we have set the base. We need to fill our table with the data of our people
        // We so ask them to enter their name and display what data they would like to fill after

        System.out.println("Welcome to the hBase test program !\n\n");
        System.out.println("You can enter and save your data here, namely your personnal information as well as your friends' one\n\n");
        System.out.println("To start with, let's choose what you want to insert : type the number corresponding to your choice and press Enter\n");

        while (!hasFinishedFillingEveryOneData) {
            do {
                if (!hasEnteredFirstName) {
                    do {
                        System.out.println("1 - Enter your first name (note that it is the first mandatory information)\n\n");
                        choice = sc.nextLine();
                        // We check if it's equal to "1" so we can proceed to get the firstname of the person
                        if (choice.trim().equals("1")) {
                            correctChoice = true;
                        }
                    } while (!correctChoice);
                    System.out.println("Please enter your first name:\n");
                    // We get the first name and then we set hadEntered first name to true so we can proceed with the personnal/friends info
                    firstName = sc.nextLine();
                    hasEnteredFirstName = true;
                } else {
                    System.out.println("1 - Enter your personnal information\n");
                    System.out.println("2 - Enter your friends' names\n");
                    System.out.println("3 - Finish and enter another person information\n");
                    System.out.println("(Note that you have to enter your best Friend inside choice 2)\n");
                    choice = sc.nextLine();
                    // We do a switch on the choice of the person
                    choiceNumber = Integer.parseInt(choice);
                    switch(choiceNumber){
                        case 1:
                            // Entering personnal information
                            System.out.println("\n1 - Enter your email\n");
                            System.out.println("2 - Enter your address\n");
                            System.out.println("Type anything else to exit your personnal information\n");
                            choice = sc.nextLine();
                            if (choice.trim().equals("1")){
                                System.out.println("\nPlease enter your email\n");
                                mail = sc.nextLine();
                            } else if (choice.trim().equals("2")){
                                System.out.println("\nPlease enter your address\n");
                                address = sc.nextLine();
                            }
                            break;
                        case 2:
                            // Entering your friend's information
                            System.out.println("\n1 - Enter your best friend name\n");
                            System.out.println("2 - Enter your other friends' name\n");
                            System.out.println("Type anything else to exit your personnal information\n");
                            if (choice.trim().equals("1")){
                                System.out.println("\nPlease enter your best friend name (MANDATORY !!!)\n");
                                // Getting the best friend name and then adding to the put for the table
                                bestFriend = sc.nextLine();
                            } else if (choice.trim().equals("2")){
                                System.out.println("\nPlease enter your friends name, and then type -exit to stop \n");
                                // Getting the friends' names and then adding to the put for the table
                                choice = sc.nextLine();
                                while(!(choice.trim().equals("-exit"))){
                                    // We add the friend's name in the friend list, and increment the counter and then get another name
                                    friendList[friendCount] = choice;
                                    friendCount ++;
                                    choice = sc.nextLine();
                                }
                            }
                            break;
                        case 3:
                            if (!hasBestFriend){
                                // checking if the best friend is inserted, otherwise the person has to do it here
                                System.out.println("You have to enter your best friend's name below : no escape x_x !\n");
                                bestFriend = sc.nextLine();
                                hasBestFriend = true;
                            }
                            hasFinishedFillingOnePersonData = true;
                            System.out.println("\n");
                            break;
                        default:
                            System.out.println("Incorrect choice ! Please choose between the choices above\n");
                            break;
                    }
                }
            } while (!hasFinishedFillingOnePersonData);

            // We add everything in the table
            p = new Put(Bytes.toBytes(firstName));
            if (!(mail.equals(""))){p.add(Bytes.toBytes("info"), Bytes.toBytes("email"), Bytes.toBytes(mail));}
            if (!(address.equals(""))){p.add(Bytes.toBytes("info"), Bytes.toBytes("address"), Bytes.toBytes(address));}
            if (!(address.equals(""))){p.add(Bytes.toBytes("friends"), Bytes.toBytes("BFF"), Bytes.toBytes(bestFriend));}
            // We loop through the friend List
            for (int i = 0; i < friendCount; i++) {
                p.add(Bytes.toBytes("friends"), Bytes.toBytes("other"), Bytes.toBytes(friendList[i]));
            }
            // We add the row to the table
            table.put(p);
            System.out.println("\n Congrats, you have finished entering a person's data\n");
            System.out.println("If you want to do it again, type 1, else any input will close the program\n");
            choice = sc.nextLine();
            if (!(choice.trim().equals("1"))){
                // If the person wants to stop we can then enter each information in the table
                hasFinishedFillingEveryOneData = true;
            } else {
                // We reset every field
                firstName = "";
                mail = "";
                address = "";
                choice = "";
                choiceNumber = 0;
                bestFriend = "";
                friendList = null;
                friendCount = 0;
                hasEnteredFirstName = false;
                hasBestFriend = false;
                hasFinishedFillingOnePersonData = false;
                correctChoice = false;
            }
        }
        return;
    }
}