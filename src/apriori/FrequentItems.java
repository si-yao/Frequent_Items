package apriori;

import java.io.*;
import java.util.*;

/**
 * Created by szeyiu on 10/1/14.
 */
public class FrequentItems {
    //Assumption: Frequent item groups can be stored in memory.
    private String inputPath;
    private String outputPath="out.txt";
    private String delimeter=",";
    private int bucketSize=0;
    private int itemSize=0;
    private double threshold=0.7;//threshold to be frequent items.
    private List<ItemGroup> frequentList=new ArrayList<ItemGroup>();

    public FrequentItems(String inputPath, String delimeter, int threshold){
        this.inputPath=inputPath;
        this.delimeter=delimeter;
        this.threshold=threshold;
    }
    public FrequentItems(String inputPath){
        this.inputPath=inputPath;
    }
    public void setOutputPath(String path){
        outputPath=path;
    }
    public String getOutputPath() {
        return outputPath;
    }
    public void setDelimeter(String delimeter){
        this.delimeter=delimeter;
    }
    public String getDelimeter(){
        return delimeter;
    }
    public int bucketSize(){
        return bucketSize;
    }
    public int itemSize(){
        return itemSize;
    }
    public void setThreshold(double threshold){
        this.threshold=threshold;
    }
    public double getThreshold(){
        return threshold;
    }

