# MaMeMiFilter

This work was used in the publication of "Simple real-time QRS detector with the MaMeMi filter"  available online on: http://www.sciencedirect.com/science/article/pii/S1746809415001032 
  
I encourage that you cite it as:
 [*] Castells-Rufas, David, and Jordi Carrabina. "Simple real-time QRS detector with the MaMeMi filter." 
     Biomedical Signal Processing and Control 21 (2015): 137-145.

I've used Netbeans 8.0.2 to compile and execute the code.

The code automatically downloads the dat and atr MIT-BIH files from Internet and stores them locally.
The directory of the local copy of the downloaded files is specified in **dir** member of the **cat.uab.cephis.mitbih.ECGProcess** class

To test the whole MIT-BIH database, run the **cat.uab.cephis.mitbih.TestWholeSet** class

To test a particular file, run the **cat.uab.cephis.mitbih.Test<number>** class

I tried several methods to test the effectiveness of the MaMeMi filter. The code supports to try different methods, by changing a number (which identifies each method).
The different methods are implemented in the ECGProcess class. 
The final method described in the paper is #5.

All the infrastructure could be used to test many other methods reported in the literature.

