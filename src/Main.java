import apriori.FrequentItems;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.print("Find Frequent Item Sets. Author: Siyao Li\n\n\nInstruction:\n\n" +
                "Arguments: inputfile, outputfile, [delimiter=','], [threshold=0.7]\n" +
                "You are required to at least provide the first two arguments, " +
                "as delimiter is set ',' and threshold is set 0.7 by default.\n\n\n\n\n");

        if(args.length<2){
            throw new Error("Error: need at least two args: inputfile, outputfile!");
        }
        String inputfile = args[0];
        String outputfile = args[1];
        String delimeter = null;
        double threshold = -1;
        if(args.length>2) {
            delimeter = args[2];
            threshold = Double.valueOf(args[3]);
            if(threshold<=0||threshold>=1)
                throw new Error("Error: threshold should be a value between 0 and 1!");
        }
        FrequentItems fi = new FrequentItems(inputfile);
        fi.setOutputPath(outputfile);
        if(delimeter!=null) fi.setDelimeter(delimeter);
        if(threshold>0) fi.setThreshold(threshold);
        fi.findFrequentPairs().findFrequentAll();
        fi.printfile();
        System.out.print("Result has outputted to file. Do you want to display results on screen?\n" +
                "Y: Display\nN: No and quit for safe.\n");
        Scanner console = new Scanner(System.in);
        String in = console.nextLine();
        if(in.equals("Y")||in.equals("y"))  fi.print();
        System.out.println("Program safely terminated! Thanks!");
    }
}