    /**
     * main functino to find frequent items
     * using A-priori algorithm.
     * @return this in order to cascade the operations.
     * @throws IOException
     */
    public FrequentItems findFrequentPairs() throws IOException {
        File infile = new File(inputPath);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(infile)));

        String header = reader.readLine();
        String[] headersplit = header.split(delimeter);
        for(String h:headersplit){
            if(h.length()>0)
                itemSize++;
        }
        //C1 contains all items and its frequency in the input file.
        int[] C1 = new int[itemSize];
        for(int i=0;i<C1.length;++i)    C1[i]=0;
        String bucket;
        String[] bucketSplit;
        bucket=reader.readLine();
        while(bucket!=null){
            bucketSize++;
            bucketSplit=bucket.split(delimeter);
            for(int i=1;i<bucketSplit.length;++i){
                if(bucketSplit[i].matches("[0-9]+")){//is a number
                    if(Integer.parseInt(bucketSplit[i])<C1.length)
                        C1[Integer.parseInt(bucketSplit[i])]++;
                }
            }
            bucket=reader.readLine();
        }
        int thresholdNum = (int)(bucketSize * threshold); //should strictly bigger than this.
        //L1 filters those items in C1 whose frequency is too low to be a part of frequent pair.
        List<Integer> L1 = new ArrayList<Integer>();
        for(int i=0;i<itemSize;++i){
            if(C1[i]>thresholdNum)  L1.add(i);
        }
        //C2 is the set of pair candidates that could be frequent pairs
        HashMap<ItemGroup,Integer> C2 = new HashMap<ItemGroup,Integer>(L1.size()*L1.size());
        for(int i=0;i<L1.size();++i){
            for(int j=i+1;j<L1.size();++j){
                List<Integer> lst = new ArrayList<Integer>();
                ItemGroup ig=null;
                if(L1.get(i)<L1.get(j))
                    ig=new ItemGroup(L1.get(i),L1.get(j));
                else
                    ig=new ItemGroup(L1.get(j),L1.get(i));
                C2.put(ig, ig.occurrence);
            }
        }
        reader.close();
        //Read file second time to find real frequent pairs from
        //pair candidates C2.
        reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(infile)));
        bucket = reader.readLine();//read header that we don't use.

        while(bucket!=null){
            //read each line
            bucket=reader.readLine();
            if(bucket==null)    break;
            bucketSplit = bucket.split(delimeter);
            for(int i=1;i<bucketSplit.length;++i){
                if(!bucketSplit[i].matches("[0-9]+"))
                    continue;
                for(int j=i+1;j<bucketSplit.length;++j){
                    //every possible pairs in each line.
                    //if it appears in C2, count that pair in C2
                    if(!bucketSplit[j].matches("[0-9]+"))
                        continue;
                    ItemGroup ig = new ItemGroup(
                            Integer.parseInt(bucketSplit[i]),
                            Integer.parseInt(bucketSplit[j]));
                    if(C2.containsKey(ig)){//CONTAINS HAS BUG HERE!!! WE SHOULD USE OTHER DS!!
                        C2.put(ig,C2.get(ig)+1);
                    }
                }
            }
        }

        //find true frequent pairs from C2
        for(ItemGroup ig:C2.keySet()){
            int occ=C2.get(ig);
            if(occ>thresholdNum){
                ig.occurrence=occ;
                frequentList.add(ig);
            }
        }
        reader.close();
        return this;
    }

    /**
     * This function finds frequent items with any size,
     * return the max number of items in frequent items.
     * @return
     */
    public int findFrequentAll() throws IOException {
        int k=3;//start with finding 3-frequent items.
        if(frequentList.isEmpty())  return 0;//no action if empty
        while(true) {
            HashSet<ItemGroup> km1Set = new HashSet<ItemGroup>(frequentList);
            int begin = frequentList.size() - 1;
            //possible items in frequent k-items sets.
            Set<Integer> candidateItems = new HashSet<Integer>();
            for (; begin >= 0; --begin) {
                if (frequentList.get(begin).itemList.size() == k - 1) {
                    for (int item : frequentList.get(begin).itemList)
                        candidateItems.add(item);
                } else break;
            }
            begin++;//now begin is the first index of (k-1)-set
            HashMap<ItemGroup, Integer> Ck = new HashMap<ItemGroup, Integer>();
            //Construct Ck set which is the set of possible frequent k-items.
            for (int i = begin; i < frequentList.size(); ++i) {
                //km1Tuple is frequent (k-1)-items that we have known.
                List<Integer> km1Tuple = new ArrayList<Integer>(frequentList.get(i).itemList);
                //we are going to construct frequent k-item candidate from (k-1)-item.
                List<Integer> kTuple;
                for (int item : candidateItems) {
                    kTuple = null;
                    for (int j = 0; j < km1Tuple.size(); ++j) {
                        if(item==km1Tuple.get(j)) break;
                        if (item < km1Tuple.get(j)) {
                            kTuple=new ArrayList<Integer>(km1Tuple);
                            kTuple.add(j, item);
                            break;
                        }
                        if(j==km1Tuple.size()-1){
                            kTuple=new ArrayList<Integer>(km1Tuple);
                            kTuple.add(j+1,item);
                            break;
                        }
                    }
                    if(kTuple==null)    continue;
                    ItemGroup ig = new ItemGroup(kTuple);
                    boolean isC = true;
                    //if kTuple is frequent, at least every subset of kTuple is frequent
                    for(int m=0;m<ig.itemList.size();++m){
                        List<Integer> testL = new ArrayList<Integer>(ig.itemList);
                        testL.remove(m);
                        ItemGroup testG = new ItemGroup(testL);
                        if(!km1Set.contains(testG)){
                            isC=false;
                            break;
                        }
                    }
                    if(isC) Ck.put(ig, ig.occurrence);
                }
            }
            File infile = new File(inputPath);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(infile)));
            reader.readLine();//skip the head line.
            String bucket = reader.readLine();
            String[] bucketSplit;
            while (bucket != null) {
                bucketSplit = bucket.split(delimeter);
                List<List<Integer>> kComb = kCombination(bucketSplit, 1, bucketSplit.length - 1, k);
                //count in Ck
                for (List<Integer> kelement : kComb) {
                    ItemGroup tmp = new ItemGroup(kelement);
                    if (Ck.containsKey(tmp))
                        Ck.put(tmp, Ck.get(tmp) + 1);
                }
                bucket = reader.readLine();
            }
            reader.close();
            int thresholdNum = (int) (bucketSize * threshold); //should strictly bigger than this.
            int count = 0;
            for (ItemGroup g : Ck.keySet()) {
                int occ = Ck.get(g);
                if (occ > thresholdNum) {
                    count++;
                    g.occurrence = occ;
                    frequentList.add(g);
                }
            }
            if (count < k + 1) return k;
            k++;
        }
    }

    /**
     * This function returns all k-combinations of elements in bucketSplit
     * Every combination is in an ordered list.
     * @param bucketSplit
     * @param start
     * @param end
     * @param k
     * @return
     */
    public List<List<Integer>> kCombination(String[] bucketSplit, int start, int end, int k){
        //remember to judge if the element is a number.
        List<Integer> items = new ArrayList<Integer>();
        for(int i=start;i<=end;++i){
            if(!bucketSplit[i].matches("[0-9]+"))
                continue;
            items.add(Integer.valueOf(bucketSplit[i]));
        }
        Set<List<Integer>> combSet1 = new HashSet<List<Integer>>();
        Set<List<Integer>> combSet2 = new HashSet<List<Integer>>();
        for(int b:items){
            List<Integer> l = new ArrayList<Integer>();
            l.add(b);
            combSet1.add(l);
        }
        for(int i=1;i<k;++i){
            for(List<Integer> t:combSet1){
                for(int b:items){
                    for(int j=0;j<t.size();++j){
                        if(b==t.get(j)) break;
                        if(b<t.get(j)){
                            List<Integer> tt = new ArrayList<Integer>(t);
                            tt.add(j,b);
                            combSet2.add(tt);
                            break;
                        }
                    }
                }
            }
            combSet1=combSet2;
            combSet2 = new HashSet<List<Integer>>();
        }
        return new ArrayList<List<Integer>>(combSet1);
    }

    /**
     * print frequent items on screen.
     * Every line is the list of frequent items followed by percentage.
     */
    public void print(){
        for(ItemGroup ig:frequentList){
            for(int item:ig.itemList)
                System.out.print(item+",");
            int perc = ig.occurrence*100/bucketSize;
            System.out.print(perc+"%\n");
        }
    }

    public void printfile() throws IOException{
        File outfile = new File(outputPath);
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(outfile)));
        for(ItemGroup ig:frequentList){
            for(int item:ig.itemList)
                writer.write(item+",");
            int perc = ig.occurrence*100/bucketSize;
            writer.write(perc+"%\n");
        }
        writer.flush();
        writer.close();
    }
}
