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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author dcr
 */
public class DatReader
{
    private final BufferedInputStream bis;
    private final int channel;
    
    public DatReader(File file, int channel) throws FileNotFoundException, MalformedURLException, IOException
    {
        if (!file.exists())
        {
            URL url = new URL("http://www.physionet.org/physiobank/database/mitdb/"+file.getName());
            
            System.out.println("Downloading " + url);
            Wget.get(url, file);
        }
        
        bis = new BufferedInputStream(new FileInputStream(file));
        
        this.channel = channel;
               
        if ((channel < 0) || (channel > 1)) 
            throw new IllegalArgumentException("Supported channels are 0 and 1");
    }
    
    /**
     * Reads a sample from the first channel of the DAT file.
     * 
     * The DAT file is organized in 212 format  i.e.
     * 
     * [ 7 6 5 4 3 2 1 0 ] [ 7 6 4 5  3 2 1 0 ] [ 7 6 4 5 3 2 1 0 ] 
     * [  Channel 0 Low  ] [ Ch 1 H ][ Ch 0 L ] [  Channel 1 Low  ]
     * 
     * @return -1 if nothing read
     * 
     * @throws java.io.IOException 
     */
    public int get() throws IOException
    {
        byte[] buff = new byte[3];
        
        int n = bis.read(buff, 0, 3);
        
        if (n == -1)
            return -1;
        
        if (n != 3)
            throw new RuntimeException("Read less than 3 = " +  n);
        
        if (channel == 0)
        {
            int r = ((((byte)(buff[1] << 4) >> 4) << 8)) | (buff[0] & 0xFF);
            //System.out.println("r=" + r);
            return r;
        }
        else
        {
            int r = (( (buff[1] & 0xF0)) << (8-4)) | (buff[2] & 0xFF);
            return r;
        }
    }
    
    /**
     * 
     * @param buff
     * @return the number of bytes read
     * @throws IOException 
     */
    public int get(double[] buff) throws IOException
    {
        for (int i=0; i < buff.length; i++)
        {
            buff[i] = get();
            
            if (buff[i] == -1)
            {
                buff[i] = 0;
                return i-1;
            }
        }
        
        return buff.length;
    }

    void skip(int k) throws IOException {
        for (int i= 0; i < k; i++)
            get();
        
    }
}
