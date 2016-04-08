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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dcr
 */
public class ECGProcess {
    private static int tolaratedError = 80;
    
    public static File dir = new File("C:\\Projects\\Research\\INT_QRS\\data");
    

    /**
     * Process with high pass filter
     * Edge detector takes into account an adaptive edge detector
     * and looks for another maxixum value on the window of the local maximum found
     * @param chunk
     * @param read
     * @param beats
     * @return 
     */
    private static int[] processM2(String set, double[] chunk0, double[] chunk1, int[] beats, boolean debug, boolean bFP, int skipped) 
    {        
        double[] hpData2 = minMaxFilter(chunk0,  2, 2, SELECT_VALUE   );

        double[] maxThreshold = new double[chunk0.length];

        double[] finald = hpData2; // sub(abs, medianRange);

        int[] ret3 = posEdgeCheckOtherMaxAdaptive(finald, maxThreshold, 0.4, 90, 700);
                
        int[] errors = reportResults(set, ret3, beats);

        int error = findFirstError(ret3, beats, bFP, skipped, debug);
        
        if (error != -1 && debug)
        {
            int focus = error - 50;
            if (focus < 0)
                focus = 0;
            
            Plot1D[] plots = plotSignals(set, new double[][]{
                finald,
                hpData2, 
                chunk0},
                      new String[]{
                          " Final",
                          " HP",
                          " Source"  }, 
                      beats, focus, skipped);
              
            plots[0].setData2(maxThreshold);          
//            plots[0].setData3(range);  
            
            
        }
        
        return  errors;
    }

    
    /**
     * @param set
     * @param chunk0
     * @param chunk1
     * @param beats
     * @param debug
     * @param bFP
     * @param skipped
     * @return 
     */
    private static int[] processM3(String set, double[] chunk0, double[] chunk1, int[] beats, boolean debug, boolean bFP, int skipped) 
    {
        int decimation = 1;

        double[] hpData2 = minMaxFilter(chunk0,  2, 2, SELECT_VALUE   );
        

        double[] maxThreshold = new double[chunk0.length];
        double[] minThreshold = new double[chunk0.length];

        double[] finald = hpData2; // sum(sum(derivated, sub(hpData2, range)), hpData2);

        int[] ret3 = eitherEdgeCheckOtherMaxAdaptive(finald, maxThreshold, minThreshold, 0.4, 1, 90, 700);
                
        int[] errors = reportResults(set, ret3, beats);

        int error = findFirstError(ret3, beats, bFP, skipped, debug);
        
        if (error != -1 && debug)
        {
            int focus = error - 50;
            if (focus < 0)
                focus = 0;
            
            Plot1D[] plots = plotSignals(set, new double[][]{
                finald,
                hpData2, 
                chunk0},
                      new String[]{
                          " Final",
                          " HP",
                          " Source"  }, 
                      beats, focus, skipped);
              
            plots[0].setData2(maxThreshold);          
            plots[0].setData3(minThreshold);  
        }
        
        return  errors;
    }

    private static int[] processM4(String set, double[] chunk0, double[] chunk1, int[] beats, boolean debug, boolean bFP, int skipped) 
    {
        int decimation = 1;

        double[] hpData2 = minMaxFilter(chunk0,  2, 2, SELECT_VALUE   );
        double[] range = minMaxFilter(chunk0,  2, 2, SELECT_RANGE  );
  
        double[] squared =  reduceRange(hpData2, range);
        
        
//        
//        double[] sub = sub(range, derivated);

        double[] maxThreshold = new double[chunk0.length];
        double[] minThreshold = new double[chunk0.length];

        double[] finald = squared; // limitRange(scale(squared, 3), 100, -100); // absolute(integrated); // limitRange(squared, 200, -200); // sum(sum(derivated, sub(hpData2, range)), hpData2);

//        double[] finald = triangleHeight(sub(hpData2, range),15); // integrateOverCount(trian); // positive(sub(spike, sub));
        
//        int[] ret3 = posEdgeAdaptive(finald, maxThreshold, 0.2, 90, 700);
        int[] ret3 = eitherEdgeCheckOtherMaxAdaptive(finald, maxThreshold, minThreshold, 0.3, 1, 90, 700);
//        int[] ret3 = pulseEdgeAdaptive(finald, maxThreshold, minThreshold, 
//                .4, .4,
//                22, .6,
//                90/decimation,      // min gap after pulse 
//                700/decimation,     // max gap after pulse
//                90/decimation,      // max positive pulse
//                60/decimation, false);

//        int[] ret3 = findMaxInWindow(finald, maxThreshold, 0.5, 90/decimation, 700/decimation);
                
        int[] errors = reportResults(set, ret3, beats);

        int error = findFirstError(ret3, beats, bFP, skipped, debug);
        
        if (error != -1 && debug)
        {
            int focus = error - 50;
            if (focus < 0)
                focus = 0;
            
            Plot1D[] plots = plotSignals(set, new double[][]{
                finald,
                range,
                squared,
                hpData2, 
                chunk0},
                      new String[]{
                          "Final",
                          "range",
                          "Squared",
                          "HP",
                          "Source"  }, 
                      beats, focus, skipped);
              
            plots[0].setData2(maxThreshold);          
            plots[0].setData3(minThreshold);  
            
            
        }
        
        return  errors;
    }
    
    private static double[] minMaxFilter(double[] chunk, double delta, double deltaFactor)
    {
        return minMaxFilter(chunk, delta, deltaFactor, SELECT_VALUE);
    }
    
    public static int SELECT_VALUE = 0x01;
    public static int SELECT_MEAN =  0x02;
    public static int SELECT_RANGE = 0x04;
    public static int SELECT_MAX =  0x08;
    public static int SELECT_MAX_MINUS_MEAN =  0x10;
    public static int SELECT_VALUE_MINUS_MIN = 0x20;
    public static int SELECT_MEAN_OR_VALUE = 0x40;
    public static int SELECT_VALUE_OUT_OF_RANGE = 0x80;
    
    public static int METHOD_JUMP = 0x100;
    
    static double[] minMaxFilter(double[] chunk, double delta, double deltaFactor, int flags) 
    {
        double[] ret = new double[chunk.length];
        
        double max = chunk[0];
        double min = chunk[0];
        
        
        double mean;
        
        for (int i=0; i < chunk.length; i++)
        {
            if (chunk[i] >= max) 
            {
                if ((flags & METHOD_JUMP) != 0)
                    max = chunk[i];
                else    
                    max += delta * deltaFactor;
            }
            else
            {
                max -= delta;
            }
            
            if (chunk[i] <= min)
            {
                if ((flags & METHOD_JUMP) != 0)
                    min = chunk[i];
                else
                    min -= delta * deltaFactor;
            }
            else
            {
                min += delta;
            }

            mean = (max + min) / 2;
            
            double value = chunk[i];
            
            if ((flags & SELECT_VALUE) != 0)
                ret[i] = value - mean;
            if ((flags & SELECT_MEAN) != 0)
                ret[i] = mean;
            if ((flags & SELECT_RANGE)!= 0)
                ret[i] = (max > min)? max - min :  0;
            if ((flags & SELECT_MAX)!= 0)
                ret[i] = max;
            if ((flags & SELECT_MAX_MINUS_MEAN) != 0)
                ret[i] = max - mean;
            if ((flags & SELECT_VALUE_MINUS_MIN) != 0)
                ret[i] = value - min;
            if ((flags & SELECT_MEAN_OR_VALUE) != 0)
                ret[i] = (value>max)? value :  (value < min) ? value : mean;
            if ((flags & SELECT_VALUE_OUT_OF_RANGE) != 0)
                ret[i] = (value>=max)? (value - mean) : (value <= min)? (value - mean) : 0;
        }
        
        return ret;
    }

    private static double[] derivate(double[] in) 
    {
        double[] ret = new double[in.length];
        
        ret[0] = 0;
        
        for (int i=1; i < ret.length; i++)
        {
            ret[i] = in[i] - in[i-1];
        }
        
        return ret;
    }
    
    private static double[] ridges(double[] in) 
    {
        double[] ret = new double[in.length];
        
        double lastx = in[0];
        int state = 0;  
        
        for (int i=1; i < ret.length; i++)
        {
            double x = in[i];

            ret[i] = 0;
 
            switch (state)
            {
                case 0: if (x > lastx) state = 1; break;   // up
                case 1: if (x < lastx) 
                        {
                            // down
                            state = -1;
                            ret[i] = lastx;
                                
                        }  
                        break;
                case -1:
                        state = 0;
                        break;
                    
            }
            
            lastx = x;
        }
        
        return ret;
    }
    
     
    private static double[] valleys(double[] in) 
    {
                double[] ret = new double[in.length];
        
        double lastx = in[0];
        int state = 0;  
        
        for (int i=1; i < ret.length; i++)
        {
            double x = in[i];

            ret[i] = 0;
 
            if (i == 22310)
                i = i*1;
            
            switch (state)
            {
                case 0: if (x < lastx) state = 1; break;   // down
                case 1: if (x > lastx) 
                        {
                            // up
                            state = -1;
                            ret[i] = lastx;
                                
                        }  
                        break;
                case -1:
                        state = 0;
                        break;
                    
            }
            
            lastx = x;
        }
        
        return ret;
    }
    
    
    private static double[] zeroPass(double[] in) 
    {
        double[] ret = new double[in.length];
        
        ret[0] = 0;
        
        for (int i=1; i < ret.length; i++)
        {
            if (in[i] >= 0 && in[i-1] < 0)
                ret[i] = in[i] - in[i-1];
            else if (in[i] <= 0 && in[i-1] > 0)
                ret[i] = in[i-1] - in[i];
            else
                ret[i] = 0; // in[i] - in[i-1];
        }
        
        return ret;
    }
    
    private static double[] average(double[] in, int n) 
    {
        double[] ret = new double[in.length];

        
        
        for (int i=0; i < ret.length; i++)
        {
            double sum = 0;
            int m=0;
            
            for (int j=0; j < n; j++)
                if ((i-j)>=0)
                {
                    sum += in[i-j];
                    m++;
                }
            
            ret[i] = sum / m;
        }
        
        return ret;
    }

