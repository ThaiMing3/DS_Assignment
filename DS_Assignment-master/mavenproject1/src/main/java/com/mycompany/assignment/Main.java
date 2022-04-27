package com.mycompany.assignment;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Record;
import java.nio.*;
import java.util.*;
import static org.neo4j.driver.Values.*;
import org.neo4j.driver.internal.logging.JULogging;
import java.util.logging.Level;
import org.neo4j.driver.Config;
import org.neo4j.driver.Logger;
import org.neo4j.driver.Logging;
import org.neo4j.fabric.stream.*;
import org.neo4j.graphdb.*;

public class Main implements AutoCloseable {

    private final Driver driver;

    /**
     * Main Constructor to start the program
     *
     * @param uri
     * @param user
     * @param password
     */
    public Main(String uri, String user, String password) {
        Config config = Config.builder().withLogging(new JULogging(Level.WARNING)).build();
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), config);
    }

    /**
     * To close the session after communicating with database
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        driver.close();
    }

    public static void main(String... args) throws Exception {
        // Build up the model of the graph
//        startup();

//        Event1("8", "10");
//        Event2("1", "2");
//        Event4();
//        Event3();
//        Event5("2","7");
        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
            greeter.Event3();
        } catch (Exception e) {
        }
    }

    static int count = 0;
    static String str1 = "";

    /**
     * Additional Challenge 6
     *
     * @param numvac
     * @return String that will be printed in GUI
     */
    public static String additional6(int numvac) {
        if (count == numvac) {
            count++;
            return str1;
        }
        if (count > numvac) {
            count = 0;
            str1 = "";
        }
        ArrayList<ArrayList<String>> temp = new ArrayList<>();
        ArrayList<String> temp2 = new ArrayList<>();
        ArrayList<String> nodes = new ArrayList<>();
        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
//            String a = greeter.getNodeSize();
//            int n = Integer.parseInt(a);
            nodes = greeter.getPerson();
            for (int i = 0; i < nodes.size(); i++) {
                temp2.add(nodes.get(i));
                temp2.add(greeter.getFriendNumber(nodes.get(i)));
                temp.add(temp2);
                temp2 = new ArrayList<>();
            }

            ArrayList<String> tmp = new ArrayList<>();
            for (int i = 0; i < temp.size() - 1; i++) {
                for (int j = 0; j < temp.size() - 1 - i; j++) {
                    if (temp.get(j).get(1).compareTo(temp.get(j + 1).get(1)) < 0) {
                        tmp = temp.get(j);
                        temp.remove(j);
                        temp.add(j, temp.get(j));
                        temp.remove(j + 1);
                        temp.add(j + 1, tmp);
                    }
                }
            }
            str1 += "Person \"" + temp.get(0).get(0) + "\" will get the vaccine becuase he/she has " + temp.get(count).get(1) + " friends\n";
            greeter.deleteNode(temp.get(0).get(0));
            count++;
            additional6(numvac);
        } catch (Exception e) {
        }
        return str1;
    }

    public boolean deleteNode(String person) {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (n:Person{name: $person})"
                            + "DETACH DELETE n", parameters("person", person));
            return true;
        }
    }

    /**
     * Get number of friends of a person in database
     *
     * @param person
     * @return number of friends in String
     */
    public String getFriendNumber(String person) {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (:Person)-[r:KNOWS]->(:Person{name: $person})"
                            + "RETURN toString(count(r))", parameters("person", person));
            String str = myResult.peek().get("toString(count(r))").asString();
            return str;
        }
    }

    /**
     * Get lunch starting time of person in database
     *
     * @param person1
     * @return lunch starting time in String
     */
    public String getLunchTime(String person1) {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (a:Person{name: $person1})"
                            + "RETURN a.lunch_time", parameters("person1", person1));
            String str = myResult.peek().get("a.lunch_time").asString();
            return str;
        }
    }

    /**
     * Get lunch duration of person in database
     *
     * @param person1
     * @return lunch duration in String
     */
    public String getLunchPeriod(String person1) {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (a:Person{name: $person1})"
                            + "RETURN a.lunch_period", parameters("person1", person1));
            String str = myResult.peek().get("a.lunch_period").asString();
            return str;
        }
    }

    /**
     * Get number of persons in database
     *
     * @return number of persons in String
     */
    public String getNodeSize() {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (n:Person)"
                            + "RETURN toString(count(n))");
            String str = myResult.peek().get("toString(count(n))").asString();
            return str;
        }
    }

    /**
     * Calculate lunch end time based on start and duration given
     *
     * @param start
     * @param duration
     * @return String of the lunch end time
     */
    public String calculateLunchEnd(String start, String duration) {
        int x1 = Integer.parseInt(start);
        int y1 = Integer.parseInt(duration);
        x1 = x1 + y1; //1190
        int hour = 0;
        int min = 0;
        if (x1 % 100 >= 60) {
            hour = (x1 % 100) / 60;
            min = (x1 % 100) - 60;
            x1 = x1 - (x1 % 100);
        }
        x1 = x1 + hour * 100 + min;
        return String.valueOf(x1);
    }

    /**
     * Event 3
     *
     * @return String which will be printed in GUI
     */
    public String Event3() {
        String str = "";
        System.out.println("Running Event 3...\n\n");
        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
            ArrayList<String> temp;
            ArrayList<ArrayList<String>> detail = new ArrayList<>();
            int x = Integer.parseInt(greeter.getNodeSize());
            // Get all persons' lunch time and lunch period from database and put in an arraylist
            for (int i = 1; i <= x; i++) {
                temp = new ArrayList<>();
                String str1 = String.valueOf(i);
                temp.add(str1);
                String lunchtime = greeter.getLunchTime(str1);
                String lunchperiod = greeter.getLunchPeriod(str1);
                String lunchend = greeter.calculateLunchEnd(lunchtime, lunchperiod);
                temp.add(lunchtime);
                temp.add(lunchend);
                detail.add(temp);
            }

            //Sort the person based on the lunch start time
            ArrayList<String> temp2 = new ArrayList<>();
            ArrayList<String> temp3 = new ArrayList<>();
            for (int i = 0; i < detail.size(); i++) {
                for (int j = 0; j < detail.size() - 1 - i; j++) {
                    if (detail.get(j).get(1).compareTo(detail.get(j + 1).get(1)) > 0) {
                        temp2 = detail.get(j);
                        detail.remove(j);
                        detail.add(j, detail.get(j));
                        detail.remove(j + 1);
                        detail.add(j + 1, temp2);
                    }
                }
            }

            // Compare person with person their lunch end time starting from first person in sorted list
            ArrayList<String> temp4 = new ArrayList<>();
            int count = 1;
            temp3.add(detail.get(0).get(0));
            temp4.add(detail.get(0).get(1));
            String end = detail.get(0).get(2);
            for (int i = 1; i < detail.size(); i++) {
                if (end.compareTo(detail.get(i).get(1)) <= 0) {
                    temp3.add(detail.get(i).get(0));
                    end = detail.get(i).get(2);
                    temp4.add(detail.get(i).get(1));
                    count++;
                }
            }

            // Compare again with second ways
            temp3.add(detail.get(1).get(0));
            temp4.add(detail.get(1).get(1));
            int count1 = 1;
            end = detail.get(1).get(2);
            for (int i = 2; i < detail.size(); i++) {
                if (end.compareTo(detail.get(i).get(1)) <= 0) {
                    temp3.add(detail.get(i).get(0));
                    end = detail.get(i).get(2);
                    temp4.add(detail.get(i).get(1));
                    count1++;
                }
            }

            // Compare between two ways see to check which has the most reputations
            if (count1 > count) {
                for (int i = 0; i < count; i++) {
                    temp3.remove(0);
                    temp4.remove(0);
                }
                str += "The max reputation I can get are : " + count1 + "\n";
                for (int i = 1; i <= temp3.size(); i++) {
                    str += i + ". Person : \"" + temp3.get(i - 1) + "\" __>" + temp4.get(i - 1) + "\n";
                }
            } else {
                for (int i = 0; i < count1; i++) {
                    temp3.remove(count);
                    temp4.remove(count);
                }
                str += "The max reputation I can get are : " + count + "\n";
                for (int i = 1; i <= temp3.size(); i++) {
                    str += i + ". Person : \"" + temp3.get(i - 1) + "\" __>" + temp4.get(i - 1) + "\n";
                }
            }

        } catch (Exception e) {
        }
        return str;
    }

    /**
     * Event 4
     *
     * @param num
     * @param bookinline
     * @return String which will be printed in GUI
     */
    public String Event4(String num, String bookinline) {
        int n = Integer.parseInt(num);
        String[] bookarr = bookinline.split(",");
        Scanner sc = new Scanner(System.in);
        Stack<Integer> books = new Stack<>();
        Stack<Integer> temp = new Stack<>();

        int count = 0;
        for (int i = 0; i < n; i++) {
            books.push(Integer.parseInt(bookarr[i]));
        }
        System.out.println(books.toString());
//        System.out.println(books.toString());

        boolean remove = false;
        while (!books.isEmpty() || !remove) {
            temp.push(books.pop());
            if (!books.isEmpty() && temp.peek() > books.peek()) {
                temp.remove(temp.size() - 1);
                remove = true;
            }
            if (books.isEmpty() && remove) {
                count++;
                while (!temp.isEmpty()) {
                    books.push(temp.pop());
                }
                remove = false;
            } else if (books.isEmpty() && !remove) {
                break;
            }
        }

        while (!temp.isEmpty()) {
            books.push(temp.pop());
        }
        String str = "";
        str += count + "\n\n" + books.toString();
//        System.out.println(books.toString());
//        System.out.println("");
//        System.out.println("_________________");
        return str;
    }

    /**
     * Event 5
     *
     * @param personstart
     * @param crush
     * @return String which will be printed in GUI
     */
    public String Event5(String personstart, String crush) {
        ArrayList<String> subgraph2 = new ArrayList<>();
        subgraph2.add("4");
        subgraph2.add("8");
        subgraph2.add("9");
        subgraph2.add("10");
        String str = "";

        if (personstart.equals(crush)) {
            str += "Please enter different person!";
            return str;
        }

        if ((subgraph2.contains(personstart) && !subgraph2.contains(crush))
                || (subgraph2.contains(crush) && !subgraph2.contains(personstart))) {
            str += "The crush (" + crush + ") will not possible to know the truth from stranger (" + personstart + ")";
            return str;
        }

        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {

            if (greeter.checkRelation(personstart, crush)) {
                str += "Person \"" + personstart + "\" and person \"" + crush + "\" are from same cluster. Event does not work for them\n";
                return str;
            }

            ArrayList<String> friends = new ArrayList<>();
            friends = greeter.checkFriendNodeAll(personstart);
            str += "Rumuors will spread to " + friends.size() + " person(s) on the first day\n";

            if (friends.size() == 1) {
                str += "Convince Person \"" + friends.get(0) + "\" will be able to stop spreading the rumour to my crush(" + crush + ")\n";
            } else {
                boolean testRelation;
                for (int i = 0; i < friends.size(); i++) {
                    if (greeter.checkRelation(friends.get(i), crush)) {
                        testRelation = true;
                    } else {
                        testRelation = false;
                        for (int j = 0; j < friends.size(); j++) {
                            if (greeter.checkRelation(friends.get(j), crush)) {
                                str += "Convice Person \"" + friends.get(j) + "\" will be able to stop spreading the rumuor to my crush(" + crush + ")\n";
                            }
                        }
                        return str;
                    }
                }
                if (testRelation = true) {
                    str += "Unable to stop spreading the rumor";
                    return str;
                }

            }
        } catch (Exception e) {
        }
        return str;
    }

    /**
     * Event 2
     *
     * @param person1
     * @param person2
     * @return String which will be printed in GUI
     */
    public String Event2(String person1, String person2, String msg) {
        if (person1.equals(person2)) {
            return "Please enter different person!";
        }
        String str = "";
        String num = "";
        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
            if (greeter.checkRelation(person2, person1)) {
                ArrayList<String> friends = greeter.checkFriendNode(person1, person2);
                double x;
                if (msg.equals("Good")) {
                    x = 0.6;
                } else if (msg.equals("Bad")) {
                    x = 0.1;
                } else {
                    x = Math.random();
                }
                if (x > 0.5) {
                    for (int i = 0; i < friends.size(); i++) {
                        if (greeter.checkRelation(friends.get(i), person1)) {
                            if (i == 0) {
                                str += "Person \"" + person2 + "\" share good message about Person \"" + person1 + "\"\n";
                            }
                            String rep = greeter.getRep(person1, person2);
                            double repDou = Double.parseDouble(rep);
                            String repfriend = greeter.getRep(person1, friends.get(i));
                            double repfriendDou = Double.parseDouble(repfriend);
                            repDou = repfriendDou + (repDou * 0.5);
                            String tmp = String.valueOf(repDou);
                            num = greeter.updateRep(person1, friends.get(i), tmp);
                            str += "Person \"" + friends.get(i) + "\" knows person \"" + person1 + "\" with reputations of " + num + "\n";
                        } else {
                            if (i == 0) {
                                str += "Person \"" + person2 + "\" share good message about Person \"" + person1 + "\"\n";
                            }
                            String rep = greeter.getRep(person1, person2);
                            double repDou = Double.parseDouble(rep);
                            repDou = repDou * 0.5;
                            String tmp = String.valueOf(repDou);
                            greeter.makeFriends(friends.get(i), person1, tmp);
                            str += "Person \"" + friends.get(i) + "\" knows person \"" + person1 + "\" with reputations of " + tmp + "\n";
                        }
                    }
                } else {
                    for (int i = 0; i < friends.size(); i++) {
                        if (greeter.checkRelation(friends.get(i), person1)) {
                            if (i == 0) {
                                str += "Person \"" + person2 + "\" share bad message about Person \"" + person1 + "\"\n";
                            }
                            String rep = greeter.getRep(person1, person2);
                            double repDou = Double.parseDouble(rep);
                            String repfriend = greeter.getRep(person1, friends.get(i));
                            double repfriendDou = Double.parseDouble(repfriend);
                            repDou = repfriendDou - repDou;
                            String tmp = String.valueOf(repDou);
                            num = greeter.updateRep(person1, friends.get(i), tmp);
                            str += "Person \"" + friends.get(i) + "\" knows person \"" + person1 + "\" with reputations of " + num + "\n";
                        } else {
                            if (i == 0) {
                                str += "Person \"" + person2 + "\" share bad message about Person \"" + person1 + "\"\n";
                            }
                            String rep = greeter.getRep(person1, person2);
                            double repDou = Double.parseDouble(rep);
                            repDou = -repDou;
                            String tmp = String.valueOf(repDou);
                            greeter.makeFriends(friends.get(i), person1, tmp);
                            str += "Person \"" + friends.get(i) + "\" knows person \"" + person1 + "\" with reputations of " + tmp + "\n";
                        }
                    }
                }
            } else {
                return "Please enter two people that are already friends!";
            }
        } catch (Exception e) {
        }
        return str;
    }

    /**
     * Event 1
     *
     * @param person1
     * @param person2
     * @return String which will be printed in GUI
     */
    public String Event1(String person1, String person2) {
        String str = "";
        if (person1.equals(person2)) {
            str += "Please enter different person!";
            return str;
        }

        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
            if (greeter.checkRelation(person1, person2)) {
                str += "\" " + person1 + "\" and \"" + person2 + "\" knows each other\n"
                        + "Event 1 does not suit for them";
                return str;
            } else {
                double x = Math.random();
                if (x > 0.5) {
                    System.out.println("test3");
                    str += "\"" + person1 + "\" knows how to solve the lab question";
                    str += greeter.makeFriends(person2, person1, "10");
                    str += greeter.makeFriends(person1, person2, "2");
                } else {
                    System.out.println("test4");
                    str += "\"" + person1 + "\" does not knows how to solve the lab question";
                    str += greeter.makeFriends(person2, person1, "2");
                    str += greeter.makeFriends(person1, person2, "2");
                }
            }
        } catch (Exception e) {
        }
        return str;
    }

    /**
     * Check all person2's friends except person1
     *
     * @param person1
     * @param person2
     * @return Lists of person2's friends except person1
     */
    public ArrayList<String> checkFriendNode(String person1, String person2) {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (a:Person{name:$person2})-[r:KNOWS]->(b:Person)"
                            + "WHERE NOT (a:Person{name:$person2})-[r:KNOWS]->(b:Person{name:$person1})"
                            + "RETURN b.name",
                            parameters("person2", person2, "person1", person1));
            List<Record> myRecords = myResult.list();
            ArrayList<String> test = new ArrayList<>();
            for (Record records : myRecords) {
                test.add(records.get("b.name").asString());
            }
            return test;
        }
    }

    /**
     * Check all person2's friends
     *
     * @param person2
     * @return List of person2's friends
     */
    public ArrayList<String> checkFriendNodeAll(String person2) {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (a:Person{name:$person2})-[r:KNOWS]->(b:Person)"
                            + "RETURN b.name",
                            parameters("person2", person2));
            List<Record> myRecords = myResult.list();
            ArrayList<String> test = new ArrayList<>();
            for (Record records : myRecords) {
                test.add(records.get("b.name").asString());
            }
            return test;
        }
    }

    /**
     * Check reputation of person2 to person1
     *
     * @param person1
     * @param person2
     * @return value of reputation in String
     */
    public String getRep(String person1, String person2) {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (:Person{name: $person2})-[r:KNOWS]->(:Person{name: $person1})"
                            + "RETURN r.rep", parameters("person1", person1, "person2", person2));
            String str = myResult.peek().get("r.rep").asString();
            return str;
        }
    }

    /**
     * Update the reputation of person2 to person1 with specific value in
     * database
     *
     * @param person1
     * @param person2
     * @param num
     * @return updated reputation
     */
    public String updateRep(String person1, String person2, String num) {
        try ( Session session = driver.session()) {
            session.run("MATCH (a:Person{name: $person2})-[r:KNOWS]->(b:Person{name: $person1})"
                    + "SET r.rep = $updt", parameters("person2", person2, "person1", person1, "updt", num));
        } catch (Exception e) {
        }
        return num;
    }

    /**
     * Check if person1 knows person2
     *
     * @param person1
     * @param person2
     * @return if yes then return true else return false
     */
    public boolean checkRelation(final String person1, final String person2) {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (a:Person{name: $person1}),(b:Person{name: $person2})"
                            + "RETURN EXISTS((a)-[:KNOWS]->(b))",
                            parameters("person1", person1, "person2", person2));
            String str = myResult.peek().toString();
            if (str.contains("TRUE")) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Add new person to the database
     *
     * @param name
     */
    public void addPerson(String name) {
        try ( Session session = driver.session()) {
            session.run("CREATE (a:Person {name: $name})", parameters("name", name));
        }
    }

    // Test
    public String test() {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (b:Person) RETURN b.name");
            List<Record> myRecords = myResult.list();
            ArrayList<String> test = new ArrayList<>();
            for (Record records : myRecords) {
                test.add(records.get("b.name").asString());
            }
            return test.toString();
        }
    }

    /**
     * Create relation between person1 and person2 with specific reputation
     *
     * @param person1
     * @param person2
     * @param rep
     * @return String that will be printed in GUI
     */
    public String makeFriends(final String person1, final String person2, final String rep) {
        String str = "";
        try ( Session session = driver.session()) {

            session.run("MATCH (a:Person {name: $person_1}), (b:Person{name: $person_2})"
                    + "MERGE (a)-[:KNOWS{rep: $rep}]->(b)",
                    parameters("person_1", person1, "person_2", person2, "rep", rep));
            str += "\nPerson \"" + person1 + "\" knows person \"" + person2 + "\" with reputations of " + rep;
        }
        return str;
    }

    /**
     * Set diving rate
     *
     * @param person1
     * @param diving_rate
     * @return String that will be printed in GUI
     */
    public String setDivingRate(final String person1, final String diving_rate) {
        String str = "";
        try ( Session session = driver.session()) {
            session.run("MATCH (a:Person {name: $person_1})"
                    + "SET a.diving_rate = $diving_rate",
                    parameters("person_1", person1, "diving_rate", diving_rate));
            Result myResult
                    = session.run("MATCH (a:Person {name: $person_1})" + "RETURN a.diving_rate",
                            parameters("person_1", person1));
            List<Record> myRecords = myResult.list();
            ArrayList<String> test = new ArrayList<>();
            for (Record records : myRecords) {
                test.add(records.get("a.diving_rate").asString());
            }
            str += "Set \"" + person1 + "\" diving_rate to " + diving_rate + "%\n";
        }
        return str;
    }

    /**
     * Set lunch time
     *
     * @param person1
     * @param lunch_time
     * @return String that will be printed in GUI
     */
    public String setLunchTime(final String person1, final String lunch_time) {
        String str = "";
        try ( Session session = driver.session()) {
            session.run("MATCH (a:Person {name: $person_1})"
                    + "SET a.lunch_time = $lunch_time",
                    parameters("person_1", person1, "lunch_time", lunch_time));
            Result myResult
                    = session.run("MATCH (a:Person {name: $person_1})" + "RETURN a.lunch_time",
                            parameters("person_1", person1));
            List<Record> myRecords = myResult.list();
            ArrayList<String> test = new ArrayList<>();
            for (Record records : myRecords) {
                test.add(records.get("a.lunch_time").asString());
            }
            str += "Set \"" + person1 + "\" lunch_time to " + lunch_time + "\n";
        }
        return str;
    }

    /**
     * Set lunch period
     *
     * @param person1
     * @param lunch_period
     * @return String that will be printed in GUI
     */
    public String setLunchPeriod(final String person1, final String lunch_period) {
        String str = "";
        try ( Session session = driver.session()) {
            session.run("MATCH (a:Person {name: $person_1})"
                    + "SET a.lunch_period = $lunch_period",
                    parameters("person_1", person1, "lunch_period", lunch_period));
            str += "Set \"" + person1 + "\" lunch_period to " + lunch_period + "minute(s)\n";
        }
        return str;
    }

    public ArrayList<String> getPerson() {
        try ( Session session = driver.session()) {
            Result myResult
                    = session.run("MATCH (n:Person)\n"
                            + "RETURN n.name");
            List<Record> myRecords = myResult.list();
            ArrayList<String> test = new ArrayList<>();
            for (Record records : myRecords) {
                test.add(records.get("n.name").asString());
            }
            return test;
        }
    }

    /**
     * Set friend lists
     *
     * @param person1
     * @param friend_lists
     */
    public String setFriendLists() {
        String str = "";
        ArrayList<ArrayList<String>> temp = new ArrayList<>();
        ArrayList<String> temp2 = new ArrayList<>();
        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
            int n = Integer.parseInt(greeter.getNodeSize());
            ArrayList<String> temp3 = greeter.getPerson();
            for (int i = 0; i < n; i++) {
                temp2.add(temp3.get(i));
                temp.add(temp2);
                temp.add(greeter.checkFriendNodeAll(temp3.get(i)));
                temp2 = new ArrayList<>();
            }

            for (int i = 0; i < n * 2; i += 2) {
                str += temp.get(i) + ": ";
                str += temp.get(i + 1).toString() + "\n";
            }
            return str;
        } catch (Exception e) {
        }
        return str;
    }

    /**
     * Set all property of a person (diving rate, lunch time and lunch period)
     *
     * @return String that will be printed in GUI
     */
    public String setProperty() {
        String j = "";
        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
            j += greeter.setDivingRate("1", "85");
            j += greeter.setDivingRate("2", "30");
            j += greeter.setDivingRate("3", "45");
            j += greeter.setDivingRate("4", "50");
            j += greeter.setDivingRate("5", "80");
            j += greeter.setDivingRate("6", "70");
            j += greeter.setDivingRate("7", "90");
            j += greeter.setDivingRate("8", "45");
            j += greeter.setDivingRate("9", "75");
            j += greeter.setDivingRate("10", "55");

            j += greeter.setLunchTime("1", "1100");
            j += greeter.setLunchTime("2", "1115");
            j += greeter.setLunchTime("3", "1110");
            j += greeter.setLunchTime("4", "1200");
            j += greeter.setLunchTime("5", "1130");
            j += greeter.setLunchTime("6", "1230");
            j += greeter.setLunchTime("7", "1300");
            j += greeter.setLunchTime("8", "1310");
            j += greeter.setLunchTime("9", "1340");
            j += greeter.setLunchTime("10", "1200");

            j += greeter.setLunchPeriod("1", "45");
            j += greeter.setLunchPeriod("2", "15");
            j += greeter.setLunchPeriod("3", "40");
            j += greeter.setLunchPeriod("4", "20");
            j += greeter.setLunchPeriod("5", "30");
            j += greeter.setLunchPeriod("6", "30");
            j += greeter.setLunchPeriod("7", "30");
            j += greeter.setLunchPeriod("8", "30");
            j += greeter.setLunchPeriod("9", "20");
            j += greeter.setLunchPeriod("10", "25");

        } catch (Exception e) {
        }
        return j;
    }

    /**
     * Check if the database is Empty
     *
     * @return
     */
    public static boolean isEmpty() {
        try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
//             System.out.println(greeter.test());
            int x = Integer.parseInt(greeter.getNodeSize());
            if (x != 0) {
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * Build graph model with node and relationship
     */
    public void startup() {
        if (isEmpty()) {
            try ( Main greeter = new Main("bolt://localhost:7687", "neo4j", "wia1002")) {
//             System.out.println(greeter.test());
                greeter.addPerson("1");
                greeter.addPerson("2");
                greeter.addPerson("3");
                greeter.addPerson("4");
                greeter.addPerson("5");
                greeter.addPerson("6");
                greeter.addPerson("7");
                greeter.addPerson("8");
                greeter.addPerson("9");
                greeter.addPerson("10");

                greeter.makeFriends("1", "7", "4");
                greeter.makeFriends("7", "1", "3");
                greeter.makeFriends("1", "2", "5");
                greeter.makeFriends("2", "1", "8");
                greeter.makeFriends("6", "2", "7");
                greeter.makeFriends("2", "6", "9");
                greeter.makeFriends("2", "5", "6");
                greeter.makeFriends("5", "2", "2");
                greeter.makeFriends("2", "3", "5");
                greeter.makeFriends("3", "2", "4");

                greeter.makeFriends("8", "4", "10");
                greeter.makeFriends("4", "8", "7");
                greeter.makeFriends("4", "10", "7");
                greeter.makeFriends("10", "4", "7");
                greeter.makeFriends("10", "9", "6");
                greeter.makeFriends("9", "10", "5");

                // Set property for each node
                setProperty();

            } catch (Exception e) {
            }
        }
    }

    /**
     * Reset graph to base model
     */
    public void reset() {
        try ( Session session = driver.session()) {
            session.run("MATCH (n)"
                    + "DETACH DELETE (n)");
        }
    }
}
