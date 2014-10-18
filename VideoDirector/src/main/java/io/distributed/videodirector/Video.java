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

/**
 *
 * @author gonzo
 */
public class Video
{
    public enum status_t
    {
        METADATA,
        UPLOAD,
        RECEIVED,
    };
    
    private final int id;
    private final int event_id;
    private status_t  status;
    
    public Video(int event_id, int id)
    {
        this.id       = id;
        this.event_id = event_id;
        this.status   = status_t.METADATA;
    }
    
    public int getId()
    {
        return this.id;
    }
    public int getEventId()
    {
        return this.event_id;
    }
    
    public boolean isReceived()
    {
        return status == status_t.RECEIVED;
    }
    public void received()
    {
        this.status = status_t.RECEIVED;
    }
}