    private static int[] negEdge(double[] data, double threshold, int minGap) 
    {
        ArrayList<Integer> events = new ArrayList<>();
        
        for (int i=1; i < data.length; i++)
        {
            if ((data[i] <= threshold) && (data[i-1] > threshold))
            {
                events.add(i);
                
                i--;
                i += minGap;
            }
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }
    
    /**
     * 
     * @param data
     * @param threshold
     * @param minGap minimum gap between pulses
     * @return 
     */
    private static int[] posEdge(double[] data, double threshold, int minGap) 
    {
        ArrayList<Integer> events = new ArrayList<>();
        
        for (int i=1; i < data.length; i++)
        {
            if ((data[i] >= threshold) && (data[i-1] < threshold))
            {
                events.add(i);
                
                i--;
                i += minGap;
            }
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }

    /**
     * When a pos edge on a threshold is found report it as being a pulse.
     * Check again after a minGap.
     * If a maxGap is reached without having found an edge, then update the threshold
     * to the initial value
     * 
     * @param data
     * @param threshold
     * @param factor
     * @param minGap
     * @param maxGap
     * @return 
     */
    private static int[] posEdgeAdaptive(double[] data, double[] threshold, double factor, int minGap, int maxGap) 
    {
        ArrayList<Integer> events = new ArrayList<>();
        
        int initialThreshold = (int) (getMaxInRange(data, 0, 600) * factor);
        
        double lastThreshold;
        
        threshold[0] = initialThreshold;
        int gap = minGap;
        
        double currentThreshold = initialThreshold;
        
        for (int i=1; i < data.length; i++, gap++)
        {
            threshold[i] = currentThreshold;
            
            if (gap <= minGap)
                continue;
            
            if (gap > maxGap)
                currentThreshold = initialThreshold;
            
            if ((data[i] >= currentThreshold) && (data[i-1] < currentThreshold))
            {
                events.add(i);
                
                double newThreshold = getMaxInRange(data, i, minGap) * factor; 
                
                
                lastThreshold = initialThreshold;
                currentThreshold = (lastThreshold + newThreshold) / 2;
                
                
                gap = 0;
            }
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }
    
    /**
     * When a pos edge is found, look for the local maximum over the minGap range
     * @param data
     * @param threshold threshold array that is filled
     * @param factor
     * @param minGap
     * @param maxGap
     * @return 
     */
    private static int[] posEdgeCheckOtherMaxAdaptive(double[] data, double[] threshold, double factor, int minGap, int maxGap) 
    {
        ArrayList<Integer> events = new ArrayList<>();
        
        int initialThreshold = (int) (getMaxInRange(data, 0, maxGap) * factor);
        
        double lastThreshold;
        
        threshold[0] = initialThreshold;
        int gap = minGap;
        
        double currentThreshold = initialThreshold;
        
        for (int i=1; i < data.length; i++, gap++)
        {
            threshold[i] = currentThreshold;
            
            if (gap <= minGap)
                continue;
            
            if (gap > maxGap)
                currentThreshold = initialThreshold;
            
            if ((data[i] >= currentThreshold) && (data[i-1] < currentThreshold))
            {
                int lasti = i;
                int recenti;
                
                // The i index could be a local max, ensure this is a max
                do
                {
                    recenti = i;
                    i = getMaxIndexInRange(data, i, minGap);
                } while (i != recenti);
                
                for (int k = lasti; k <= i; k++)
                    threshold[k] = currentThreshold;
                
                events.add(i);
                
                double newThreshold = data[i] * factor; 
                
                
                lastThreshold = initialThreshold;
                currentThreshold = (lastThreshold + newThreshold) / 2;
                
                
                gap = 0;                
            }
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }
    

    /**
     * 
     * @param data
     * @param threshold
     * @param factor
     * @param minGap
     * @param maxGap
     * @return 
     */
    private static int[] eitherEdgeCheckOtherMaxAdaptive(double[] data, 
            double[] threshold, 
            double[] negThreshold,
            double factor, double negFactor, int minGap, int maxGap) 
    {
        ArrayList<Integer> events = new ArrayList<>();
        
        int initialThreshold = (int) (getMaxInRange(data, 0, maxGap) * factor);
        
        double lastThreshold;
        
        threshold[0] = initialThreshold;
        negThreshold[0] = initialThreshold * -negFactor;
        
        int gap = minGap;
        
        double currentThreshold = initialThreshold;
        double currentNegThreshold = initialThreshold * -negFactor;
        
        for (int i=1; i < data.length; i++, gap++)
        {
            threshold[i] = currentThreshold;
            negThreshold[i] = currentThreshold * -negFactor;
            
            if (gap <= minGap)
                continue;
            
            if (gap > maxGap)
            {
                currentThreshold = initialThreshold;
                currentNegThreshold = currentThreshold * -negFactor;
            }
            
            if ((data[i] >= currentThreshold) && (data[i-1] < currentThreshold))
            {
                int lasti = i;
                int recenti;
                
                // The i index could be a local max, ensure this is a max
                do
                {
                    recenti = i;
                    i = getMaxIndexInRange(data, i, minGap);
                } while (i != recenti);
                
                for (int k = lasti; k <= i; k++)
                {
                    threshold[k] = currentThreshold;
                    negThreshold[k] = currentThreshold * -negFactor;
                }
                
                events.add(i);
                
                double newThreshold = data[i] * factor; 
                
                
                lastThreshold = initialThreshold;
                currentThreshold = (lastThreshold + newThreshold) / 2;
                currentNegThreshold = currentThreshold * -negFactor;                
                
                gap = 0;                
            }
            else if ((data[i] <= currentNegThreshold) && (data[i-1] > currentNegThreshold))
            {
                int lasti = i;
                int recenti;
                
                // The i index could be a local max, ensure this is a max
                do
                {
                    recenti = i;
                    i = getMinIndexInRange(data, i, minGap);
                } while (i != recenti);
                
                for (int k = lasti; k <= i; k++)
                {
                    threshold[k] = currentThreshold;
                    negThreshold[k] = currentThreshold * -negFactor;
                }
                
                events.add(i);
                
                double newThreshold = (data[i]*-negFactor) * factor; 
                
                
                lastThreshold = initialThreshold;
                currentThreshold = (lastThreshold + newThreshold) / 2;
                currentNegThreshold = currentThreshold * -negFactor;                
                
                gap = 0;                
            }
            
            
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }
        
    /**
     * 
     * @param data
     * @param threshold
     * @param factor
     * @param minGap minimum gap after beat has been detected
     * @param maxGap
     * @return 
     */
    private static int[] findMaxInWindow(double[] data, double[] threshold, double factor, int minGap, int maxGap) 
    {
        ArrayList<Integer> events = new ArrayList<>();
        
        double initialThreshold =  (getMaxInRange(data, 0, 600) * factor);
        
        double lastThreshold;
        
        threshold[0] = initialThreshold;
        int gap = minGap;
        
        double currentThreshold = initialThreshold;
        
        for (int i=1; i < data.length; )
        {
            threshold[i] = currentThreshold;
                        
            int index = getMaxIndexInRange(data, i, maxGap);
            
            events.add(index);
            
            double newThreshold = data[index] * factor;
                                
            lastThreshold = currentThreshold;
            currentThreshold = (lastThreshold + newThreshold) / 2;
                
            for (int k=i; i < index + minGap; i++)
                if (i<data.length)
                threshold[i] = currentThreshold;
            
             i = index + minGap;
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }
/*
        private static int[] pulseDetectorAdaptive(double[] data, double[] maxThreshold, double[] minThreshold, 
            double posFactor, double posMaxFactor, 
            double negFactor, double negMinFactor,
            double minPosSlope, double minNegSlope,
            int minGap, int maxGap, 
            int maxPositivePulse, 
            int maxNegativePulse) 
    {
        ArrayList<Integer> events = new ArrayList<>();

        int initialMaxThreshold = (int) (getMaxInRage(data, 0, 400) * posFactor);
        int initialMinThreshold = (int) (getMinInRage(data, 0, 400) * negFactor);
        
        System.out.println("initial max threshold: " + initialMaxThreshold);
        System.out.println("initial min threshold: " + initialMaxThreshold);

        int gap = minGap;
        int state = 0;
        int count;
        
        for (int i = 1; i < data.length; i++)
        {
            gap++;
            
            if (gap < minGap)
                continue;
            
            double slope = data[i] - data[i-1];
            
            if (state == 0)
            {
                if (slope > minPosSlope)
                {
                    // rising 
                    state = 1;
                    count = 0;
                    val = 
                }
            }
            else if (state == 1)
            {
                count++;
                
                if (slope < 0) && ()
            }
            
        }
        
        
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }*/
        
    /**
     * 
     * @param data
     * @param maxThreshold
     * @param minThreshold
     * @param posFactor
     * @param posMaxFactor
     * @param negFactor
     * @param negMinFactor
     * @param minGap
     * @param maxGap
     * @param maxPositivePulse
     * @param maxNegativePulse
     * @return 
     */
    private static int[] pulseEdgeAdaptive(double[] data, double[] maxThreshold, double[] minThreshold, 
            double posFactor, double posMaxFactor, 
            double negFactor, double negMinFactor,
            int minGap, int maxGap, 
            int maxPositivePulse, 
            int maxNegativePulse, boolean bNeg) 
    {
        ArrayList<Integer> events = new ArrayList<>();

        int initialMaxThreshold = (int) (getMaxInRange(data, 0, 600) * posFactor);
        int initialMinThreshold = (int) (getMinInRage(data, 0, 600) * negFactor);
        
        System.out.println("initial max threshold: " + initialMaxThreshold);
        System.out.println("initial min threshold: " + initialMinThreshold);

        double lastMaxThreshold;
        double lastMinThreshold;
        
        maxThreshold[0] = initialMaxThreshold;
        minThreshold[0] = initialMinThreshold;
        int gap = minGap;
        
        double currentMaxThreshold = initialMaxThreshold;
        double currentMaxMaxThreshold = initialMaxThreshold/posFactor * posMaxFactor;
        double currentMinThreshold = initialMinThreshold;
        double currentMinMinThreshold = initialMinThreshold/negFactor * negMinFactor;

        int state = 0;
        int pulseCount=0;
        double localMax = 0;
        double localMin = 0;
        double maxInPulse = 0;
        double minInPulse = 0;
        
        for (int i=1; i < data.length; i++, gap++)
        {
            maxThreshold[i] = currentMaxThreshold;
            minThreshold[i] = currentMinThreshold;
            
            if (gap <= minGap)
                continue;
            
            if (data[i] > localMax) localMax = data[i];
            if (data[i] < localMin) localMin = data[i];
            
            if (gap > maxGap)
            {
                currentMaxThreshold = initialMaxThreshold;
                currentMinThreshold = initialMinThreshold;
            }
            
            if (state == 0)
            {
                // looking for a positive edge
                if ((data[i] >= currentMaxThreshold) && (data[i-1] < currentMaxThreshold))
                {
                    state = 1; // rising
                    pulseCount = 0;
                    maxInPulse = data[i];
                }
                
                if ((data[i] <= currentMinThreshold) && (data[i-1] < currentMinThreshold) && bNeg)
                {
                    state = 2; // falling
                    pulseCount = 0;
                    minInPulse = data[i];
                }
            }
            else if (state == 1)
            {
                pulseCount++;
                
                if (data[i] > maxInPulse) maxInPulse = data[i];
                                
                if (pulseCount > maxPositivePulse)
                {
                    state = 0;
                }
                else if ((data[i] <= currentMaxThreshold) && (data[i-1] > currentMaxThreshold))
                {
                    // falling edge
                    if (maxInPulse > currentMaxMaxThreshold)
                    {
                        events.add(i-pulseCount/2);

                        double newThreshold = localMax * posFactor; 

                        lastMinThreshold = initialMaxThreshold;
                        currentMaxThreshold = (lastMinThreshold + newThreshold) / 2;
                        currentMaxMaxThreshold = (currentMaxThreshold / posFactor) * posMaxFactor;
                        
                        gap = 0;
                        
                        localMax = 0;
//                        localMin = 0;
                    }
//                    else
//                    {
//                        System.out.println("Max not enough " + localMax + " < " + currentMaxThreshold);
//                    }
                    
                    state = 0;
                }
                
                
            }
            else if (state == 2)
            {
                pulseCount++;
                                
                if (pulseCount > maxNegativePulse)
                {
                    state = 0;
                }
                else if ((data[i] >= currentMinThreshold) && (data[i-1] < currentMinThreshold))
                {
                    if (minInPulse < currentMinMinThreshold)
                    {
                        events.add(i-pulseCount);

                        double newThreshold = localMin * negFactor; 

                        lastMinThreshold = initialMinThreshold;
                        currentMinThreshold = (lastMinThreshold + newThreshold) / 2;
                        currentMinMinThreshold = (currentMinThreshold / negFactor) * negMinFactor;

                        gap = 0;

//                        localMax = 0;
                        localMin = 0;
                    }
//                    else
//                    {
//                        System.out.println("Max not enough " + localMax + " < " + currentMaxThreshold);
//                    }
                    
                    state = 0;
                }
                
                
            }

        }
        
        
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }
    
    

    
    private static int[] sigmaEdge(double[] data, int threshold1, int threshold2, int threshold3, int minGap) 
    {
        ArrayList<Integer> events = new ArrayList<>();
        
        for (int i=2; i < data.length; i++)
        {
            if ((data[i-2] > threshold1) &&
                (data[i-1] > threshold2) && 
                (data[i] < threshold3))
            {
                events.add(i);
                
                i--;
                i += minGap;
            }
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }
    
    private static int[] posEdge(double[] data, double[] range, double factor, int minGap) 
    {
        ArrayList<Integer> events = new ArrayList<>();
        
        double threshold = 20;
        double lastMax = threshold / factor;
        
        for (int i=1; i < data.length; i++)
        {
            
            if ((data[i] >= threshold) && (data[i-1] < threshold))
            {
                events.add(i);
                
                
                
                for (int j=0; j < minGap-1; j++)
                {
                    if (i+j+1 >= data.length)
                        break;
                    
                    if (data[i+j+1] > data[i+j])
                        lastMax = data[i+j+1];
                }
                
                threshold = lastMax * factor;
                
                i--;
                i += minGap;
            }
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;
    }

    private static double[] hysteresis(double[] in, double[] derivate, int m) {
        double[] ret = new double[in.length];
        
        ret[0] = 0;
        
        for (int i=1; i < ret.length; i++)
        {
            int vp = 0;
            double deltap = 0;
            int vn = 0;
            double deltan = 0;
            
            for (int j=0; j < m; j++)
            {
                int k = i - m + 1 + j;
                
                if ((k >= 0) && (k < derivate.length))
                {
                    if ((in[k] > 0) && (derivate[k] > 0))
                    {
                        vp++;
                        deltap += derivate[k];
                    }
//                    
//                    if (derivate[k] < 0)
//                    {
//                        vn++;
//                        deltan += derivate[k];   
//                    }
                }
                
            }
            
            if (vp == m)
                ret[i] = /*ret[i-1] +*/ deltap;
//            else if (vn == m)
//                ret[i] = /*ret[i-1] +*/ deltan;
            else
                ret[i] = /*ret[i-1]*/ 0;
        }
        
        return ret;
    }

    private static int[] reportResults(String name, int[] detected, int[] annotated) {
        
        
        System.out.println(name + " - Detected Heart Pulses = " + detected.length);
        System.out.println(name + " - Number of annotations = " +  annotated.length);
        
                
        int fp = 0;
        int fn = 0;
        
        int error;
        
        int ta = annotated.length;
        int td = detected.length;
        
        int ai=0;
        int di = 0;
        
        while ((ai < ta) && (di < td))
        {
            error = Math.abs(detected[di] - annotated[ai]);

            if (error > tolaratedError)
            {
                if (detected[di] > annotated[ai])
                {
                    // missed an annotated beat, meaning that there was an annotated beat
                    // that was not present in the detected ones
                    fn++;
                    ai++;
                }
                else
                {
                    // missed a detected beat
                    fp++;
                    di++;
                }
            }                    
            else
            {
                ai++;
                di++;
            }
        }

        fn += ta -ai;
        fp += td - di;
        
        int tot = fp + fn;
        
        System.out.println("ERRORS: " + tot + " FP:" + fp + " FN:" + fn);
        
        
        return new int[]{ tot, fp, fn};
    }
    
    /**
     * 
     * @param detected
     * @param annotated
     * @param bFP first positive
     * @param skipped
     * @param debug
     * @return 
     */
    private static int findFirstError(int[] detected, int[] annotated, boolean bFP, int skipped, boolean debug)
    {        
        int id = 0;
        int ia = 0;
        
        while (id < detected.length && ia < annotated.length)
        {
            int error = Math.abs(detected[id] - annotated[ia]);
            
            if (error > tolaratedError)
            {
                if (detected[id] < annotated[ia])
                {
                    if (debug) System.err.println("FALSE Positivie in " + (detected[id]+skipped)  + " (next annotated in " +  (annotated[ia]+skipped) + ")");
                    
                    if (bFP)
                        return detected[id];
                    else
                        id++;
                }
                else
                {
                    if (debug) System.err.println("FALSE Negative in " + (annotated[ia]+skipped) + " (next detected in " + (detected[id]+skipped) + ")");

                    if (!bFP)
                        return annotated[ia];
                    else
                        ia++;
                    //return beats[i];
                }
            }
            else
            {
                id++;
                ia++;
            }
        }
        
        if (annotated.length > detected.length)
        {
            System.err.println("FALSE Negative in " + (annotated[detected.length]+skipped) + " (last)" );
            return annotated[detected.length];
        }
        
        return -1;
    }

    /**
     * 
     * @param set
     * @param chunk
     * @param read
     * @param beats
     * @return number of errors
     */
    private static int[] processM5(String set, double[] chunk0, double[] chunk1, int[] beats, boolean debug, boolean bFP, int skipped) 
    {
        int decimation = 1;

        
        
//       double[] lp = chunk0; // lp(chunk0, 5);
       // double[] derivated = notch(chunk0, 6*2);
        double[] derivated = null;
        
        double[] hpData2 = minMaxFilter(chunk0,  2, 2, SELECT_VALUE   );
        double[] range2 = minMaxFilter(chunk0,  2, 2, SELECT_RANGE   );
        
  
        double[] mi = hpData2;
        
         
        
         
        double[] squared =  reduceRange(hpData2, range2);
        

        
        double[] maxThreshold = new double[chunk0.length];
        double[] minThreshold = new double[chunk0.length];

//        double[] finald = squared; // limitRange(scale(squared, 3), 100, -100); // absolute(integrated); // limitRange(squared, 200, -200); // sum(sum(derivated, sub(hpData2, range)), hpData2);

//        lp = hpData2;
        
        double[] t2 = triangleHeight(squared, 15); // triangleDerivateHeight(derivated, 7); // integrateOverCount(trian); // positive(sub(spike, sub));

        
         
         double[] zeroPass = null; // zeroPass(derivated);
         
//         t2 = nonZero(t2);
         
         double[] ridges = ridges(t2);
         double[] valleys = valleys(t2);
         
         double[] density = null; // integrate(absolute(positive(derivate(absolute(squared)))), 15); // integrate(sum(detectNonZero(ridges), detectNonZero(valleys)), 15);
         double[] nonZeroMean = null; // integrate(absolute(derivate(lp)), 50);
         
       //double[] finald = filterPulses(ridges, valleys);//   div(filterPulses(ridges, valleys), density); // sum(pulses, derivate); // reduceRange(t2, scale(zeroPass, 0.3)); // lp2; //  ( integrate(lp, 5)); // square(t2); // reduceRange(t2, range);
       
//       finald = sum(finald, squared);
         
         double[] filteredRidges = mixRidgesValleys(ridges, valleys);
       
         int[] ret3 = detectRidgesAndValleys(filteredRidges, valleys, maxThreshold, minThreshold, 0.3, 1, 90, 700, .85);
         
//        int[] ret3 = posEdgeAdaptive(finald, maxThreshold, 0.2, 90, 700);
//        int[] ret3 = eitherEdgeCheckOtherMaxAdaptive(finald, maxThreshold, minThreshold, 0.3, 1, 90, 700);
//        int[] ret3 = pulseEdgeAdaptive(finald, maxThreshold, minThreshold, 
//                .4, .4,
//                22, .6,
//                90/decimation,      // min gap after pulse 
//                700/decimation,     // max gap after pulse
//                90/decimation,      // max positive pulse
//                60/decimation, false);

//        int[] ret3 = findMaxInWindow(finald, maxThreshold, 0.5, 90/decimation, 700/decimation);
                
        int[] errors = reportResults(set, ret3, beats);

        int error = findFirstError(ret3, beats, bFP, skipped, debug);
        
        
        
        if (debug)
        //if (error != -1 && debug)
        {
            int focus = error - 50;
            if (focus < 0)
                focus = 0;
         
            PlotDiscrete1D pdis = new PlotDiscrete1D("Ridges");
        
            
        pdis.setData(ridges);
        pdis.setData2(valleys);
//        pdis.setAnnotation(beats);
        pdis.setVisible(true);
        pdis.setZoom(300);
        pdis.setOffset(focus);
        pdis.drawValleys = false;
//        pdis.setDetected(ret3);
//        pdis.setData3(maxThreshold);
        pdis.setData3(t2);

        pdis = new PlotDiscrete1D("Valleys");
        
        pdis.setData(ridges);
        pdis.setData2(valleys);
//        pdis.setAnnotation(beats);
        pdis.setVisible(true);
        pdis.setZoom(300);
        pdis.setOffset(focus);
        pdis.drawData = false;
        
        
        
        pdis = new PlotDiscrete1D("Filtered Ridges");
        
        pdis.setData(filteredRidges);
        pdis.setData2(valleys);
        pdis.setAnnotation(beats);
        pdis.setVisible(true);
        pdis.setZoom(300);
        pdis.setOffset(focus);
        pdis.drawData = true;
        pdis.drawValleys = false;
        //pdis.setData3(t2);
        pdis.setData3(maxThreshold);
	pdis.setDetected(ret3);
	
//        pdis.setDetected(ret3);
//        pdis.setData3(maxThreshold);

            Plot1D[] plots = plotSignals(set, new double[][]{
   //             finald,
                ridges,
                valleys,
                nonZeroMean,
                density,
                 t2, 
                derivated,
                squared,
                range2,
                hpData2, 
                
                chunk0},
                      new String[]{
     //                     "Final",
                          "ridges",
                          "valleys",
                          " Integrated absolute derivate 45",
                          " Integrated absolute derivate 15",
                          "t2", 
                          "derivated",
                          "Squared",
                          "Range",
                          "HP",
                          
                          "Source"  }, 
                      beats, 
                      focus, skipped);
              
            plots[0].setData2(maxThreshold);          
            plots[0].setData3(minThreshold);  
            
            
        }
        
        return  errors;
        }
        
        


    /**
     * Test Integrate over count
     * @param set
     * @param chunk0
     * @param chunk1
     * @param beats
     * @param debug
     * @param bFP
     * @param skipped
     * @return 
     */
    private static int[] processM6(String set, double[] chunk0, double[] chunk1, int[] beats, boolean debug, boolean bFP, int skipped) 
    {
        int decimation = 1;
        
        double[] hpData2 = minMaxFilter(chunk0,  2, 2, SELECT_MEAN | METHOD_JUMP   );
  
        double[] lp =  lp(hpData2, 3);
        double[] derivated = derivate(lp);
        
        
        double[] integrate = integrateOverCount(derivated);
        

        double[] maxThreshold = new double[chunk0.length];
        double[] minThreshold = new double[chunk0.length];

//        int[] ret3 = posEdgeAdaptive(integrate, maxThreshold, 0.4, 90, 500);
        int[] ret3 = pulseEdgeAdaptive(integrate, maxThreshold, minThreshold, 
                .5, .5,
                22, .6,
                90/decimation,      // min gap after pulse 
                600/decimation,     // max gap after pulse
                50/decimation,      // max positive pulse
                60/decimation, false);

                
        int[] errors = reportResults(set, ret3, beats);

        int error = findFirstError(ret3, beats, bFP, skipped, debug);
        
        if (error != -1 && debug)
        {
            int focus = error - 50;
            if (focus < 0)
                focus = 0;
            
            Plot1D[] plots = plotSignals(set, new double[][]{integrate, 
                derivated,
                lp,
                chunk0},
                      new String[]{"Integrate",
                          "Derivated",
                          "LP",
                          "Source"  }, 
                      beats, focus, skipped);
              
            plots[0].setData2(maxThreshold);          
//            plots[0].setData3(minThreshold);  
            
            
        }
        
        return  errors;

    }
    
    /**
     * Test Integrate over count
     * @param set
     * @param chunk0
     * @param chunk1
     * @param beats
     * @param debug
     * @param bFP
     * @param skipped
     * @return 
     */
    private static int[] processM1(String set, double[] chunk0, double[] chunk1, int[] beats, boolean debug, boolean bFP, int skipped) 
    {
        int decimation = 1;
        
        double[] hpData2 = minMaxFilter(chunk0,  2, 2, SELECT_VALUE   );
        double[] range = minMaxFilter(chunk0,  2, 2, SELECT_MAX_MINUS_MEAN);
  
        double[] median = median(hpData2, 2);
        
        
        double[] squared = sub(hpData2, range);
        
        double[] derivated = derivate(squared);
        
        double[] integrated = integrate(positive(derivated), 15);
        
//        double[] sq = square(hpData2);
        
//        double[] trian = triangleHeight(hpData2, 15);
//        
//        double[] sub = sub(range, derivated);

        double[] maxThreshold = new double[chunk0.length];
        double[] minThreshold = new double[chunk0.length];

        double[] finald = hpData2; // sub(abs, medianRange);

//        double[] finald = triangleHeight(sub(hpData2, range),15); // integrateOverCount(trian); // positive(sub(spike, sub));
        
        int[] ret3 = posEdgeAdaptive(finald, maxThreshold, 0.4, 90, 700);
//        int[] ret3 = pulseEdgeAdaptive(finald, maxThreshold, minThreshold, 
//                .4, .4,
//                22, .6,
//                90/decimation,      // min gap after pulse 
//                700/decimation,     // max gap after pulse
//                90/decimation,      // max positive pulse
//                60/decimation, false);

//        int[] ret3 = findMaxInWindow(finald, maxThreshold, 0.5, 90/decimation, 700/decimation);
                
        int[] errors = reportResults(set, ret3, beats);

        int error = findFirstError(ret3, beats, bFP, skipped, debug);
        
        if (error != -1 && debug)
        {
            int focus = error - 50;
            if (focus < 0)
                focus = 0;
            
            Plot1D[] plots = plotSignals(set, new double[][]{
                finald,
                integrated,
//                sub,
                
                squared,
                derivated,
                median,
                hpData2, 
                chunk0},
                      new String[]{
                          "Final",
                          "spike",
//                          "median - range",                          
                          "Squared",
                          "Derivated", 
                          "median",
                          "HP",
                          "Source"  }, 
                      beats, focus, skipped);
              
            plots[0].setData2(maxThreshold);          
            plots[0].setData3(range);  
            
            
        }
        
        return  errors;

    }
    
    /**
     * Test Correlation
     * @param set
     * @param chunk0
     * @param chunk1
     * @param beats
     * @param debug
     * @param bFP
     * @param skipped
     * @return 
     */
    private static int[] processM7(String set, double[] chunk0, double[] chunk1, int[] beats, boolean debug, boolean bFP, int skipped) 
    {
        
//        double[] hpData = minMaxFilter(dec2, read, 4, 1, SELECT_RANGE | METHOD_JUMP);
//        double[] dec1 = decimate(chunk0);
//        double[] dec2 = decimate(dec1);
//        double[] dec3 = decimate(dec2);
//        double[] avg = decimate(decimate(chunk0));
        
        int decimation = 1;
        
        
//         double[] hpData = minMaxFilter(chunk0,  2, 2, SELECT_MEAN_OR_VALUE   );
//         double[] hpData2 = derivate(hpData);
        double[] hpData2 = minMaxFilter(chunk0,  2, 2, SELECT_VALUE   );
        //double[] hpData2 = minMaxFilter(chunk0,  2, 4, SELECT_VALUE_MINUS_MIN | METHOD_JUMP);
        
//        double[] hpData1 = minMaxFilter(chunk1,  6, 3, SELECT_VALUE );
//        double[] sum = sum(hpData, hpData1);
        
        double[] maxThreshold = new double[chunk0.length];
        double[] minThreshold = new double[chunk0.length];
        
       // double[] linear = linearize(hpData2);

//                double[] absd = raiseNeg(hpData2);

//        double[] match = match(abs);
//        double[] kernel = new double[]{-100, 0, 100, 150, 200, 250, 300, 250, 200, 150, 100, 0, -25, -50, -75, -100};
//        double[] kernel2 = new double[]{35.0,58.0,79.0,108.0,138.0,163.0,173.0,166.0,139.0,92.0,39.0,-15.0,-70.0,-119.0,-158.0,-181.0,-179.0,-160.0,-134.0,-107.0,-81.0,-61.0,-42.0,-26.0,-9.0,6.0,17.0,19.0,18.0,17.0};

        // ideal for 100
//        double[] kernel2 =new double[]{-1.0,-3.0,-9.0,-12.0,-12.0,-17.0,-15.0,-7.0,13.0,38.0,74.0,110.0,155.0,209.0,256.0,291.0,299.0,279.0,216.0,120.0,25.0,-25.0,-33.0,-20.0,-10.0,-1.0,-5.0,-9.0,-10.0,-8.0};

        // ideal for 116
//        double[] kernel2 =new double[]{-1.0,2.0,6.0,4.0,8.0,13.0,23.0,38.0,58.0,82.0,109.0,136.0,164.0,203.0,240.0,274.0,298.0,300.0,271.0,211.0,117.0,10.0,-85.0,-152.0,-179.0,-168.0,-132.0,-86.0,-48.0,-23.0};

        // ideal for 113
        double[] kernel2 = new double[]{370.0,286.0,193.0,104.0,39.0};

//double[] square = square(hpData2);
//        double[] integrate = positive(hpData2);
        double[] integrate2 = SSE(hpData2, kernel2);
        
        double[] kernel3 = new double[]{56.0,49.0,42.0,38.0,33.0,26.0,20.0,10.0,-2.0,-10.0,-22.0,-30.0,-39.0,-47.0};

        
        double[] integrate3 = SSE(hpData2, kernel3);
        
        double[] kernel4 = new double[]{82.0,77.0,72.0,64.0,58.0,45.0,33.0,22.0,8.0,-3.0,-9.0,-20.0,-28.0,-35.0};
        
        double[] integrate4 = SSE(hpData2, kernel4);
        
        //int[] integrateKernel =  new int[]{-30, -20,  20, 30};
//        int[] integrateKernel =  new int[]{-50, -30,  30, 50};
          
//        double[] pos = integrate;
//        double[] integrate2 = sumOfSomeDifferences(pos, integrateKernel);
                
//        double[] avg2 = decimate(hpData2);
        double[] positive2 = positive(integrate2); //sumOfSomeDifferences(integrate2, new int[]{-10, 10});

        double[] positive3 = positive(integrate3); 
        double[] positive4 = positive(integrate4);
        
        double[] neg = sub(positive2, positive3); // scale(derivate, -1); 
//        
//        decimation *= 2;
//        beats = scale(beats, 1.0/decimation);
        
//        double[] integrate = integrate(square, 15/decimation);
        
        double[] finalD = neg; // derivate(integrate);
        
        //int[] ret3 = posEdgeAdaptive(finalD, maxThreshold, 0.2, 90, 500);
        int[] ret3 = pulseEdgeAdaptive(finalD, maxThreshold, minThreshold, 
                .4, .3,
                -2, .6,
                90/decimation,      // min gap after pulse 
                600/decimation,     // max gap after pulse
                20/decimation, // max positive pulse
                60/decimation, false);

                
        int[] errors = reportResults(set, ret3, beats);

        int error = findFirstError(ret3, beats, bFP, skipped, debug);
        
        if (error != -1 && debug)
        {
            int focus = error - 50;
            if (focus < 0)
                focus = 0;
            
            Plot1D[] plots = plotSignals(set, new double[][]{neg, 
                positive2, 
                positive3, 
                positive4, 
//                square, //derivate, 
//                avg2, avg, 
                chunk0},
                      new String[]{"Final",
                               "Positive2",
                          "Positive3",
                          "Positive4",
//                          "Square",// "Derivate", 
//                          "Filtered",   "Avg", 
                          "Source"  }, 
                      beats, focus, skipped);
              
            plots[0].setData2(maxThreshold);          
//            plots[0].setData3(minThreshold);  
            
            System.out.print("Ideal kernel: new double[]{" );
            String sLink = "";
            for (int i=0; i < kernel3.length; i++)
            {
                int k = (error-((kernel3.length/2)+i));
                
                if (k >= hpData2.length)
                    continue;
                
                System.out.print(sLink + hpData2[k]);
                sLink = ",";
            }
            
            System.out.println("};");
            
        }
        
        return  errors;
    }
    

    /**
     * Test Morphological filters
     * @param set
     * @param chunk0
     * @param chunk1
     * @param beats
     * @param debug
     * @param bFP
     * @param skipped
     * @return 
     */
    private static int[] processM8(String set, double[] chunk0, double[] chunk1, int[] beats, boolean debug, boolean bFP, int skipped)  
    {
     //            
                  int decimation =1;
//          double[] dec = decimateMax(chunk0, decimation);
//          beats = scale(beats, 1.0/decimation);

                  
//          double[] dec2 = subsample(chunk0, decimation);
          
          double[] hp1 = minMaxFilter(chunk0,  2, 2, SELECT_VALUE   );
//          double[] hp1 = derivate(chunk0);
//           double[] lp =  lp(hp1, 16);
           
//        double[] lp = integrate(absolute(hp1), 15);
          double[] lp = dilate(hp1 , new double[]{0, 10, 20, 10, 0});
          
          
          double[] kernel1 = new double[]{10, 10, 10, 10,  20,  10, 10, 10, 10};
          
//          double[] kernel1 = new double[]{ 50, 100, 200, 100 , 50};
          
          double[] close1 = close(open(hp1, kernel1), kernel1); // dilate + erode          
          double[] open1 = open(close(hp1, kernel1), kernel1); // dilate + erode
          
          double[] temp = sum(close1, open1);
          
          double[] close2 = close(open(temp, kernel1), kernel1); // dilate + erode          
          double[] open2 = open(close(temp, kernel1), kernel1); // dilate + erode
          
          double[] temp2 = sum(close2, open2);
          
          double[]  integrated = integrate(temp2, 15);
          
          double[] finald = zeroPass(integrated);
          //double[] lp2 = open(hp1 , 1);

//        double[] lin = linearize(lp);
        
          //double[] lp =  lp(hp1, 4);
          
//        double[] hp2 = minMaxFilter(sig,  2, 2, SELECT_VALUE   );
        
/*          double[] dec = decimateMaxMin(hp);
          double[] dec2 = decimateMaxMin(dec);
          double[] dec3 = decimateMaxMin(dec2);
          double[] dec4 = decimateMaxMin(dec3);
          
*/
//          double[] deriv = lp(derivate(hp1), 5);
          
          //double[] sig = normalizedWindowSAD(lp, new double[]{0, 0, 0, 0, 0, -1, -.9, -.5, -.2, -.1, .2, .5, .8, .9, 1, 1, .9, .8, .7,  .5, .2, -.5, -.8, -.9, -1, 0, 0, 0 , 0, 0, 0});

//          double[] sig = close; // derivate(lp(close,2)); // square(close);
          
//          double[] hp3 = hp(hp2, 10);
//          double[] hp4 = hpmin(hp3, 10);
          
        
//          double[] abs = zeroPass(sig);
          
//          double[] sig = integrate(abs, 15); // integrateOverCount(abs);
//          double[] sig = sumOfSomeDifferences(hp1, new int[]{-20, -10, 10, 20});
                  
          double[] maxThreshold = new double[chunk0.length];
        double[] minThreshold = new double[chunk0.length];

        
//        int[] ret3 = posEdgeAdaptive(sig, maxThreshold, 0.1, 90/decimation, 700/decimation);
        int[] ret3 = eitherEdgeCheckOtherMaxAdaptive(finald, maxThreshold, minThreshold, 0.4, 1, 90, 700);
//          int[] ret3 = pulseEdgeAdaptive(finald, maxThreshold, minThreshold, 
//                .4, .4,
//                .8, .5,
//                60/decimation,  // gap after beat
//                700/decimation, 
//                90/decimation, // max positive pulse
//                100/decimation, false);

                
        int[] errors = reportResults(set, ret3, beats);

        int error = findFirstError(ret3, beats, bFP, skipped, debug);
        
        if (error != -1 && debug)
        {
            int focus = error - 50;
            if (focus < 0)
                focus = 0;
          
                      Plot1D[] plots = plotSignals(set, new double[][]{
                          finald, 
                          integrated,
//                          dec, dec2, 
                          //                          sig, 
//                          dec, dec2, 
                          //                          sig, 
//                          dec, dec2, 
                          //                          sig, 
//                          dec, dec2, 
                          hp1,
                          lp, 
                          open1,
                          close1,
                          chunk0},
                      new String[]{
                          "Final", 
                          "Integrated",
//                          "dec", "dec2", 
                          "high pass",
                          "low pass",
//                          "dilate kernel",
//                          "erode kernel",
                          "open",
                          "close",
                          "source"}, 
                      beats, focus, skipped);
                      
                      
                      System.out.print("Ideal kernel: new double[]{" );
            String sLink = "";
//            for (int i=0; i < 30; i++)
//            {
//                int k = (error-15+i);
//                
//                if (k >= deriv.length)
//                    continue;
//                
//                System.out.print(sLink + deriv[k]);
//                sLink = ",";
//            }
            System.out.println("}");
            
                      plots[0].setData2(maxThreshold);
                      plots[0].setData3(minThreshold);
        }
        
        return errors;
    }

    private static double[] decimate(double[] chunk) 
    {
        int read = chunk.length;
        double[] ret = new double[read/2];
        
        for (int i=0; i < ret.length; i++)
        {
            //ret[i] = chunk[i*2];
            
            //if (ret[i] < chunk[i*2+1])
            //    ret[i] = chunk[i*2+1];
            
            ret[i] = (chunk[i*2] + chunk[i*2+1])/2;
        }
        
        return ret;
    }

    private static double[] scale(double[] beats, double scale) 
    {
        double[] ret = new double[beats.length];
        
        for(int i=0; i< beats.length; i++)
            ret[i] = (beats[i] * scale);
        
        return ret;
    }
    
    private static double[] limitRange(double[] beats, double max, double min) 
    {
        double[] ret = new double[beats.length];
        
        for(int i=0; i< beats.length; i++)
        {
            ret[i] = (beats[i] >  max) ? max :  (beats[i]<min)? min : beats[i];
        }
        
        return ret;
    }
    
    private static int[] scale(int[] beats, double scale) 
    {
        int[] ret = new int[beats.length];
        
        for(int i=0; i< beats.length; i++)
            ret[i] = (int) (beats[i] * scale);
        
        return ret;
    }

    public static void testSingle(int v, int skip, boolean bFP, int method) throws MalformedURLException, IOException {
                   
        
        
        String set = "" + v;
            
            DatReader dr0 = new DatReader(new File(dir, set + ".dat"), 0);
            DatReader dr1 = new DatReader(new File(dir, set + ".dat"), 1);
            AtrReader ar = new AtrReader(new File(dir, set + ".atr"), 0);
            //dr.skip(1195);
            
            double[] chunk0 = new double[1000000];
            double[] chunk1 = new double[1000000];
//              double[] chunk = new double[1000];
            
            dr0.skip(skip);
            dr1.skip(skip);
            
            int read0  = dr0.get(chunk0);
            int read1 = dr1.get(chunk1);
            int[] beats = ar.getBeatsAfter(skip);
            
            
            
            double[] nc0 = new double[read0];
            double[] nc1 = new double[read1];
            
            System.arraycopy(chunk0, 0, nc0, 0, read0);
            System.arraycopy(chunk1, 0, nc1, 0, read1);
            
            int[] errors;
            
            switch (method)
            {
                case 1: errors = processM1(set, nc0, nc1, beats, true, bFP, skip); break;
                case 2: errors = processM2(set, nc0, nc1, beats, true, bFP, skip); break;
                case 3: errors = processM3(set, nc0, nc1, beats, true, bFP, skip); break;
                case 4: errors = processM4(set, nc0, nc1, beats, true, bFP, skip); break;
                case 5: errors = processM5(set, nc0, nc1, beats, true, bFP, skip); break;
                case 8: errors = processM8(set, nc0, nc1, beats, true, bFP, skip); break;
                default:
                    throw new RuntimeException("Unknown method " + method);
            }

            
//            if (errors[0] == 0)
//                System.exit(0);

    }
    
    public static void testChenSubset() throws IOException
    {
        testSet(new int[]{100, 101, 103, 109, 113, 114, 115, 116, 117, 119, 122, 123, 124 , 201, 202, 205, 209, 213,219,220,  221, 222, 230, 231, 234});
    }
    
    public static void testWholeSet() throws IOException {
        testSet(new int[]{100, 101, 102, 103, 104, 105, 106, 107, 108 , 109,
            111, 112, 113, 114, 115, 116, 117, 118, 119, 
            121, 122, 123, 124 , 
            200, 201, 202, 203, 205, 207, 208, 209, 
            210, 212, 213, 214, 215, 217, 219,
            220, 221, 222, 223, 228, 
            230, 231, 232, 233, 234});
    }
    private static void testSet(int[] sets) throws MalformedURLException , IOException
    {
        int[][] results = new int[sets.length][];
        int[] totBeats = new int[sets.length];
        
        for (int i=0; i < sets.length; i++)
        {
                    String set = "" + sets[i];
            
            DatReader dr0 = new DatReader(new File(dir, set + ".dat"), 0);
            DatReader dr1 = new DatReader(new File(dir, set + ".dat"), 1);
            AtrReader ar = new AtrReader(new File(dir, set + ".atr"), 0);
            //dr.skip(1195);
            
            double[] chunk0 = new double[1000000];
            double[] chunk1 = new double[1000000];
//              double[] chunk = new double[1000];
            
            int read0  = dr0.get(chunk0);
            int read1  = dr1.get(chunk1);
            int[] beats = ar.getBeats();
            
             double[] nc0 = new double[read0];
            double[] nc1 = new double[read1];
            
            System.arraycopy(chunk0, 0, nc0, 0, read0);
            System.arraycopy(chunk1, 0, nc1, 0, read1);
            
            results[i] = processM5(set, nc0, nc1, beats, false, true, 0);
            //results[i] = processM5(set, chunk, read, beats);
            totBeats[i] = beats.length;
        }
        
        System.err.println("");
        System.out.println("");
        
        int total= 0;
        int tfp = 0;
        int tfn = 0;
        double TQRS = 0; // total pulses

        // Print in CSV format
        System.out.println("Set;Beats;Errors;FP;FN;");

        for (int i=0; i < results.length; i++)
        {
            total += results[i][0];
            tfp += results[i][1];
            tfn += results[i][2];
            System.out.println("" + sets[i] + ";" + totBeats[i] + ";"   + results[i][0] + ";" + results[i][1] + ";" + results[i][2]);
            
            TQRS += totBeats[i];
        }
        
        double TP = TQRS - tfn;
        double Se = TP / (TP + tfn);
        double pP = TP / (TP + tfp);
        double DER = (tfn + tfp) / TQRS;
        System.out.println("TOTAL; " + TQRS + ";" + tfp + ";" + tfn + ";");
        System.out.println(";Se%;" + (Se) + "+P%;" + (pP) + ";DER%; " + (DER));
    }

    /**
     * Sum of absolute differences
     * @param in
     * @param kernel
     * @return 
     */
    private static double[] SAD(double[] in, double[] kernel) {
        double[] ret = new double[in.length];
        
        int kl = kernel.length;
        double integrateKernel = 0;
    
        for (double v : kernel) integrateKernel += 150;
       
        for (int i=0; i < ret.length; i++)
        {
            double sum = 0;
            
            for (int j=0; j < kl; j++)
            {
                int k = i + j-(kl/2);
                
                if (k >= 0 && k < in.length)
                    sum += Math.abs(in[k] - kernel[j]);
                else
                    if (k >= 0)
                        sum += Math.abs(in[in.length-1] - kernel[j]);
                    else
                        sum += Math.abs(in[0] - kernel[j]);

                        
            }
            
            ret[i] = -( integrateKernel - sum);
        }
        
        return ret;
    }
    
    private static double[] SSE(double[] in, double[] kernel) {
        double[] ret = new double[in.length];
        
        int kl = kernel.length;
        double integrateKernel = 0;
    
        for (double v : kernel) integrateKernel += Math.pow(v, 2);
       
        for (int i=0; i < ret.length; i++)
        {
            double sum = 0;
            
            for (int j=0; j < kl; j++)
            {
                int k = i + j-(kl/2);
                
                if (k >= 0 && k < in.length)
                    sum += Math.pow(in[k] - kernel[j], 2);
                else
                    if (k >= 0)
                        sum += Math.pow(in[in.length-1] - kernel[j], 2);
                    else
                        sum += Math.pow(in[0] - kernel[j], 2);

                        
            }
            
            // sum is minimum when a match occurs
            ret[i] = (integrateKernel - sum);
        }
        
        return ret;
    }
    
    
    private static double[] normalizedWindowSAD(double[] in, double[] kernel) {
        double[] ret = new double[in.length];
        
        int kl = kernel.length;
        double integrateKernel = 0;
//        for (double v : kernel) integrateKernel += v;
       
        for (int i=0; i < ret.length; i++)
        {            
            double[] window = getWindow(in, i, kl); 
            double windowMax = getMaxInRange(window, 0, window.length);
            normalize(window);
            
            ret[i] =  computeSAD(window, kernel) * windowMax;            
        }
        
        return ret;
    }
    
    private static double[] sumOfSomeDifferences(double[] in, int[] diff) {
        double[] ret = new double[in.length];
        
        int kl = diff.length;
        double integrateKernel = 0;
//        for (double v : kernel) integrateKernel += v;
       
        for (int i=0; i < ret.length; i++)
        {
            double sum = -integrateKernel;
            
            for (int j=0; j < kl; j++)
            {
                int k = i + diff[j];
                
                if (k >= 0 && k < in.length)
                    sum += in[i] - in[k];                       
            }
            
            ret[i] = sum;
        }
        
        return ret;
    }

    private static double getMaxInRange(double[] data, int i, int len) {
    
            double max = data[i];
            
            for (int j=i; (j < data.length) && (j < i + len) ; j++ )
                if (data[j] >  max) max = data[j];

            return max;
    }
    
    private static int getMaxIndexInRange(double[] data, int i, int len) {
    
            double max = data[i];
            int index = i;
            
            for (int j=i; (j < data.length) && (j < i + len) ; j++ )
                if (data[j] >  max) 
                {
                    max = data[j];
                    index = j;
                }

            return index;
    }
    
    private static double getMinInRage(double[] data, int i, int len) {
    
            double min = data[i];
            
            for (int j=i; (j < data.length) && (j < i + len) ; j++ )
                if (data[j] <  min) min = data[j];

            return min;
    }

        private static int getMinIndexInRange(double[] data, int i, int len) {
    
            double min = data[i];
            int index = i;
            
            for (int j=i; (j < data.length) && (j < i + len) ; j++ )
                if (data[j] <  min) 
                {
                    min = data[j];
                    index = j;
                }

            return index;
    }

    private static double[] match(double[] data) {
        double[] ref = new double[]{0, 1, 0};
        double[] ret = new double[data.length];

        int m = 15;
        double k = 3;

        for (int i=m; i < data.length-m; i++)
        {
            double a = data[i-m];
            double b = data[i];
            double c = data[i+m];
            
            // 
            if ((a < b/k) && (a > -b/k) && (c < b/k) && (c > -b/k) && (b > 0))
                ret[i] = b  - a - c;
            
            if ((a > b/k) && (a < -b/k) && (c > b/k) && (c < -b/k) && (b < 0))
                ret[i] = -b + a + c;
        }
        
        m = 20;
        /*
        for (int i=m; i < data.length-m; i++)
        {
            double a = data[i-m];
            double b = data[i];
            double c = data[i+m];
            
            // 
            if ((a < b/k) && (a > -b/k) && (c < b/k) && (c > -b/k) && (b > 0))
                ret[i] += b; //  - a - c;
            
            if ((a > b/k) && (a < -b/k) && (c > b/k) && (c < -b/k) && (b < 0))
                ret[i] += -b; //  + a + c;
        }*/
        
        return ret;
    }

    private static double[] sum(double[] a, double[] b) 
    {
        int n = a.length;
        
        double[] ret = new double[n];
        
        for (int i=0; i < n; i++)
        {
            ret[i] = a[i] + b[i];
        }
        
        return ret;
    }
    
    private static double[] sub(double[] a, double[] b) 
    {
        int n = a.length;
        
        double[] ret = new double[n];
        
        for (int i=0; i < n; i++)
        {
            ret[i] = a[i] - b[i];
        }
        
        return ret;
    }
    
    /**
     * Assume b is positive
     * @param a
     * @param b
     * @return 
     */
    private static double[] reduceRange(double[] a, double[] b) 
    {
        int n = a.length;
        
        double[] ret = new double[n];
        
        for (int i=0; i < n; i++)
        {
            ret[i] = (a[i] > 0)? Math.max(a[i] - b[i], 0) : Math.min(a[i] + b[i], 0);
        }
        
        return ret;
    }
    
    private static double[] notch(double[] in, int n) 
    {
        int len = in.length;
        
        double[] ret = new double[len];
        
        for (int i=0; i < len; i++)
        {
            if (i > n)
            ret[i] = in[i]-in[i-n];
            else
                ret[i] = in[i] - in[0];
        }
        
        return ret;
    }

    private static double[] mult(double[] a, double[] b) {
        double[] ret = new double[a.length];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = a[i] * b[i];
        }
        
        return ret;
    }
    
    
    private static double[] div(double[] a, double[] b) {
        double[] ret = new double[a.length];
        
        for (int i=0; i < ret.length; i++)
        {
            if (b[i] == 0)
                ret[i] = a[i];
            else
                ret[i] = a[i] / b[i];
        }
        
        return ret;
    }


    /**
     * Plot a set of arrays
     * @param set
     * @param data
     * @param title
     * @param focus
     * @return 
     */
    private static Plot1D[] plotSignals(String set, double[][] data, String[] title, int[] beats, int focus, int skipped)
    {
        if (data == null)
            return null;
        
        Plot1D[] ret = new Plot1D[data.length];
        
        for (int i=data.length-1; i >= 0; i--)
        {
            if (data[i] == null)
                continue;
            
            Plot1D plot;
            ret[i] =  plot = new Plot1D(set + title[i]);
                        
            plot.setData(data[i], data[i].length);
            plot.setZoom(300);
            plot.setOffset(focus);
            plot.setSkipped(skipped);
            plot.setAnnotation(beats);
            plot.setVisible(true);
        }
        
        return ret;
    }

    private static double[] absolute(double[] a) {
        int n = a.length;
        
        double[] ret = new double[n];
        
        for (int i=0; i < n; i++)
        {
            ret[i] = Math.abs(a[i]);
        }
        
        return ret;
    }
    
    private static double[] square(double[] a) {
        int n = a.length;
        
        double[] ret = new double[n];
        
        for (int i=0; i < n; i++)
        {
            ret[i] = a[i]*a[i];
        }
        
        return ret;
    }
    
    private static double[] positive(double[] a) {
        int n = a.length;
        
        double[] ret = new double[n];
        
        for (int i=0; i < n; i++)
        {
            ret[i] = (a[i] > 0)?a[i]:0;
        }
        
        return ret;
    }

    private static double[] raiseNeg(double[] a) {
        
        int n = a.length;
        
        double[] ret = new double[n];
        
        double last = 0;
        
        for (int i=0; i < n; i++)
        {
            
            if (a[i] < 0)
            {
                ret[i] = 0;
                last /= 2;
                last += a[i];
            }
            else
            {
                ret[i] = a[i] - last;
                last = 0;
            }
        }
        
        return ret;
    }

    private static double[] decimateMaxMin(double[] chunk)
    {
        return decimateMax(chunk, 2);
    }
    
    private static double[] decimateMax(double[] chunk, int factor)
    {
        int read = chunk.length;
        double[] ret = new double[read/factor];
        
        for (int i=0; i < ret.length; i++)
        {
            //ret[i] = chunk[i*2];
            double max = chunk[i*factor];
            
            //if (ret[i] < chunk[i*2+1])
            //    ret[i] = chunk[i*2+1];
            for (int k=1; (k<factor) && (((i*factor)+k) < ret.length); k++)
                if (max > chunk[(i*factor)+k]) max = chunk[(i*factor)+k];
                
            ret[i] = max;
        }
        
        return ret;    
    }
    
    

    private static double[] sigmaIntegrator(double[] data, int maxWidth) 
    {
        double[] ret = new double[data.length];
        double slope = 0;
        int count = 0;
        int state = 0;
        double sum = 0;
        
        for (int i=1; i < data.length; i++)
        {
            slope = data[i] - data[i-1];
            
            
            if (state == 0)
            {
                if (slope > 0)
                {
                    // raising
                    state = 1;
                    count = 0;
                    sum = data[i];
                }
            }
            else if (state == 1)
            {
                count++;
            
                sum += data[i];
                
                if (slope < 0)
                {
                    state = 2;
                }
            }
            else if (state == 2)
            {                
                if (slope > 0)
                {
                    state = 0;
                    
                    if (count < maxWidth)
                        ret[i-(count/2)] = sum;
                }
                else
                    sum -= data[i];
            
            }
        }
        
        return ret;
    }

    private static double[] hpmin(double[] in, int f) 
    {
        double[] ret = new double[in.length];
        
        for (int i=0; i < in.length; i++)
        {
            double mean = 0;
            double min = in[0];
            int count = 0;
            
            for (int j=-f; j <=f; j++)
            {
                int k = i+j;
                
                if (k >=0 && k < in.length)
                {
                    mean += in[k];
                    count++;

                    if (in[k] < min)
                        min = in[k];
                }
                
            }
            
            double v = mean / count;
            
            //ret[i] = in[i] - v;
            ret[i] = in[i] - min;
        }
        
        return ret;
    }
    
    private static double[] hp(double[] in, int f) 
    {
        double[] ret = new double[in.length];
        
        for (int i=0; i < in.length; i++)
        {
            double mean = 0;
            int count = 0;
            
            for (int j=-f; j <=f; j++)
            {
                int k = i+j;
                
                if (k >=0 && k < in.length)
                {
                    mean += in[k];
                    count++;
                }
                
            }
            
            double v = mean / count;
            
            ret[i] = in[i] - v;
            
        }
        
        return ret;
    }
     
    private static double[] lp(double[] in, int f) 
    {
        double[] ret = new double[in.length];
        
        for (int i=0; i < in.length; i++)
        {
            double mean = 0;
            int count = 0;
            
            for (int j=-f; j <=f; j++)
            {
                int k = i+j;
                
                if (k >=0 && k < in.length)
                {
                    mean += in[k];
                    count++;
                }
            }
            
            double v = mean / count;
            
            ret[i] = v;
        }
        
        return ret;
    }
    
    
    private static double[] median(double[] in, int f) 
    {
        double[] ret = new double[in.length];
        double[] buf = new double[f*2+1];
        
        for (int i=0; i < in.length; i++)
        {
            int count = 0;
            
            for (int j=-f; j <=f; j++)
            {
                int k = i+j;
                
                if (k >=0 && k < in.length)
                {
                    buf[count] = in[k];
                    count++;
                }
            }
            
            Arrays.sort(buf, 0, count-1);
            
            ret[i] = buf[count/2];
        }
        
        return ret;
    }
    
    private static double[] triangleHeight(double[] in, int f) 
    {
        double[] ret = new double[in.length];
        double[] buf = new double[f*2+1];
        
        for (int i=f; i < in.length-f; i++)
        {
            double x = in[i];
            double a = in[i-f];
            double b = in[i+f];
            double z = 0;
            
            if (x > 0) if (x > a && x > b) z = x - Math.max(a, b);
            if (x < 0) if (x < a && x < b) z = (x - Math.min(a, b));

            ret[i] = z;
        }
        
        return ret;
    }
    
    private static double[] triangleDerivateHeight(double[] in, int f) 
    {
        double[] ret = new double[in.length];
        double[] buf = new double[f*2+1];
        
        for (int i=f; i < in.length-f; i++)
        {
            double x = in[i];
            double a = in[i-f];
            double b = in[i+f];
            double z = 0;
            
            if (a > 0) if (a > x && x > b) z = a - b;
            if (a < 0) if (a < x && x < b) z = -(b - a);

            ret[i] = z;
        }
        
        return ret;
    }

    
    /**
     * @deprecated
     * @param in
     * @return 
     */
    private static double[] linearize(double[] in) {
        double[] ret = new double[in.length];
        
        ret[0] = in[0];
        ret[in.length-1] = in[in.length-1];
        
        for (int i=1; i < in.length-1; i++)
        {
            double a = in[i-1];
            double b = in[i];
            double c = in[i+1];
            double r = 0;
            
            if ((a < b) && (b < c)) r = b;
            else if ((a > b) && (b > c)) r = b;
            else if ((a < b) && (b > b)) r = (a+c)/2;
            else if ((a > b) && (b < b)) r = (a+c)/2;
            else r = (a + b + c)/3;
            
            ret[i] = r;
        }
        return ret;
    }

    private static double[] integrateOverCount(double[] in) {
        double[] ret = new double[in.length];
        
        double initCount = 1;
        double initSum = 0;
        double sum = initSum;
        double count = initCount;
        
        
        for (int i=1; i < in.length-1; i++)
        {
            if (in[i] > 0)
            {
                sum += in[i];
                count += 1;
            }
            else
            {
                count = initCount;
                sum = initSum;
            }
            
            ret[i] = sum/(count);
            
            /*if (ret[i] < ret[i-1])
            {
                ret[i] = 0;
                count = 1;
                sum = 0;
            }*/
        }
        return ret;
    }
    
    
    private static double[] integrate(double[] in, int f) 
    {
        double[] ret = new double[in.length];
        
        for (int i=0; i < in.length; i++)
        {
            double sum = 0;
          
            
            for (int j=-f; j <=f; j++)
            {
                int k = i+j;
                
                if (k >=0 && k < in.length)
                {
                    sum += in[k];    
                }
                else
                    sum += in[0];
            }
            
            ret[i] = sum;
        }
        
        return ret;
    }

    private static double[] getWindow(double[] in, int i, int kl) {
      
        double[] ret = new double[kl];
        
        for (int j=0; j < kl; j++)
            {
                int k = i + j-(kl/2);
                
                if (k < 0)
                    ret[j] = in[0];
                else if (k >= in.length)
                    ret[j] = in[in.length -1];
                else
                    ret[j] = in[k];
            }
        
        return ret;
    }

    private static void normalize(double[] window) {
        double max = 0;
        double min = 0;
        
        for (int i=0; i < window.length; i++)
        {
            if (window[i]>max) max = window[i];
            if (window[i]<min) min = window[i];
        }
        
        double range = Math.max(max, -min); // 
        double factor = 1 / range;
        
        for (int i=0; i < window.length; i++)
        {
            window[i] *= factor; 
        }
        
    }

    private static double computeSAD(double[] window, double[] kernel) 
    {
        double sum = 0;
  
        if (window.length != kernel.length)
            throw new RuntimeException("diff len");
        
        for (int i=0; i < window.length; i++)
            sum += Math.abs(window[i] - kernel[i]);
        
        return sum;
    }

    private static double[] subsample(double[] chunk0, int factor) 
    {
         int read = chunk0.length;
        double[] ret = new double[read/factor];
        
        for (int i=0; i < ret.length; i++)
        {
            //ret[i] = chunk[i*2];
            ret[i] = chunk0[i*factor];            
        }
        
        return ret;    
    }
    
    private static double[] sumOfClose(double[] in, int start, int stop)
    {
        double[] sum = new double[in.length];
        
        for (int i=start; i < stop; i++)
        {
            double[] ret = close(in, i);
            sum = sum(sum, ret);
        }
        
        return sum;
    }

    private static double[] close(double[] in, int factor) {
       
        int n = in.length;
        
        double[] ret = new double[n];
                
        for (int i=0; i < n; i++)
        {
            double x = in[i];
            double a = (i>=factor)? in[i-factor] : in[i];
            double b = (i<(n-factor))? in[i+factor] : in[i];
            
            if (x < a && x < b) x = Math.min(a, b);
            else if (x > a && x > b) a = Math.max(a, b);
            
            
            ret[i] = x;
            
        }
        
        return ret;
    
    }
    
    private static double[] open(double[] in, double[] kernel)
    {
        return dilate(erode(in, kernel), kernel);
    }

    

    private static double[] close(double[] in, double[] kernel)
    {
        return erode(dilate(in, kernel), kernel);
    }
        
    private static double[] erode(double[] in, double[] kernel) 
    {
        int n = in.length;
        int kn = kernel.length;
        
        double[] ret = new double[n];
                
        for (int i=0; i < n; i++)
        {
            double min = in[i];
            
            for (int k=0; k < kn; k++)
            {
                int j = i + k  - kn/2;
                
                if ((j < 0) || (j >= n))
                    continue;
                
                min = Math.min(in[j]-kernel[k], min);
            }            
            
            ret[i] = min;
        }
        
        return ret;
    
    }
    
    private static double[] dilate(double[] in, double[] kernel) 
    {
        int n = in.length;
        int kn = kernel.length;
        
        double[] ret = new double[n];
                
        for (int i=0; i < n; i++)
        {
            double max = in[i];
            
            for (int k=0; k < kn; k++)
            {
                int j = i + k  - kn/2;
                
                if ((j < 0) || (j >= n))
                    continue;
                
                max = Math.max(in[j]+kernel[k], max);
            }            
            
            ret[i] = max;
        }
        
        return ret;
    
    }
    
//    private static double[] close(double[] in, double[] ) {
//       
//        int n = in.length;
//        
//        double[] ret = new double[n];
//                
//        for (int i=0; i < n; i++)
//        {
//            double x = in[i];
//            double a = (i>=factor)? in[i-factor] : in[i];
//            double b = (i<(n-factor))? in[i+factor] : in[i];
//            
//            if (x < a && x < b) x = Math.min(a, b);
//            else if (x > a && x > b) a = Math.max(a, b);
//            
//            
//            ret[i] = x;
//            
//        }
//        
//        return ret;
//    
//    }
    
    private static double[] openAux(double[] in, int factor) {
       
        int n = in.length;
        
        double[] ret = new double[n];
                
        for (int i=0; i < n; i++)
        {
            double x = in[i];
            double a = (i>=factor)? in[i-factor] : in[i];
            double b = (i<(n-factor))? in[i+factor] : in[i];
            
            if (x < a && x < b) x = Math.min(a, b ) - x;
            else if (x > a && x > b) a = x - Math.max(a, b) ;
            else x = 0;
            
            
            ret[i] = x;
            
        }
        
        return ret;
    
    }

    private static double[] castellsFilter(double[] in, double[] kernel, double errorFactor) {
        double[] ret = new double[in.length];
        
        int kl = kernel.length;
        
       
        for (int i=0; i < ret.length; i++)
        {
            double sig = 0;
            double err = 0;
            
            for (int j=0; j < kl; j++)
            {
                int k = i + j-(kl/2);
                
                if (k >= 0 && k < in.length)
                {
                    if (kernel[j]>=0)
                        if (in[k] > kernel[j])
                            err += (in[k] - kernel[j]) * errorFactor;
                        else
                            err += (kernel[j] - in[k]) * errorFactor;
                    else
                        if (in[k] < kernel[j])
                            err += - (in[k] - kernel[j]) * errorFactor;
                        else
                            err += - (kernel[j] - in[k]) * errorFactor;

                    if ((kernel[j] > 0) && (in[k] > 0))
                        sig += Math.min(kernel[j], in[k]);
                    else if ((kernel[j] < 0) && (in[k] < 0))
                        sig += -Math.max(kernel[j], in[k]);
                }
            }
            
            // sum is minimum when a match occurs
            ret[i] = sig - err;
        }
        
        return ret;
    }
    
    private static double[] convolution(double[] in, double[] kernel) {
        double[] ret = new double[in.length];
        
        int kl = kernel.length;
        
       
        for (int i=0; i < ret.length; i++)
        {
            double sum = 0;
            
            for (int j=0; j < kl; j++)
            {
                int k = i + j-(kl/2);
                
                if (k >= 0 && k < in.length)
                    sum += in[k] * kernel[j];
                
                        
            }
            
            // sum is minimum when a match occurs
            ret[i] = sum;
        }
        
        return ret;
    }

    private static double[] subset(double[] in, int s, int len) {
        double[] ret = new double[len];
        
        for (int i=0; i < len; i++)
            ret[i] = in[s+i];
        
        return ret;
    }

    private static double[] filterPulses(double[] ridges, double[] valleys) {
        double[] ret = new double[ridges.length];
        
        int lastValley = 0;
        int lastRidge = 0;
        double lastValleyValue = 0;
        double lastRidgeValue = 0;
        
        int n = ridges.length;
        int radius = 25;
        double x;
        
        boolean noise = false;
        double noiseValue = 0;
        
        for (int i=1; i < ret.length; i++)
        {
            x = 0;
            double r = ridges[i];
            double v = valleys[i];
            
            int startIndex = (i-radius);
            
            if (i == 22280)
                i = i*1;
            
            if (r > 0) 
            {
//                if (noise)
//                {
//                    double nv = Math.abs(noiseValue);
//                    double aux = Math.abs(r);
//                    r = r-nv;
//                    noiseValue = (aux+nv)/2;
//                }
                
                if (lastRidge <=  startIndex || lastRidge == 0)
                {
                    // last ridge far away, so take this one
                    x = r;
                    noise = false;
                }
                else if ((lastRidge > startIndex) && (lastRidgeValue > r/2) && (lastRidgeValue < r*2) /*&& ((lastValley > startIndex && lastValleyValue < 0) )*/)
                {
                    // enter noise supression mode
//                    noise = true;
                    noiseValue = r;
                    
                    // this is probably noise, reset both ridges
                    r = 0;
                    x = r;
                    ret[lastRidge] = 0;
//                    ret[lastValley] = 0;
                    
                    
//                    System.err.println("noise at ridge " + i);
                }
                else if (lastRidge > startIndex && r > lastRidgeValue)
                {
                    // if this one is higher reset the last ridge, take this one
                    ret[lastRidge] = 0;
                    x = r;
                }
                else if (lastRidge > startIndex && r < lastRidgeValue)
                {
                    // if this one is lower take the last ridge, take this one
                    x = r = 0;
                }
                else
                    x = 0;
//                    if ((lastValleyValue < r / 2) && (lastValley > (i-radius)))
//                    // if last valley is high enough, ignore
//                    x = r;
            }
            else if (v < 0) 
            {
//                if (noise)
//                {               
//                    double nv = Math.abs(noiseValue);
//                    double aux = Math.abs(r);
//                    v = v+nv;
//                    noiseValue = (aux+nv)/2;
//                }   
                
                if (lastValley <=  startIndex || lastValley == 0)
                {
                    // last ridge far away, so take this one
                    x = v;
                    noise = false;
                }
                else if ((lastValley > startIndex) && (lastValleyValue > v/2) && (lastValleyValue < v*2) /*&& ((lastRidge > startIndex && lastRidgeValue < 0) )*/)
                {
//                    noise = true;
                    noiseValue = v;
                    
                    // this is probably noise, reset both ridges
                    v = 0;
                    x = v;
                    ret[lastValley] = 0;
//                    ret[lastRidge] = 0;
                    
                    
//                    System.err.println("noise at valley " + i);
                }
                else if (lastValley > startIndex && v < lastValleyValue)
                {
                    // if this one is higher reset the last ridge, take this one
                    ret[lastValley] = 0;
                    x = v;
                }
                else if (lastValley > startIndex && r > lastValleyValue)
                {
                    // if this one is lower take the last ridge, take this one
                    x = v = 0;
                }
                else
                    x = 0;
            }

            ret[i] = x;
            
            if (v != 0)
            {
                lastValley = i;
                lastValleyValue = v;
            }
            if (r != 0)
            {
                lastRidge = i;
                lastRidgeValue = r;
            }
        }
        
        return ret;
    }

    private static double[] nonZero(double[] in) {
        double[] ret = new double[in.length];
        
        
        
        for (int i=1; i < ret.length; i++)
        {
            double x = in[i];
            
            ret[i] = (x == 0)? 1: x;
            
        }
        
        return ret; }
    
    
    private static double[] detectNonZero(double[] in) {
        double[] ret = new double[in.length];
        
        
        
        for (int i=1; i < ret.length; i++)
        {
            double x = in[i];
            
            ret[i] = (x == 0)? 0: 1;
            
        }
        
        return ret; }

    /**
     * 
     * @param ridges
     * @param valleys
     * @param maxThreshold
     * @param minThreshold
     * @param posFactor
     * @param negFactor
     * @param minGap
     * @param maxGap
     * @return 
     */
    private static int[] detectRidgesAndValleys(double[] ridges, double[] valleys, double[] maxThreshold, 
                                                double[] minThreshold, double posFactor, 
                                                double negFactor, int minGap, int maxGap, double maxRateVariability) 
    {
        ArrayList<Integer> events = new ArrayList<Integer>();
        ArrayList<Double> beats = new ArrayList<Double>();
        ArrayList<Double> noise = new ArrayList<Double>();

        double max = getMaxInRange(ridges, 0, maxGap);
        
        double posThreshold = max *posFactor;
        double preNoiseThreshold = posThreshold;
        
        int gap = minGap;
        for (int i=0; i < ridges.length; i++)
        {
            double r = ridges[i];
            gap++;
            
            maxThreshold[i] = posThreshold;
            
            if (gap < minGap)
            {
                if (beats.size() > 0)
                {
                    double lastRidge = beats.get(beats.size()-1);
                
                    // no ridge can be detected during the gap
                    if (r > lastRidge)
                    {
                        // this is higher, take this one
                        beats.remove(beats.size()-1);
                        events.remove(events.size()-1);
                        
                        events.add(i);
                        ArrayUtils.addToLast(beats, r, 5);
                        gap = 0;
                        posThreshold = ArrayUtils.getMean(beats) * posFactor;
                        noise.clear();
                        preNoiseThreshold = posThreshold;
                    }
                    else if ((r > 0) && (gap > 40)) 
                    {
                        if (noise.size() == 0) preNoiseThreshold = posThreshold;
                        noise.add(r);
//                        posThreshold = ArrayUtils.getMax(noise);
                    }

                    continue;
                }
                else
                    continue;
            }
            
            if (gap > maxGap)
            {
                noise.clear();
                posThreshold = preNoiseThreshold;
            }
            
            if (r > posThreshold)
            {
                if ((events.size()>2) && (gap < (events.get(events.size()-1) - events.get(events.size()-2)) * (1-maxRateVariability)))
                {
                    // not in possible beat area, so noise
                    double prevHR = 360 *60 / (events.get(events.size()-1) - events.get(events.size()-2));
                    double curHR = 360 * 60/ gap;
                    System.err.println("Change from " + prevHR + " to " + curHR + " variability " + curHR/prevHR);
                    if (noise.size() == 0) preNoiseThreshold = posThreshold;
                    noise.add(r);
                    //posThreshold = ArrayUtils.getMax(noise);
                }   
                else 
                if ((noise.size() > 1) && (r < Math.min(preNoiseThreshold/posFactor-preNoiseThreshold, ArrayUtils.getMax(noise) + preNoiseThreshold)))
                {
                    // this is noise
                    if (noise.size() == 0) preNoiseThreshold = posThreshold;
                    noise.add(r);
                    //posThreshold = ArrayUtils.getMax(noise);
                }
                else
                {
                    // consider it as a new beat
                    events.add(i);
                    ArrayUtils.addToLast(beats, r, 5);

                    gap = 0;

                    // calculate the new threshold
                    posThreshold = ArrayUtils.getMean(beats) * posFactor;
                    noise.clear();  
                }
            }
            
            
        }
        
        int[] ret = new int[events.size()];
        
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = events.get(i);
        }
        
        return ret;

    }

    private static double[] mixRidgesValleys(double[] ridges, double[] valleys) {
        double[] ret = new double[ridges.length];
        
        
        for (int i=0; i < ret.length; i++)
        {
            double r = ridges[i];
            double v = valleys[i];
            
            if (r > 0) ret[i] = r;
            else if (v < 0) ret[i] = -v;
            else
                ret[i] = 0;
        }
        
        return ret;
    }

    

    

}
