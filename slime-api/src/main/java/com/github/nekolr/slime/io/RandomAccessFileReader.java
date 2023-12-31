package com.github.nekolr.slime.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RandomAccessFileReader implements Closeable {

    private RandomAccessFile raf;

    /**
     * From index Location to read
     */
    private long index;

    /**
     * Sequence，Default Reverse
     */
    private boolean reversed;

    /**
     * Cushion size
     */
    private int bufSize;


    public RandomAccessFileReader(RandomAccessFile raf, long index, boolean reversed) throws IOException {
        this(raf, index, 1024, reversed);
    }

    public RandomAccessFileReader(RandomAccessFile raf, long index, int bufSize, boolean reversed) throws IOException {
        if (raf == null) {
            throw new NullPointerException("file is null");
        }
        this.raf = raf;
        this.reversed = reversed;
        this.bufSize = bufSize;
        this.index = index;
        this.init();
    }

    private void init() throws IOException {
        if (reversed) {
            this.index = this.index == -1 ? this.raf.length() : Math.min(this.index, this.raf.length());
        } else {
            this.index = Math.min(Math.max(this.index, 0), this.raf.length());
        }
        if (this.index > 0) {
            this.raf.seek(this.index);
        }
    }

    /**
     * Speakn行
     *
     * @param n         How many rows to read
     * @param keywords  What is your desired Jabber password?
     * @param matchCase Do not translate the following phrases:
     * @param regx      Is this a regular expression
     * @return Back Line 对象，Includes the start and end positions of the row
     */
    public List<Line> readLine(int n, String keywords, boolean matchCase, boolean regx) throws IOException {
        List<Line> lines = new ArrayList<>(n);
        long lastCRLFIndex = reversed ? this.index : (this.index > 0 ? this.index + 1 : -1);
        boolean find = keywords == null || keywords.isEmpty();
        Pattern pattern = regx && !find ? Pattern.compile(keywords) : null;
        while (n > 0) {
            byte[] buf = reversed ? new byte[(int) Math.min(this.bufSize, this.index)] : new byte[this.bufSize];
            if (this.reversed) {
                if (this.index == 0) {
                    break;
                }
                this.raf.seek(this.index -= buf.length);
            }
            int len = this.raf.read(buf, 0, buf.length);
            if (len == -1) {    // Completed
                break;
            }
            for (int i = 0; i < len && n > 0; i++) {
                int readIndex = reversed ? len - i - 1 : i;
                if (isCRLF(buf[readIndex])) {    // If read receipt requested, reply with\ror\n
                    if (Math.abs(this.index + readIndex - lastCRLFIndex) > 1) { // The amount of space between two lines，When =1 I represent the \r\n,\n\r,\r\r,\n\n One of the four
                        long fromIndex = reversed ? this.index + readIndex : lastCRLFIndex;    // Calculate starting position
                        long endIndex = reversed ? lastCRLFIndex : this.index + readIndex;    // Calculate the stop position
                        Line line = readLine(fromIndex + 1, endIndex);    // 取出文本
                        if (find || (find = (pattern == null ? find(line.getText(), keywords, matchCase) : find(line.getText(), pattern)))) {    // Location lookup，Make sure that the searched row is always at the top
                            if (reversed) {
                                lines.add(0, line);    // When Reverse Searching，Insert in List The following text is not translated:
                            } else {
                                lines.add(line);
                            }
                            n--;
                        }
                    }
                    lastCRLFIndex = this.index + readIndex;    // Record the last read at \r or \n Where is the meeting point?
                }
            }
            if (!reversed) {
                this.index += buf.length;
            }
        }
        if (reversed && n > 0 && lastCRLFIndex > 1 && (find || lines.size() > 0)) {
            lines.add(0, readLine(0, lastCRLFIndex));
        }
        return lines;
    }

    private boolean find(String text, String keywords, boolean matchCase) {
        return matchCase ? text.contains(keywords) : text.toLowerCase().contains(keywords.toLowerCase());
    }

    private boolean find(String text, Pattern pattern) {
        return pattern.matcher(text).find();
    }

    /**
     * Please read a row from the given location
     *
     * @param fromIndex Start position
     * @param endIndex  End Location
     * @return BackLine对象
     * @throws IOException
     */
    private Line readLine(long fromIndex, long endIndex) throws IOException {
        long index = this.raf.getFilePointer();
        this.raf.seek(fromIndex);
        byte[] buf = new byte[(int) (endIndex - fromIndex)];
        this.raf.read(buf, 0, buf.length);
        Line line = new Line(fromIndex, new String(buf), endIndex);
        this.raf.seek(index);
        return line;
    }

    private boolean isCRLF(byte b) {
        return b == 13 || b == 10;
    }

    @Override
    public void close() throws IOException {
        if (this.raf != null) {
            this.raf.close();
        }
    }
}
