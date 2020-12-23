package com.funguscow.gible.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Stack;

/**
 * Provides code points one-at-a-time
 * Primarily serves as an in-between to handle BOMs and encodings
 */
public class CodePointStream {

    private InputStreamReader reader;
    private int lastPoint;
    private boolean pushedBack = false, startedReading = false;
    private boolean eof = false;
    private int lineno, colno;
    private Stack<Integer> backward = new Stack<>();
    private int countdown = -1;

    /**
     *
     * @param reader Already-constructed reader with proper encoding
     */
    public CodePointStream(InputStreamReader reader){
        this.reader = reader;
    }

    /**
     *
     * @param source A Java string to read from
     */
    public CodePointStream(String source){
        this(new InputStreamReader(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
    }

    /**
     *
     * @param bis BufferedInputStream(must allow marking and resetting) from which encoding is determined
     * @throws IOException
     */
    public CodePointStream(BufferedInputStream bis) throws IOException{
        bis.mark(4);
        byte[] bom = new byte[3];
        int read = bis.read(bom, 0, 3);
        System.out.println("BOM is " + Arrays.toString(bom));
        Charset encoding = StandardCharsets.UTF_8;
        if(read >= 3){
            if(bom[0] == (byte)0xEF && bom[1] == (byte)0xBB && bom[2] == (byte)0xBF) {
                read = 0;
            }
        }
        if(read >= 2){
            bis.reset();
            bis.skip(2);
            if(bom[0] == (byte)0xFE && bom[1] == (byte)0xFF) {
                encoding = StandardCharsets.UTF_16BE;
            }
            else if(bom[0] == (byte)0xFF && bom[1] == (byte)0xFE) {
                encoding = StandardCharsets.UTF_16LE;
            }
            else {
                bis.reset();
            }
        }
        reader = new InputStreamReader(bis, encoding);
    }

    /**
     * If marking is supported on the stream, and encoding is specified as allowing a BOM,
     * it will be looked for and skipped if present
     * @param stream InputStream symbols are read from
     * @param encoding Encoding of stream
     * @throws IOException
     */
    public CodePointStream(InputStream stream, Charset encoding) throws IOException{
        if(stream.markSupported()) {
            stream.mark(3);
            byte[] bom = new byte[3];
            if (encoding == StandardCharsets.UTF_16) {
                int read = stream.read(bom, 0, 2);
                if (read == 2) {
                    if (bom[0] == (byte) 0xFE && bom[1] == (byte) 0xFF)
                        encoding = StandardCharsets.UTF_16BE;
                    else if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE)
                        encoding = StandardCharsets.UTF_16LE;
                    else {
                        encoding = StandardCharsets.UTF_16LE;
                        stream.reset();
                    }
                } else stream.reset();
            } else if (encoding == StandardCharsets.UTF_16LE) {
                int read = stream.read(bom, 0, 2);
                if (!(read == 2 && bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE))
                    stream.reset();
            } else if (encoding == StandardCharsets.UTF_16BE) {
                int read = stream.read(bom, 0, 2);
                if (!(read == 2 && bom[0] == (byte) 0xFE && bom[1] == (byte) 0xFF))
                    stream.reset();
            } else if(encoding == StandardCharsets.UTF_8){
                int read = stream.read(bom, 0, 3);
                if(!(read == 3 && bom[0] == (byte)0xEF && bom[1] == (byte)0xBB && bom[2] == (byte)0xBF))
                    stream.reset();
            }
        }
        reader = new InputStreamReader(stream, encoding);
    }

    /**
     * Limit how many characters to read before sending EOF
     * @param c Characters that can remain
     */
    public void countdown(int c){
        countdown = c;
    }

    /**
     * Push back the previous character to be returned on next call to `read`
     */
    public void pushBack(){
        if(!startedReading)
            throw new IllegalStateException("Attempt to push back before reading anything");
        if(pushedBack)
            throw new IllegalStateException("Code point already pushed back upon push back call");
        pushedBack = true;
    }

    /**
     * Push back a code point up to an arbitrary amount
     * @param point Point to push back
     */
    public void pushBack(int point){
        if(pushedBack)
            backward.push(lastPoint);
        pushedBack = false;
        backward.push(point);
    }

    /**
     * Get the next codepoint
     * @return Codepoint read, or -1 for EOF
     * @throws IOException
     */
    public int read() throws IOException {
        startedReading = true;
        if(!backward.isEmpty()) {
            lastPoint = backward.pop();
            return lastPoint;
        }
        if(countdown == 0)
            return -1;
        if(eof) {
            pushedBack = false;
            return -1;
        }
        if(pushedBack){
            pushedBack = false;
            return lastPoint;
        }
        else {
            lastPoint = reader.read();
            if(countdown > 0)
                countdown--;
        }
        if(lastPoint == '\n'){
            lineno ++;
            colno = 0;
        }
        else
            colno ++;
        if(lastPoint == -1)
            eof = true;
        if(lastPoint >= 0xD800){
            int second = reader.read();
            lastPoint = 0x10000 | ((lastPoint - 0xD800) << 10) | (second - 0xDC00);
        }
        return lastPoint;
    }

    public int getLineno(){
        return lineno;
    }

    public int getColno(){
        return colno;
    }

    public boolean eof(){
        return (eof || countdown == 0) &&
                (backward.isEmpty() || backward.stream().allMatch(Integer.valueOf(-1)::equals));
    }

}
