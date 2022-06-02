import java.nio.channels.SelectableChannel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.DoubleBinaryOperator;
import java.lang.Math;
import javax.swing.event.SwingPropertyChangeSupport;

//import java.util.InputMismatchException;
import java.util.Arrays;
import java.util.List;

public class isg224 {
    static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:CSE241";

    /**
     * @param args
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean pre_conn = true;
        int userType = 0;
        int opChoice = 0;
        // int operation = 0;

        while (pre_conn) {
            System.out.println("Welcome! Please Log In to Oracle.");
            System.out.print("Enter Oracle User ID: ");
            String userID = in.nextLine();
            System.out.print("Enter Oracle Password: ");
            String pass = in.nextLine();
            try (
                    Connection conn = DriverManager.getConnection(DB_URL, userID, pass);) {

                ResultSet myRs;
                pre_conn = false;

                // System.out.println("successful connection");
                System.out.println("Welcome to NUMA Residential Data Management App \nA Higher Quality of Living!");

                do {
                    userType = chooseUser(in);

                    switch (userType) {
                        case 1:
                            do {
                                opChoice = propManagerMenu(in);

                                switch (opChoice) {
                                    case 1: // record visit data
                                        in.nextLine();
                                        String prosName = getName(in);
                                        String phoneNum = getNumber(in);

                                        int P_id = getP_id(in);
                                        // System.out.println("chcek 1");
                                        int pros_id = newPros_id(conn) * 2;
                                        // int pros_id = newPros_id(conn);
                                        // System.out.println("check 2");
                                        String locationVisited = getLocation(P_id);
                                        String tem_Query;

                                        tem_Query = "INSERT INTO pros_tenant("
                                                + "pros_id, name, phone_num, location_visited, p_id) VALUES "
                                                + "(?,?,?,?,?) ";
                                        PreparedStatement svisit = conn.prepareStatement(tem_Query);

                                        svisit.setInt(1, pros_id);
                                        svisit.setString(2, prosName);
                                        svisit.setString(3, phoneNum);
                                        svisit.setString(4, locationVisited);
                                        svisit.setInt(5, P_id);

                                        int checkv = svisit.executeUpdate();

                                        if (checkv < 0) {
                                            System.out.println("Something Went Wrong. Please Try Again.");
                                        } else {
                                            System.out.println(
                                                    "Visit Recorded! Visitor named: " + prosName + "ID:" + pros_id);
                                        }
                                        break;
                                    case 2: // record lease data
                                        System.out.println("The following apartments can be leased:\n");
                                        availableApt(conn);
                                        in.nextLine();
                                        String newAptLease = getAptChoice(in, conn);
                                        int rentLease = chooseRent(in);
                                        int durationLease = chooseDuration(in);
                                        // in.nextLine(); // can remove case 2 was working wen removed
                                        String dateLease = getDate(in);
                                        in.nextLine();
                                        int securityLease = chooseSecurity(in);
                                        tem_Query = "INSERT INTO Lease("
                                                + "apt_num, rent, duration, date_signed,securitydeposit) VALUES "
                                                + "(?,?,?,?,?) ";
                                        PreparedStatement s = conn.prepareStatement(tem_Query);

                                        s.setString(1, newAptLease);
                                        s.setInt(2, rentLease);
                                        s.setInt(3, durationLease);
                                        s.setString(4, dateLease);
                                        s.setInt(5, securityLease);

                                        int checkL = s.executeUpdate();

                                        if (checkL < 0) {
                                            System.out.println("Something Went Wrong. Please Try Again.");
                                        } else {
                                            System.out.println(
                                                    "Lease Recorded! for Apt: " + newAptLease);
                                        }

                                        break;

                                    case 3: // record move out
                                        allApt(conn);
                                        String aten = " ";
                                        boolean run = true;
                                        in.nextLine();
                                        do {
                                            try {

                                                System.out.print(
                                                        "Please enter tenant_id that is Moving Out from list above : ");
                                                aten = in.nextLine();
                                                String maxQuery = "SELECT tenant_id FROM tenant";
                                                // System.out.println("checkpoint 0 ");

                                                PreparedStatement sMove = conn.prepareStatement(maxQuery);

                                                // System.out.println("checkpoint 1 ");
                                                ResultSet myrs3 = sMove.executeQuery();

                                                List<String> rowValues = new ArrayList<String>();
                                                // System.out.println("checkpoint 4 ");
                                                while (myrs3.next()) { // Position the cursor 3
                                                    rowValues.add(myrs3.getString(1));
                                                }
                                                if (rowValues.contains(aten)) {
                                                    System.out.println("You chose choice:" + aten);
                                                    run = false;
                                                } else {
                                                    System.out.println("try again and choose a valid tenant_id");
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Invalid Input");
                                            }
                                        } while (run);

                                        // based on the inputted client ID print welcome message

                                        String dateofMove = getDateMoveout(in);

                                        String move_Query = "update tenant set moveout_date = ? where tenant_id = ? ";

                                        PreparedStatement smoveout = conn.prepareStatement(move_Query);
                                        smoveout.setString(1, dateofMove);
                                        smoveout.setString(2, aten);
                                        int checkmoves = smoveout.executeUpdate();

                                        if (checkmoves < 0) {
                                            System.out.println("Something Went Wrong. Please Try Again.");
                                        } else {
                                            System.out.println(
                                                    "Moveout Recorded! for tenant " + aten);
                                        }

                                    case 4: // logging off the prop manager portal.
                                        break;
                                }

                            } while (opChoice != 4);
                            break;
                        case 2:
                            allApt(conn);
                            String t_id = " ";
                            String ten = " ";
                            boolean run = true;
                            in.nextLine();
                            do {
                                try {

                                    System.out.print(
                                            "Please enter your tenant_id: ");
                                    t_id = in.nextLine();
                                    // System.out.println("you chose:" + t_id);
                                    String maxQuery = "SELECT tenant_id FROM tenant";
                                    // System.out.println("checkpoint 0 ");

                                    PreparedStatement sMove = conn.prepareStatement(maxQuery);

                                    // System.out.println("checkpoint 1 ");
                                    ResultSet myrs3 = sMove.executeQuery();

                                    List<String> rowValues = new ArrayList<String>();
                                    // System.out.println("checkpoint 4 ");
                                    while (myrs3.next()) { // Position the cursor 3
                                        rowValues.add(myrs3.getString(1));
                                    }
                                    if (rowValues.contains(t_id)) {

                                        System.out.println(
                                                "\n-------------Hello welcome to your tenant portal--------------\n");
                                        run = false;
                                    } else {
                                        System.out.println("try again and choose a valid tenant_id");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Invalid Input");
                                }
                            } while (run);

                            String tName = "select name from tenant where tenant_id = " + t_id;
                            PreparedStatement tmoveout2 = conn.prepareStatement(tName);
                            ResultSet myrs2Ten = tmoveout2.executeQuery(tName);
                            myrs2Ten.next();
                            String tenName = myrs2Ten.getString(1);
                            System.out.println("Welcome Tenant:" + tenName);

                            do {
                                opChoice = tenantMenu(in);

                                switch (opChoice) {
                                    case 1: // check payment status (amount due, if any)
                                        String maxQuery = "SELECT *  FROM Payment WHERE tenant_id = ?";

                                        PreparedStatement sMove = conn.prepareStatement(maxQuery);
                                        sMove.setString(1, t_id);

                                        ResultSet myrs3 = sMove.executeQuery();
                                        Double total = 0.0;
                                        Double pay_amnt = 0.0;
                                        Double due = 0.0;

                                        List<Double> rowValues = new ArrayList<Double>();
                                        while (myrs3.next()) { // Position the cursor 3
                                            rowValues.add(myrs3.getDouble("total_paid"));
                                            pay_amnt = myrs3.getDouble("pay_amount");

                                        }
                                        total = rowValues.get(0);
                                        due = total - pay_amnt;

                                        if (due != 0) {

                                            System.out.println("You have a unpaid dues of: " + due);
                                            break;
                                        } else {
                                            System.out.println("You have already made your payment of: " + pay_amnt);
                                            break;

                                        }

                                    case 2: // , make rental payment,
                                        String payQuery = "SELECT *  FROM Payment WHERE tenant_id = ?";

                                        PreparedStatement payMove = conn.prepareStatement(payQuery);
                                        payMove.setString(1, t_id);

                                        ResultSet myrs4 = payMove.executeQuery();
                                        Double totalpay = 0.0;
                                        Double pay_amntpay = 0.0;
                                        Double duepay = 0.0;

                                        List<Double> rowValuespay = new ArrayList<Double>();
                                        while (myrs4.next()) { // Position the cursor 3
                                            rowValuespay.add(myrs4.getDouble("total_paid"));
                                            pay_amntpay = myrs4.getDouble("pay_amount");

                                        }
                                        totalpay = rowValuespay.get(0);
                                        duepay = totalpay - pay_amntpay;

                                        if (duepay < 0) {
                                            System.out.println("You have a unpaid dues of: " + duepay);
                                            int tempPay = getPay(in);
                                            int payMethod = getPayMethod(in);
                                            String Method = " ";
                                            if (payMethod == 1) {
                                                Method = "VENMO";
                                            } else if (payMethod == 2) {
                                                Method = "Credit";
                                            } else if (payMethod == 3) {
                                                Method = "Cashapp";
                                            }
                                            in.nextLine();
                                            String datepaid = getDatePayed(in);

                                            String tmove_Query = "update payment set total_paid = ?, pay_method = ?,pay_date = ? where tenant_id = ? ";

                                            PreparedStatement tmoveout = conn.prepareStatement(tmove_Query);
                                            tmoveout.setInt(1, tempPay);
                                            tmoveout.setString(2, Method);
                                            tmoveout.setString(3, datepaid);
                                            tmoveout.setString(4, t_id);
                                            int checkmoves = tmoveout.executeUpdate();

                                            if (checkmoves < 0) {
                                                System.out.println("Something Went Wrong. Please Try Again.");
                                            } else {
                                                System.out.println(
                                                        "You payed dues for t_id: " + t_id + " on Date: " + datepaid);
                                                break;
                                            }
                                            break;

                                        } else {
                                            System.out.println("You have already made your payment of: " + pay_amntpay);
                                            run = false;
                                            break;
                                        }

                                    case 3: // add person or pet,
                                        // System.out.println("Enter your Dependants name; ");
                                        in.nextLine();
                                        String depName = getNameDep(in);
                                        String relationship = getRelation(in);
                                        String dep_Query = "update other_resident set name = ?, relationship = ? where tenant_id = ? ";

                                        PreparedStatement depde = conn.prepareStatement(dep_Query);
                                        depde.setString(1, depName);
                                        depde.setString(2, relationship);
                                        depde.setString(3, t_id);
                                        int checkdep = depde.executeUpdate();

                                        if (checkdep < 0) {
                                            System.out.println("Something Went Wrong. Please Try Again.");
                                        } else {
                                            System.out.println(
                                                    "Dependant Added! for tenant " + t_id + " Welcome to the family : "
                                                            + depName);
                                        }
                                        break;

                                    case 4: // setmove out date,
                                        // System.out.println("Enter your planned Move out Date; ");
                                        in.nextLine();
                                        String moveDate = getDateMoveout(in);
                                        String tmove_Query = "update tenant set moveout_date = ?, moveplanned = ? where tenant_id = ? ";

                                        PreparedStatement tmoveout = conn.prepareStatement(tmove_Query);
                                        tmoveout.setString(1, moveDate);
                                        tmoveout.setString(2, "yes");
                                        tmoveout.setString(3, t_id);
                                        int checkmoves = tmoveout.executeUpdate();

                                        if (checkmoves < 0) {
                                            System.out.println("Something Went Wrong. Please Try Again.");
                                        } else {
                                            System.out.println(
                                                    "Moveout Planned! for tenant: " + tenName + "on the Date:"
                                                            + moveDate);
                                        }
                                        break;
                                    case 5: // update personal data
                                        in.nextLine();
                                        String newPhone = getnewNumber(in);
                                        String phoneQ = "update tenant set phone = ? where tenant_id = ? ";

                                        PreparedStatement phoneS = conn.prepareStatement(phoneQ);
                                        phoneS.setString(1, newPhone);
                                        phoneS.setString(2, t_id);
                                        int checkPhone = phoneS.executeUpdate();

                                        if (checkPhone < 0) {
                                            System.out.println("Something Went Wrong. Please Try Again.");
                                        } else {
                                            System.out.println(
                                                    "Your new updated contact is your new Phone#: " + newPhone);
                                        }
                                        break;
                                    case 6:
                                        break;
                                }

                            } while (opChoice != 6);
                            break;

                        case 3:
                            do {
                                opChoice = businessManagerMenu(in);

                                switch (opChoice) {
                                    case 1: // view all tennats in a chosen property
                                        allProps(conn);
                                        in.nextLine();
                                        String whatProp = chooseProp(in);
                                        alltents(whatProp, conn);
                                        break;

                                    case 2: // display taxes incurrd on a chosen property
                                        allProps(conn);
                                        in.nextLine();
                                        String propWhat = chooseProp(in);
                                        findTaxes(propWhat, conn);
                                        break;

                                    case 3: // display revenuw for all of NUMA
                                        allProps(conn);
                                        in.nextLine();
                                        String proppy = chooseProp(in);
                                        findRev(proppy, conn);
                                        break;
                                    case 4: // logging off the prop manager portal.
                                        break;
                                }

                            } while (opChoice != 4);
                            break;
                        case 4: // logging off app
                            break;

                    }

                } while (userType != 4);// dowhile for the app's general menu

            } catch (SQLException se) {
                se.printStackTrace();
                System.out.println("[Error]: Connect Error. Re-enter login data:");
            }
        }

    }// while loop ends here for preconnection check!

    /**
     * @param input
     * @return int
     */
    public static int chooseUser(Scanner input) { // intro menu so users may select who they are based on what actions
                                                  // they wish to perform/
        int choice = 0;
        do {
            System.out.println("\nEnter the corresonding number for who you are:  ");
            System.out.println(" 1 for Property Manager");
            System.out.println(" 2 for Tenant");
            System.out.println(" 3 for Business Manager");
            System.out.println(" 4 to leave NUMA portal");

            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1 && choice <= 4)
                    break;
                else
                    System.out.println("Invalid Request. Please choose from options 1 through 4");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return int
     */
    ///////////////////////////// Prop MANAGER THINGS////////////////////////////
    ///////////////////////////// Prop MANAGER THINGS////////////////////////////
    ///////////////////////////// Prop MANAGER THINGS////////////////////////////
    ///////////////////////////// Prop MANAGER THINGS////////////////////////////
    ///////////////////////////// Prop MANAGER THINGS////////////////////////////
    ///////////////////////////// Prop MANAGER THINGS////////////////////////////
    ///////////////////////////// Prop MANAGER THINGS////////////////////////////
    ///////////////////////////// Prop MANAGER THINGS////////////////////////////

    public static int propManagerMenu(Scanner input) { // the list of actions a property manager may choose to do.
        int choice = 0;
        do {
            System.out.println("\nHow may we help you today:  ");
            System.out.println(" 1: Record Visit Data");// update
            System.out.println(" 2: Record Lease Data");// update
            System.out.println(" 3: Record Move Out");// update
            System.out.println(" 4: Log Out"); // exit the portal
            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1 && choice <= 4)
                    break;
                else
                    System.out.println("Invalid Input. Please choose from options 1 through 4.");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return String
     */
    public static String getNumber(Scanner input) {
        System.out.println("Please Enter the Visitor's Phone(###-###-####):) ");
        String phoneNum = input.nextLine();
        do {
            if (phoneNum.matches("^(\\d{3}[- .]?){2}\\d{4}$")) {
                break;
            } else {
                System.out.println("Invalid Input. Please enter an Phone Number (###-###-####) as your input.");
            }
        } while (true);
        return phoneNum;
    }

    /**
     * @param input
     * @return String
     */
    public static String getnewNumber(Scanner input) {
        System.out.println("Please Enter the Visitor's new Phone(###-###-####):) ");
        String phoneNum = input.nextLine();
        do {
            if (phoneNum.matches("^(\\d{3}[- .]?){2}\\d{4}$")) {
                break;
            } else {
                System.out.println("Invalid Input. Please enter an Phone Number (###-###-####) as your input.");
            }
        } while (true);
        return phoneNum;
    }

    /**
     * @param input
     * @return String
     */
    public static String getName(Scanner input) {
        System.out.println("Please Enter the Visitor's Name: ");
        String name = input.nextLine();

        return name;
    }

    /**
     * @param input
     * @return String
     */
    public static String getNameDep(Scanner input) {
        System.out.println("Please Enter the Dependants's Name: ");
        String name = input.nextLine();

        return name;
    }

    /**
     * @param input
     * @return String
     */
    public static String getRelation(Scanner input) {
        System.out.println("Please Enter the Dependant's relationship: ");
        System.out.println("1 for Family ");
        System.out.println("2 for Friend ");
        System.out.println("3 for Pet ");
        String name = " ";
        int choice = 0;
        if (input.hasNextInt()) {
            choice = input.nextInt();
            if (choice >= 1 && choice <= 3) {
                if (choice == 1) {
                    name = "Family";

                } else if (choice == 2) {
                    name = "Friend";
                } else if (choice == 3) {
                    name = "Pet";
                } else
                    System.out.println("Invalid Input. Please choose from options 1 through3.");
            }
        } else {
            input.nextLine();
            System.out.println("Invalid Input. Please enter an integer as your input.");
        }

        return name;
    }

    /**
     * @param p_id
     * @return String
     */
    public static String getLocation(int p_id) {
        String location = " ";

        if (p_id == 1) {
            location = "Grand View Dr.";
        } else if (p_id == 2) {
            location = "Vanbrooke Blvd.";
        } else if (p_id == 3) {
            location = "Hunt Rd.";
        } else if (p_id == 4) {
            location = "Jordan Ave.";
        } else if (p_id == 5) {
            location = "Lebron Blvd.";
        }

        return location;
    }

    /**
     * @param input
     * @return int
     */
    public static int getP_id(Scanner input) {
        int choice = 0;
        do {
            System.out.println("Please Enter a corresponding number for the Property Visited: ");
            System.out.println(" 1: Grand View Dr.");// update
            System.out.println(" 2: Vanbrooke Blvd.");// update
            System.out.println(" 3: Hunt Rd.");// update
            System.out.println(" 4: Jordan Ave."); // exit the portal
            System.out.println(" 5: Lebron Blvd."); // exit the portal

            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1 && choice <= 5)
                    break;
                else
                    System.out.println("Invalid Input. Please choose from options 1 through 5.");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return int
     */
    public static int getPay(Scanner input) {
        int choice = 0;
        do {
            System.out.println("Please Enter how much you want to pay: ");

            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1 && choice <= 9999)
                    break;
                else
                    System.out.println("Invalid Input. Please choose from options 1 through 9999.");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter a valid payment from 1-9999.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return int
     */
    public static int getPayMethod(Scanner input) {
        int choice = 0;
        do {
            System.out.println("Please Enter how you want to pay: ");
            System.out.println("1 for Venmo: ");
            System.out.println("2 for Credit Card: ");
            System.out.println("3 for Cashapp: ");

            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1 && choice <= 3)
                    break;
                else
                    System.out.println("Invalid Input. Please choose from options 1 through 3.");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter a valid payment from 1-3.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param conn
     * @return int
     */
    public static int newPros_id(Connection conn) {
        int newidd = (int) Math.floor(Math.random() * (5555 - 1 + 1) + 1);
        int newid = 0;
        try {
            String maxQuery = "select max(pros_id) from pros_tenant";
            PreparedStatement s = conn.prepareStatement(maxQuery);
            ResultSet myrs = s.executeQuery();

            if (myrs.next()) {
                newid = myrs.getInt(1) + newidd;
                return newid;
            } else {
                newid = 1;
            }

        } catch (SQLException e) {
            System.out.println("Something is wrong with connection to the NUMA database.");
        }
        return newid;
    }

    /**
     * @param conn
     */
    public static void availableApt(Connection conn) {
        // int newid = 0;

        try {
            String maxQuery = "SELECT apt_num FROM apartment WHERE apt_num NOT IN (SELECT apt_num FROM Lease)";
            PreparedStatement s = conn.prepareStatement(maxQuery);
            ResultSet myrs = s.executeQuery();
            while (myrs.next()) { // Position the cursor 3
                String apts = myrs.getString(1); // Retrieve only the first column value
                System.out.println(apts);
                // Print the column value
            }

        } catch (SQLException e) {
            System.out.println("Something is wrong with connection to the NUMA database.");
        }
    }

    /**
     * @param conn
     */
    public static void allApt(Connection conn) {
        // int newid = 0;

        try {
            String maxQuery = "SELECT * FROM tenant";
            PreparedStatement s = conn.prepareStatement(maxQuery);
            ResultSet myrs = s.executeQuery();
            System.out.println("tenant_id               name               ssn              phone            bank");

            while (myrs.next()) { // Position the cursor 3
                String apts = myrs.getString(1); // Retrieve only the first column value
                int id = myrs.getInt("tenant_id");
                String name = myrs.getString("name");
                String ssn = myrs.getString("ssn");
                String phone = myrs.getString("Phone");
                String bank = myrs.getString("bank");

                System.out.println(id + "      " + name + "               " + ssn + "      " + phone + "     " + bank);
                // System.out.printf("[%4s %-4d %10f %-10s %-10s\n]%n", id, name, ssn, phone,
                // bank);

                // Print the column value
            }

        } catch (SQLException e) {
            System.out.println("Something is wrong with connection to the NUMA database.");
        }
    }

    /**
     * @param input
     * @param conn
     * @return String
     */
    public static String getAptChoice(Scanner input, Connection conn) {
        String choice = " ";
        do {
            System.out.println("Please Enter the apartment number for a new lease: ");
            if (input.hasNextInt()) {
                choice = input.nextLine();
                System.out.println("your input is: " + choice);
                try {
                    String maxQuery = "SELECT apt_num FROM apartment WHERE apt_num NOT IN (SELECT apt_num FROM Lease)";
                    PreparedStatement s = conn.prepareStatement(maxQuery);
                    ResultSet myrs = s.executeQuery();
                    List<String> rowValues = new ArrayList<String>();

                    while (myrs.next()) { // Position the cursor 3
                        rowValues.add(myrs.getString(1));
                    }
                    if (rowValues.contains(choice)) {
                        System.out.println("nice choice.");
                    } else {
                        System.out.println("try again and choose a valid apt");
                    }

                } catch (SQLException e) {
                    System.out.println("Something is wrong with connection to the NUMA database.");
                }
                break;

            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return int
     */
    public static int chooseRent(Scanner input) { // intro menu so users may select who they are based on what actions
                                                  // they wish to perform/
        int choice = 0;
        do {
            System.out.println("\nEnter the  number for the rent amount on the Lease:  ");

            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1000 && choice <= 9999)
                    break;
                else
                    System.out.println("Invalid Request. Please choose from options 1000 through 9999");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return int
     */
    public static int chooseDuration(Scanner input) { // intro menu so users may select who they are based on what
                                                      // actions
        // they wish to perform/
        int choice = 0;
        do {
            System.out.println("\nEnter either 12, 24, or 36 months for Lease Duration :  ");

            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice == 12 || choice == 24 || choice == 36)
                    break;
                else
                    System.out.println("Invalid Request. Please choose from from: options 12,24,36 ");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return String
     */
    public static String getDate(Scanner input) {
        String phoneNum;
        do {
            System.out.println("Please Enter the Date the Lease was Signed (YYYY-MM-DD):) ");
            phoneNum = input.nextLine();
            if (phoneNum.matches("\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) {
                break;
            } else {
                System.out.println(
                        "Invalid Input. Please enter a valid Date-(YYYY-MM-DD) as your input MM(01-12) DD(01-31).");
            }
        } while (true);
        return phoneNum;
    }

    /**
     * @param input
     * @return String
     */
    public static String getDatePayed(Scanner input) {
        String phoneNum;
        do {
            System.out.println("Please Enter today's date of payment (YYYY-MM-DD):) ");
            phoneNum = input.nextLine();
            if (phoneNum.matches("\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) {
                break;
            } else {
                System.out.println(
                        "Invalid Input. Please enter a valid Date-(YYYY-MM-DD) as your input MM(01-12) DD(01-31).");
            }
        } while (true);
        return phoneNum;
    }

    /**
     * @param input
     * @return String
     */
    public static String getDateMoveout(Scanner input) {
        String phoneNum;
        do {
            System.out.println("Please Enter the Date of Your planned Move out(YYYY-MM-DD):) ");
            phoneNum = input.nextLine();
            if (phoneNum.matches("\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) {
                break;
            } else {
                System.out.println(
                        "Invalid Input. Please enter a valid Date-(YYYY-MM-DD) as your input MM(01-12) DD(01-31).");
            }
        } while (true);
        return phoneNum;
    }

    /**
     * @param input
     * @return int
     */
    public static int chooseSecurity(Scanner input) { // intro menu so users may select who they are based on what
                                                      // actions
        // they wish to perform/
        int choice = 0;
        do {
            System.out.println("\nEnter the  number for security deposit the Lease:  ");

            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1000 && choice <= 9999)
                    break;
                else
                    System.out.println("Invalid Request. Please choose from options 1000 through 9999");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return int
     */
    ///////////////////////////// Business MANAGER
    ///////////////////////////// THINGS////////////////////////////
    public static int businessManagerMenu(Scanner input) { // the list of actions a property manager may choose to do.
        int choice = 0;
        do {
            System.out.println("\nHow may we help you today:  ");
            System.out.println(" 1: Display all Tenants based on Property");// update
            System.out.println(" 2: Find Taxes Incurred based on Property");// update
            System.out.println(" 3: Find overall revenue for NUMA");// update
            System.out.println(" 4: Log Out"); // exit the portal
            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1 && choice <= 4)
                    break;
                else
                    System.out.println("Invalid Input. Please choose from options 1 through 4.");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

    /**
     * @param conn
     */
    public static void allProps(Connection conn) {
        // int newid = 0;

        try {

            String maxQuery = "SELECT * FROM property ";

            PreparedStatement s = conn.prepareStatement(maxQuery);
            ResultSet myrs = s.executeQuery();
            System.out.println("p_id        street                         city      state     zipcode    num_apt");

            while (myrs.next()) { // Position the cursor 3
                String p_id = myrs.getString("p_id");
                String street = myrs.getString("street");
                String city = myrs.getString("city");
                String state = myrs.getString("state");
                String zipcode = myrs.getString("zipcode");
                String num_apts = myrs.getString("num_apts");

                System.out.println(
                        p_id + "            " + street + "              " + city + "           " + state + "        "
                                + zipcode + "        " +
                                num_apts);
                // System.out.printf("[%4s %-4d %10f %-10s %-10s 10s\n]%n", p_id, street, city,
                // state, zipcode, num_apts);

                // Print the column value
            }

        } catch (SQLException e) {
            System.out.println("Something is wrong with connection to the NUMA database getting props.");
        }
    }

    /**
     * @param prop_id
     * @param conn
     */
    public static void alltents(String prop_id, Connection conn) {
        // int newid = 0;

        try {
            String maxQuery = " ";
            if (prop_id.equals("1")) {
                maxQuery = "SELECT * FROM tenant where apt_num NOT IN (select apt_num from apartment where p_id != 1) ";
            } else if (prop_id.equals("2")) {
                maxQuery = "SELECT * FROM tenant where apt_num NOT IN (select apt_num from apartment where p_id != 2) ";
            } else if (prop_id.equals("3")) {
                maxQuery = "SELECT * FROM tenant where apt_num NOT IN (select apt_num from apartment where p_id != 3) ";
            } else if (prop_id.equals("4")) {
                maxQuery = "SELECT * FROM tenant where apt_num NOT IN (select apt_num from apartment where p_id != 4) ";
            } else if (prop_id.equals("5")) {
                maxQuery = "SELECT * FROM tenant where apt_num NOT IN (select apt_num from apartment where p_id != 5) ";
            }

            PreparedStatement s = conn.prepareStatement(maxQuery);
            ResultSet myrs = s.executeQuery();
            System.out.println("tenant_id   name                 ssn          phone       bank             apt_num");

            while (myrs.next()) { // Position the cursor 3
                // Retrieve only the first column value
                int id = myrs.getInt("tenant_id");
                String name = myrs.getString("name");
                String ssn = myrs.getString("ssn");
                String phone = myrs.getString("Phone");
                String bank = myrs.getString("bank");
                String apt_num = myrs.getString("apt_num");

                System.out.println(
                        id + "   " + name + "            " + ssn + "       " + phone + "   " + bank + "  " + apt_num);
                // System.out.printf("[%4s %-4d %10f %-10s %-10s\n]%n", id, name, ssn, bank,
                // apt_num);
                // Print the column value
            }

        } catch (SQLException e) {
            System.out.println("Something is wrong with connection to the NUMA database.");
        }
    }

    /**
     * @param prop_id
     * @param conn
     */
    public static void findTaxes(String prop_id, Connection conn) {
        // int newid = 0;

        try {
            String maxQuery = " ";
            if (prop_id.equals("1")) {
                maxQuery = "Select Rent from Lease where Apt_num NOT IN (select apt_num from apartment where p_id != 1) ";
            } else if (prop_id.equals("2")) {
                maxQuery = "Select Rent from Lease where Apt_num NOT IN (select apt_num from apartment where p_id != 2) ";
            } else if (prop_id.equals("3")) {
                maxQuery = "Select Rent from Lease where Apt_num NOT IN (select apt_num from apartment where p_id != 3) ";
            } else if (prop_id.equals("4")) {
                maxQuery = "Select Rent from Lease where Apt_num NOT IN (select apt_num from apartment where p_id != 4) ";
            } else if (prop_id.equals("5")) {
                maxQuery = "Select Rent from Lease where Apt_num NOT IN (select apt_num from apartment where p_id != 5) ";
            }

            PreparedStatement s = conn.prepareStatement(maxQuery);
            ResultSet myrs = s.executeQuery();
            System.out.println("Rent Revenue");
            List<Double> rowValues = new ArrayList<Double>();

            while (myrs.next()) { // Position the cursor 3
                // Retrieve only the first column value
                rowValues.add(myrs.getDouble(1));

                Double rent = myrs.getDouble("rent");
                System.out.println(rent);

                // Print the column value
            }
            double sum = 0;
            for (Double d : rowValues)
                sum += d;
            if (prop_id.equals("2")) {
                Double taxes = sum * .123;
                System.out.println("State Income Taxes being Incurred from this property is: " + taxes);
            }
            if (prop_id.equals("1")) {
                Double taxes = sum * 0;
                System.out.println("State Income Taxes being Incurred from this property is: " + taxes
                        + "Texas has no Income Tax");
            }
            if (prop_id.equals("3")) {
                Double taxes = sum * .0307;
                System.out.println("Income Taxes being Incurred from this property is: " + taxes);
            }
            if (prop_id.equals("4")) {
                Double taxes = sum * .045;
                System.out.println("Income Taxes being Incurred from this property is: " + taxes);
            }
            if (prop_id.equals("5")) {
                Double taxes = sum * .123;
                System.out.println("Income Taxes being Incurred from this property is: " + taxes);
            }

        } catch (SQLException e) {
            System.out.println("Something is wrong with connection to the NUMA database.");
        }
    }

    /**
     * @param prop_id
     * @param conn
     */
    public static void findRev(String prop_id, Connection conn) {
        // int newid = 0;

        try {
            String maxQuery = "Select Rent from Lease";

            PreparedStatement s = conn.prepareStatement(maxQuery);
            ResultSet myrs = s.executeQuery();
            System.out.println("Rent Revenue");
            List<Double> rowValues = new ArrayList<Double>();

            while (myrs.next()) { // Position the cursor 3
                // Retrieve only the first column value
                rowValues.add(myrs.getDouble(1));

                Double rent = myrs.getDouble("rent");
                System.out.println(rent);

                // Print the column value
            }
            double sum = 0;
            for (Double d : rowValues)
                sum += d;
            System.out.println("The revenue from NUMA is:" + sum);

        } catch (SQLException e) {
            System.out.println("Something is wrong with connection to the NUMA database.");
        }
    }

    /**
     * @param input
     * @return String
     */
    public static String chooseProp(Scanner input) { // intro menu so users may select who they are based on what
                                                     // actions
        // they wish to perform/
        String choice = " ";
        do {
            System.out.println("\nEnter p_id of the chosen Property to display Revenue  :  ");

            choice = input.nextLine();
            if (choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4")
                    || choice.equals("5")) {
                break;
            } else {
                System.out.println("Invalid Request. Please choose from from: options 1,2,3,4,5 ");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return String
     */
    public static String chooseten_ID(Scanner input) { // intro menu so users may select who they are based on what
                                                       // actions
        // they wish to perform/
        String choice = " ";
        do {
            System.out.println("\nEnter your tenant_ID :  ");

            choice = input.nextLine();
            if (choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4")
                    || choice.equals("5")) {
                break;
            } else {
                System.out.println("Invalid Request. Please choose from from: options 1,2,3,4,5 ");
            }
        } while (true);
        return choice;
    }

    /**
     * @param input
     * @return int
     */
    public static int tenantMenu(Scanner input) { // the list of actions a property manager may choose to do.
        int choice = 0;
        do {
            System.out.println("\nHow may we help you today:  ");
            System.out.println(" 1: Check payment status (amount due, if any)");// update
            System.out.println(" 2: Make rental payment");// update
            System.out.println(" 3: Add person or pet");// update
            System.out.println(" 4: Set move out date"); // exit the portal update personal data
            System.out.println(" 5: Update Contact Info"); // exit the portal update personal data
            System.out.println(" 6: Log out"); // exit the portal update personal data

            if (input.hasNextInt()) {
                choice = input.nextInt();
                if (choice >= 1 && choice <= 6)
                    break;

                else
                    System.out.println("Invalid Input. Please choose from options 1 through 5.");
            } else {
                input.nextLine();
                System.out.println("Invalid Input. Please enter an integer as your input.");
            }
        } while (true);
        return choice;
    }

}
