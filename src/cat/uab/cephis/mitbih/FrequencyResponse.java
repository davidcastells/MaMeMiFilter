/**
 * Copyright (C) David Castells-Rufas, CEPHIS, Universitat Autonoma de Barcelona  
 * david.castells@uab.cat
 * 
 * This work was used in the publication of "Simple real-time QRS detector with the MaMeMi filter"
 * available online on: http://www.sciencedirect.com/science/article/pii/S1746809415001032 
 * 
 * I encourage that you cite it as:
 * [*] Castells-Rufas, David, and Jordi Carrabina. "Simple real-time QRS detector with the MaMeMi filter." 
 *     Biomedical Signal Processing and Control 21 (2015): 137-145.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cat.uab.cephis.mitbih;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static cat.uab.cephis.mitbih.ECGProcess.SELECT_VALUE;

/**
 *
 * @author dcr
 */
public class FrequencyResponse {
    public static void main(String[] args) 
    {
	File dir = new File("C:\\Projects\\Doctorat\\INT_Papers\\03 Acc - 2014 BSPC - QRS Detector\\Freq Response");
	
	generateMultipleExcels(dir);
	//generateGnu3dplot(dir);
	
    }
    
    public static void test()
    {
	double[] sin = SignalGenerator.greateSinSignal(10, 1, 3600, 150, 1024);
	double[] sqr = SignalGenerator.greateSquareSignal(10, 1, 3600, 150, 1024);
	
	Plot1D plot = new Plot1D("signals");
	
	plot.drawYAxis = true;
	plot.setData(sin);
	plot.setData2(sqr);
	plot.setVisible(true);
	
	double[] filtered1_1 = ECGProcess.minMaxFilter(sin,  1, 1, SELECT_VALUE   );
	    
	plot = new Plot1D("filtered");
	
	plot.drawYAxis = true;
	plot.setData(sin);
	plot.setData3(filtered1_1);
	plot.setVisible(true);
	
	double a = getAmplitude(filtered1_1, 3600);
	
	System.out.println("Amplitude = " + a);
    }

    private static double getAmplitude(double[] s, int skip) 
    {
	double max = s[skip];
	double min = s[skip];
	
	for (int i=skip; i < s.length; i++)
	{
	    if (max < s[i]) max = s[i];
	    if (min > s[i]) min = s[i];
	}
	
	return (max - min)/2;
    }

    private static void freqResponse(File outFile, double lowFreq, int highFreq, int divs, double amplitude, int filter1, int filter2) throws FileNotFoundException
    {
	PrintWriter out = new PrintWriter(outFile);
	double step = (highFreq - lowFreq) / divs;
	
	out.println("Freq (Hz);Response;Output;Input;");
	
	for (double i = lowFreq; i < highFreq; i += step)
	{
	    double outputAmplitude = computeResponse(i, amplitude, filter1, filter2);
	    
	    out.println("" + i + ";" + (20*Math.log10(outputAmplitude/amplitude)) + ";" + outputAmplitude + ";" + amplitude+ ";");
	}
	
	out.close();
    }
    
    private static ArrayList<Map.Entry<Double, Double>> freqResponseAsHash(double lowFreq, int highFreq, int divs, double amplitude, int filter1, int filter2) throws FileNotFoundException
    {
	ArrayList<Map.Entry<Double, Double>> ret = new ArrayList<>();
	
	double step = (highFreq - lowFreq) / divs;
	
	
	for (double i = lowFreq; i < highFreq; i += step)
	{
	    double outputAmplitude = computeResponse(i, amplitude, filter1, filter2);
	
	    
	    ret.add(new AbstractMap.SimpleEntry(i, 20*Math.log10(outputAmplitude/amplitude)));
	}
	
	return ret;
    }

    private static double computeResponse(double freq, double amplitude, int filter1, int filter2) 
    {
	double[] sin = SignalGenerator.greateSinSignal(10, freq, 3600, amplitude, 1024);
	double[] filtered = ECGProcess.minMaxFilter(sin,  filter1, filter2, SELECT_VALUE   );

	return getAmplitude(filtered, 3600);
    }

