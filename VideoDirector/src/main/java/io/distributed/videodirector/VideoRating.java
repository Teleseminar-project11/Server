/*
 * The MIT License
 *
 * Copyright 2014 gonzo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.distributed.videodirector;

import com.google.gson.JsonObject;

/**
 *
 * @author gonzo
 */
public class VideoRating
{
    static int rank(double[] kernel)
    {
        double dot = 0.0f;
        for (double f : kernel)
        {
            dot += f * f;
        }
        double root = Math.sqrt(dot);
        
        return (int) (root * 100.0);
    }
    
    static int rate(JsonObject obj)
    {
        // finish_time timestamp not null,
        // duration int not null,
        // width int not null,
        // height int not null,
        //shaking int not null,
        // tilt int not null,
        int tilt  = obj.get("tilt").getAsInt();
        int shake = obj.get("shaking").getAsInt();
        
        // apply sigmoid function for mapping to [0, 1]
        double ntilt = Math.tanh(tilt);
        double nshake = Math.tanh(shake);
        
        // given that most mobile videos are low w:h does it even matter?
        double kernel[] = { ntilt, nshake };
        return rank(kernel);
    }
}
