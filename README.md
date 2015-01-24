Frequent_Items
==============
A Machine Learning App for online business.
==============
#0.Overview 
This project is able to find frequent item groups among large scale of transaction data.
It is well designed to use less memory to allow more larger input scale.
It is able to retrieve the frequent group in all size, not only frequent pairs.
You can use it as a library which is easy to program with.

#1.Instruction
To run the program, please execute the jar file: A-Priori.jar. The jar file needs at least 2 arguments, input file path and output file path. Another option is 4 arguments, input file path, output file path, delimiter and threshold. 

Please notice, the program only accepts these two options, other combination of arguments will cause failure. The following states the detail of those arguments.
*	input file path: The path of input file.
*	output file path: The path of output file. Create the file if not exists.
*	delimiter: This is optional, default value is comma (“,”). If you are using other delimiter, please specify.
*	threshold: This is optional, default value is 0.7 based on the description of problem1. But you can set any value between 0 to 1. (0.7 means at least 70% occurrence to be considered as frequent group.)

Example:<br />
(Input file is in.csv, output file is out.txt) <br />
java –jar ./A-Priori.jar ./in.csv ./out.txt<br />

(Besides input/output files, this line specifies delimiter as “-” and threshold 0.5)<br />
java –jar ./A-Priori.jar ./in.csv ./out.txt - 0.5<br />

#2.Input and Output
I strictly follow the input file format described in following.
*	The first line must be header, indicating the name of the each possible item, separated by delimiter.
*	Each line that follows the header must begin with the transaction ID followed by item numbers, which all are separated by delimiter.
*	Items must be numbered from 0 to the max value consecutively.

Example:<br />
item1,item2,item3,item4<br />
T001,0,3<br />
T002,1,2<br />
T003,0,1,2,3<br />
T004,1,2<br />

Regarding to output file, each line contains a list of frequent group separated by delimiter comma (“,”), followed by the percentage of occurrence in the input file.

For example, if we set threshold as 0.2, then output is possibly be:<br />
0,1,54%<br />
4,5,50%<br />
1,4,29%<br />
0,4,42%<br />
1,5,57%<br />
0,5,74%<br />
0,4,5,41%<br />
1,4,5,27%<br />
0,1,4,23%<br />
0,1,5,53%<br />
0,1,4,5,23%<br />

#3.Algorithm
I applied A-Priori algorithm to solve this problem, which is possible to handle large-scale case. The reference could be found on Wiki: http://en.wikipedia.org/wiki/Apriori_algorithm
The process of the algorithm is shown in the following figure: 
![Picture for a-priori algorithm](/resource/apriori.png)
 

#4.Tips for FrequentItems Class
FrequentItems class calculates the frequent groups from a file. It has two main methods, findFrequentPairs and findFrequentAll. findFrequentPairs finds the frequent item pairs. After that, invoking findFrequentAll, you can find all frequent groups. Results are stored in a member variable frequentList. Invoking print and printfile, you can print the result to stdout or file. The example code is shown as follows:
```
FrequentItems fi = new FrequentItems("in.csv");
//set threshold or use default value 0.7
fi.setThreshold(0.2);
//set delimiter or use default “,”
fi.setDelimeter(“,”);
//you can cascade two find methods.
fi.findFrequentPairs().findFrequentAll();
fi.print();
fi.setOutputPath(“out.txt”);
fi.printfile();
```
