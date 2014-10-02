package aprioriTest;

import apriori.FrequentItems;

import java.io.IOException;
import java.util.List;

/**
 * Created by szeyiu on 10/1/14.
 * This class contains test cases for FrequentItems.class
 */
public class FrequentItemsTest {
    public static void main(String[] args) throws IOException{
        kCombinationTest();
        findFrequentPairsTest();
        findFrequentAllTest();
    }

    public static void kCombinationTest(){
        FrequentItems fi = new FrequentItems("in.csv");
        String[] s = new String[10];
        for(int i=0;i<s.length;++i){
            s[i]=Integer.toString(i);
        }
        List<List<Integer>> comb = fi.kCombination(s,0,s.length-1,4);
        System.out.println(comb.size());
        for(List<Integer> c:comb){
            System.out.print(c);
            System.out.println();
        }
    }

    public static void findFrequentPairsTest() throws IOException{
        FrequentItems fi = new FrequentItems("in.csv");
        fi.findFrequentPairs();
        fi.print();
    }

    public static void findFrequentAllTest() throws IOException {
        FrequentItems fi = new FrequentItems("in.csv");
        fi.setThreshold(0.2);
        fi.findFrequentPairs().findFrequentAll();
        fi.print();
    }
}