    private static void generateMultipleFreqResponsePerAmplitude(File dir, int start, int end, int step, int divs, int f1, int f2) throws FileNotFoundException
    {
	
	    for (int a=start; a < end; a += step)
	    {
		 freqResponse(new File(dir, "filt_"+f1+"_"+f2+"_"+a+".csv"), 0.001, 50, divs, a, f1, f2);		
	    }
	
    }

    private static void generateSingleFreqResponsePerAmplitude(File dir, int start, int end, int step, int divs, int filter1, int filter2) throws FileNotFoundException {
	
	ArrayList<Map.Entry<Integer, ArrayList<Map.Entry<Double, Double>>>> cols = new ArrayList<>();
	
	try
	{
	    for (int a=start; a < end; a += step)
	    {
		System.out.println("Computing " + a);
		// Generate Column
		ArrayList<Map.Entry<Double, Double>> col = freqResponseAsHash(0.001, 50, divs, a, filter1, filter2);	
		
		cols.add(new AbstractMap.SimpleEntry(a, col));
	    }
	}
	catch (FileNotFoundException ex) {
	    Logger.getLogger(FrequencyResponse.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	File outFile =  new File(dir, "filt_"+filter1+"_"+filter2+"_amplitude.csv");
	PrintWriter out = new PrintWriter(outFile);
	
	System.out.println("Freq (Hz);");
	
	ArrayList<Map.Entry<Double, Double>> firstCol = cols.get(0).getValue();
	
	// print headers
//	for (Map.Entry<Integer, ArrayList<Map.Entry<Double, Double>>> col : cols)
//	{
//	    out.print("" + col.getKey() + ";");
//	    
//	    if (firstCol == null)
//		    firstCol = col.getValue();
//	}
//	
//	out.println();
	
	// now print rows
	for (int i=0; i < firstCol.size(); i++)
	{
	    Double key = firstCol.get(i).getKey();
	    
	    System.out.println(""  + key + ";");
	    
	    for (Map.Entry<Integer, ArrayList<Map.Entry<Double, Double>>> col : cols)
	    {
		Integer y = col.getKey();
		ArrayList<Map.Entry<Double, Double>> colData = col.getValue();
		
		Map.Entry<Double, Double> row = colData.get(i);
		
		Double x = row.getKey();
		Double z = row.getValue();
		//out.print("" + row.getValue() + ";");
		out.println("" + x + ";" + y + ";" + z + ";");
	    }
	    
	    out.println();
	}
	
	out.close();
    }

    private static void generateGnu3dplot(File dir) 
    {
	int freqDivs = 100;
	int ampDivs = 100;
	
	int step = (1000-10) / ampDivs;
	try {
	    //generateMultipleFreqResponsePerAmplitude(dir, 10, 1000, 20, divs);
	    for (int f1 = 1; f1 < 10; f1++)
		for (int f2=1; f2 < 10; f2++)
		    generateSingleFreqResponsePerAmplitude(dir, 10, 1000, step, freqDivs, f1, f2);
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(FrequencyResponse.class.getName()).log(Level.SEVERE, null, ex);
	}	
    }
    
    private static void generateMultipleExcels(File dir) 
    {
	int freqDivs = 100;
	int ampDivs = 100;
	
	int step = (1000-10) / ampDivs;
	try {
	    //
	    for (int f2=1; f2 < 10; f2++)
		for (int f1 = 1; f1 < 10; f1++)
		{
		    
		
		    generateMultipleFreqResponsePerAmplitude(dir, 10, 1000, step, freqDivs, f1, f2);
		    //generateSingleFreqResponsePerAmplitude(dir, 10, 1000, step, freqDivs, f1, f2);
		}
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(FrequencyResponse.class.getName()).log(Level.SEVERE, null, ex);
	}	
    }
}
