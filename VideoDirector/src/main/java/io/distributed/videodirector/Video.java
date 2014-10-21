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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author gonzo
 */
public class Video
{
    public final static int METADATA = 0;
    public final static int RECEIVED = 1;
    public final static int PUBLISH  = 2;
    
    private final int id;
    private final int event_id;
    private final long finish_ts; // time stamp for end of video
    private int status;
    
    public Video(int event_id, int id, String timestamp)
        throws ParseException
    {
        Date ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
        
        this.id       = id;
        this.event_id = event_id;
        this.finish_ts = ts.getTime() / 1000;
        this.status   = METADATA;
    }
    
    public int getId()
    {
        return this.id;
    }
    public int getEventId()
    {
        return this.event_id;
    }
    
    public long getFinishTimestamp()
    {
        return this.finish_ts;
    }
    
    public boolean isReceived()
    {
        return status >= RECEIVED;
    }
    public void received()
    {
        this.status = RECEIVED;
    }
}
