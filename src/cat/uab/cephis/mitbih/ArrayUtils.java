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

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author dcr
 */
public class ArrayUtils<T> 
{

    public static <T> void addToLast(ArrayList<T> a, T v, int last) 
    {
        a.add(v);
        
        if (a.size() > last)
            a.remove(0);
    }

    public static double getMean(ArrayList<Double> beats) 
    {
        double acum = 0;
 
        for (Double d : beats) acum += d;

        double mean = acum / beats.size();
                
        return mean;
    }

    static double getMax(ArrayList<Double> noise) {
        return Collections.max(noise);
    }
    
}
