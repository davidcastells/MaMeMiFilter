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

/**
 *
 * @author dcr
 */
public class SignalGenerator {
 
    static double[] greateSinSignal(double duration, double frequency, double samplingFreq, double amplitude, int offset)
    {
	int samples = (int) (duration * samplingFreq);
	int periodInSamples = (int) (samplingFreq / frequency);
	double t = (1.0/periodInSamples) * 2 * Math.PI;
	double[] ret = new double[samples];
	
	for (int i=0; i < samples; )
	{
	    ret[i++] = offset + amplitude * Math.sin(t*i);
	}
	
	return ret;
    }
    
    static double[] greateSquareSignal(double duration, double frequency, double samplingFreq, int amplitude, int offset)
    {
	int samples = (int) (duration * samplingFreq);
	int period = (int) (samplingFreq / frequency);
	int halfPeriod = period/2;
	
	double[] ret = new double[samples];
	
	for (int i=0; i < samples; )
	{
	    for (int j=0; j < halfPeriod; j++)
	    {
		ret[i++] = offset + amplitude;
	    }
	    
	    for (int j=0; j < halfPeriod; j++)
	    {
		ret[i++] = offset - amplitude;
	    }
	}
	
	return ret;
    }
}
