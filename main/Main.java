
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter the .mini file name: ");
        String fileName = keyboard.nextLine();

        // Read the file line by line and combine it into one String.
        String sourceCode = "";
        try{
            Scanner fileReader = new Scanner(new File(fileName));
            while (fileReader.hasNextLine()) {
                sourceCode = sourceCode + fileReader.nextLine() + "\n";
            }
            fileReader.close();
        } 
        catch (FileNotFoundException e){
            System.out.println("Error: could not find file '" + fileName + "'");
            return;
        }
        try{
            Tokenizer tokenizer = new Tokenizer(sourceCode);
            ArrayList<Token> tokens = tokenizer.tokenize();

            System.out.println("TOKENS");
            System.out.println("------");
            for(int i = 0; i < tokens.size(); i++){
                System.out.println(tokens.get(i));
            }

            // Add your sections right here: Parser, SemanticAnalyzer, Interpreter


        } 
        catch(MiniLangError e){
            System.out.println(e.getErrorReport());
        }
    }
}