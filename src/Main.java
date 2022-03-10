import jdk.swing.interop.SwingInterOpUtils;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.print.DocFlavor;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;
import java.sql.Array;
import java.text.DecimalFormat;
import java.util.*;


public class Main {

    //https://jsonplaceholder.typicode.com/albums
    //private static HttpURLConnection connection;
    public static void main(String[] args){

        //addDict();
        String[] words;
        words=hasMoreThanSix(noDuppl(createCharArrFromFile()));

        //PLAY THE GAME AND SHOW RESULT (WIN/LOSS,POINTS)
        System.out.println("the result was:"+game(words).toString());

    }


    public static ArrayList<Integer> game(String[] words ){
        //CHECK IF DICT IS GOOD AND GET KEY WORD
        String key_word = getKeyWord(words);
        //create string list of words of same size with key word
        LinkedList<String> listWord = new LinkedList<String>();

        for(int i =0;i<words.length;i++) {
            if (words[i].length() == key_word.length()) {
                listWord.add(words[i]);
            }
        }
        String [] keyWord = createKeyWord(key_word);
        String [] missWord = createMissWord(key_word);



        int mistakes=0;
        ArrayList<Integer> result = new ArrayList<Integer>(2);
        int win=0;
        int points=0;
        while (mistakes<=5||!Arrays.equals(keyWord,missWord)){
            for(int i=0;i<listWord.size();i++){
                System.out.println(listWord.get(i));
            }
            System.out.println(Arrays.toString(keyWord));
            System.out.println(Arrays.toString(missWord));
            Scanner scanner1 = new Scanner(System.in);
            System.out.println("select position");
            String res = scanner1.nextLine();
            if (!isInt(res,10)){
                continue;
            }
            int pos = Integer.parseInt(res);
            if(pos>=keyWord.length||pos<0){
                System.out.println("position not valid");
                continue;
            }
            if(missWord[pos]!="_"){
                System.out.println("already found the letter in this position");
                continue;
            }

            //showProb(listWord,pos);
            LinkedList<Character> charsWord = new LinkedList<Character>();

            for(int i=0;i<listWord.size();i++){

                charsWord.add(listWord.get(i).charAt(pos));
            }

            LinkedList <String> charStrings = new LinkedList<String>();

            LinkedList<Double> probabilities = new LinkedList<Double>();

            for (int i= 0;i<charsWord.size();i++){

                charStrings.add(Character.toString(charsWord.get(i)));

                //String probString = String.valueOf(Collections.frequency(charsWord,charsWord.get(i))*1.0/charsWord.size()*1.0);
                /*if(probString.length()>5){
                    probabilities.add(probString.substring(0,5));
                }else{
                    probabilities.add(probString);
                }*/
                probabilities.add(Collections.frequency(charsWord,charsWord.get(i))*1.0/charsWord.size()*1.0);


            }

            for(int i =0;i<charStrings.size();i++) {
                if (probabilities.get(i).toString().length()<= 5) {
                    System.out.println(charStrings.get(i) + " " + (probabilities.get(i).toString()));
                }else {
                    System.out.println(charStrings.get(i) + " " + (probabilities.get(i).toString()).substring(0, 5));
                }
            }
            // FUNCTION ABOVE

            System.out.println("enter letter");
            String char_try = scanner1.nextLine();
            if(!isLetter(char_try)){
                System.out.println("that is not a letter");
                continue;
            }
            if(char_try.toUpperCase().equals(keyWord[pos])){
                missWord[pos]=char_try.toUpperCase();

                int index = charStrings.indexOf(char_try.toUpperCase());
                double probi = probabilities.get(index);
                if(probi<0.25){
                    points+=30;
                }else if(probi>=0.25&&probi<0.4){
                    points+=15;
                }else if(probi>=0.4&&probi<0.6){
                    points+=10;
                }else {
                    points+=5;
                }

                //update the listWord
                for(int i =0;i<listWord.size();i++){
                    if(listWord.get(i).charAt(pos)!=char_try.charAt(0)){
                        listWord.remove(i);
                        i--;
                    }
                }
            }else {
                mistakes++;
                if(points>0)
                    points-=15;
            }
            System.out.println("Points gathered: "+points);
            System.out.println("Mistakes: "+mistakes);
            if(Arrays.equals(keyWord,missWord)){
                System.out.println("You won!");
                win=1;
                break;
            }
            if(mistakes==6) {
                System.out.println("You lost! The word was: "+Arrays.toString(keyWord));
                win=0;
                break;
            }
        }

        result.add(win);
        result.add(points);
        return result;
    }


