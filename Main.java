import syntaxtree.*;
import visitor.*;
import java.io.*;

public class Main {
    public static void main (String [] args){
    if(args.length == 0){
        System.err.println("Usage: java Main <inputFile>");
        System.exit(1);
    }
    FileInputStream fis = null;
    try{
        int counter=1;
        for (String arg: args) {
            System.out.println("\nInput file" + counter + ": "  + arg + "\n");
            counter ++;
            
            fis = new FileInputStream(arg);
            MiniJavaParser parser = new MiniJavaParser(fis);
            Goal root = parser.Goal();
     //       System.err.println("Program parsed successfully.");
            DefCollectVisitor visitor1 = new DefCollectVisitor();

            root.accept(visitor1, null);
            TypeCheckVisitor visitor2 = new TypeCheckVisitor();
            fis = new FileInputStream(arg);

            parser = new MiniJavaParser(fis);
            root = parser.Goal();
            root.accept(visitor2, visitor1);
        }
       
    }
    catch(ParseException ex){
        System.out.println(ex.getMessage());
    }
    catch(FileNotFoundException ex){
        System.err.println(ex.getMessage());
    }
    finally{
        try{
        if(fis != null) fis.close();
        }
        catch(IOException ex){
        System.err.println(ex.getMessage());
        }
    }
    }
}