    //check if string is a letter
    public static boolean isLetter(String s){
        if (s == null) // checks if the String is null {
            return false;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if ((!Character.isLetter(s.charAt(i)))) {
                return false;
            }
        }
        return len <= 1;

    }

    //check if string is int
    public static boolean isInt(String s,int radix){
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;

    }
    public static String[] createMissWord(String key_word){
        String[] missWord = new String[key_word.length()];
        for(int i=0;i<key_word.length();i++){
            missWord[i]="_";
        }
        return missWord;
    }
    public static String[] createKeyWord(String key_word){
        String[] res = new String[key_word.length()];
        for (int i = 0; i < key_word.length(); i++) {
            res[i] = Character.toString(key_word.charAt(i));
        }
        return res;
    }

    public static String getKeyWord(String[] word_input){
        if(isDictGood(word_input)){
            return getRandWord(word_input);
        }else {
            System.out.println("something went wrong");
            return null;
        }
    }

    public static boolean isDictGood(String[] word_input){
        return hasGoodPercentage(word_input) && word_input.length >= 20;
    }
    public static String[] hasMoreThanSix(String[] word_input){
        LinkedHashSet<String> lhSetwords = new LinkedHashSet<>();

        for (String s : word_input) {
            if (s.length() >= 6) {
                lhSetwords.add(s);
            }
        }
        return lhSetwords.toArray(new String[ lhSetwords.size() ]);
    }

    public static boolean hasGoodPercentage(String[] word_input){
        int counter=0;
        for (String s : word_input) {
            if (s.length() >= 9) {
                counter++;
            }
        }
        //System.out.println(counter);
        //System.out.println(word_input.length);
        double perc = counter*1.0/word_input.length;
        System.out.println("the percentage is : "+perc);
        return perc > 0.2;
    }

    public static String getRandWord(String[] word_input){
        Random r=new Random();
        int randomNumber=r.nextInt(word_input.length);
        return word_input[randomNumber];
    }

    public static String[] noDuppl (String[] words_input){
        LinkedHashSet<String> lhSetwords = new LinkedHashSet<>(Arrays.asList(words_input));
        return lhSetwords.toArray(new String[ lhSetwords.size() ]);

    }


    public static String[] createCharArrFromFile(){
        Scanner myScanner = new Scanner(System.in);
        String book_ID = myScanner.nextLine();


        //read string from file
        File f = new File("hangman_"+book_ID+".txt");
        try {
            Scanner myReader = new Scanner(f);
            String s = "";
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                s+=data;
            }
            myReader.close();
            //System.out.println("The string is: "+s);

            return s.split("[ .()1234567890@?'-:/!,\t\n\"]+");

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }
        return null;
    }


    public static void addDict(){
        Scanner myObj = new Scanner(System.in);
        String book_ID = myObj.nextLine();
        book_ID=book_ID.trim();

        String s = getData(book_ID);
        createFile(book_ID,s);
    }
    //it creates a file with the bookId given and writes the description of the book to it
    public static void createFile(String book_id,String data){
        try {
            File myObj = new File("hangman_"+book_id+".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                try {
                    FileWriter myWriter = new FileWriter("hangman_"+book_id+".txt");
                    myWriter.write(data.toUpperCase());
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    //it gets the text of the description of the book as a string using the openlibrary API
    public static String getData (String bookID){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://openlibrary.org/works/"+bookID+".json")).build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Main::parse)
                .join();
    }

    //helper function to getData to get the correct text from the JSON
    public static String parse(String responseBody){
        JSONObject data = new JSONObject(responseBody);
        String descr = data.getString("description");
        //String descr=data.getJSONObject("description").getString("value");
        return descr;
    }
}
