package org.odict.stardict2odict;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

/**
 * StarDict dictionary file parser
 * usage as follows:<br/>
 * <code>StarDictParser rdw=new StarDictParser();</code><br/>
 * <code>rdw.loadIndexFile(f);</code><br/>
 * <code>rdw.loadContentFile(fcnt);</code><br/>
 *
 * @author beethoven99@126.com
 */
public class StarDictParser {
    // Global buffer
    private byte buf[] = new byte[1024];

    // Marks the start of the string
    private int smark;

    // Buffer marker
    private int mark;

    // A map of all words and their positions
    private Map<String, WordPosition> words = new HashMap<>();

    //随机读取字典内容
    private RandomAccessFile randomAccessFile;

    private RandomAccessFile streamToTempFile(String name, InputStream s) throws IOException {
        final File tempFile = File.createTempFile(name,".tmp");
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        FileChannel fc = raf.getChannel();
        tempFile.deleteOnExit();

        try {
            final FileLock fl = fc.tryLock();

            if (fl == null) {
                throw new Error("Could not lock temporary file");
            } else {
                final ReadableByteChannel in = Channels.newChannel(new BufferedInputStream(s));

                try {
                    for (final ByteBuffer buffer = ByteBuffer.allocate(1024); in.read(buffer) != -1;) {
                        buffer.flip();
                        fc.write(buffer);
                        buffer.clear();
                    }
                } finally {
                    fl.release();
                }
            }

            raf.seek(0);

            return raf;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private RandomAccessFile decompressDict(InputStream s) {
        try {
            RandomAccessFile tmpFile = streamToTempFile("stardictc", s);
            RandomAccessInputStream rais = new RandomAccessInputStream(tmpFile);
            DictZipInputStream dzis = new DictZipInputStream(rais, rais.available());
            dzis.seek(0);
            return streamToTempFile("stardict", dzis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public StarDict parse(String f) throws IOException {
        FileInputStream in = new FileInputStream(f);
        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
        TarArchiveInputStream tarIn = new TarArchiveInputStream(bzIn);
        ArchiveEntry entry;

        while (null != (entry = tarIn.getNextEntry())) {
            String entryName = entry.getName();

            if (entry.getSize() < 1)
                continue;
            else if (entryName.contains(".idx"))
                this.loadIndexFile(tarIn);
            else if (entryName.contains("dict.dz")) {
                this.randomAccessFile = decompressDict(tarIn);
            }
        }

        tarIn.close();

        return new StarDict(this.words, this.randomAccessFile);
    }

    /**
     * Disposes of the parser and closes all open streams
     */
    public void dispose() {
        try {
            this.randomAccessFile.close();
        } catch (IOException e) {
            throw new Error("StarDictParser has already been disposed");
        }
    }

    /**
     * Loads the index file of the dictionary given a stream
     *
     * @param s
     */
    private void loadIndexFile(InputStream s) {
        try {
            // First, read it into the buffer
            int res = s.read(buf);
            while (res > 0) {
                mark = 0;
                smark = 0;
                parseByteArray(buf, 1024);
                if (mark == res) {
                    // The last one is complete, almost impossible, but still have to consider
                    res = s.read(buf);
                } else {
                    // Have not dealt with the last round, it should start from mark + 1
                    res = s.read(buf, buf.length - smark, smark);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a block of bytes
     *
     * @param buf 使用全局的，之后可以去掉它
     * @param len cbuf最多只有这么长
     * @throws UnsupportedEncodingException
     */
    private void parseByteArray(byte buf[], int len) throws UnsupportedEncodingException {
        for (; mark < len; ) {
            if (buf[mark] != 0) {
                // In the string
                if (mark == len - 1) {
                    // If the string is truncated
                    System.arraycopy(buf, smark, buf, 0, len - smark);
                    break;
                } else {
                    // Otherwise increment the marker
                    mark++;
                }
            } else {
                // If the marker is at zero, then the word is over
                String tword = null;
                if (mark != 0) {
                    byte[] bs = ArrayUtils.subarray(buf, smark, mark);
                    tword = new String(bs, "utf-8");
                }

                if (len - mark > 8) {
                    // If there are at least eight bytes left
                    smark = mark + 9;
                    byte[] bstartpos = ArrayUtils.subarray(buf, mark + 1, mark + 5);
                    byte[] blen = ArrayUtils.subarray(buf, mark + 5, mark + 9);

                    int startpos = ByteArrayHelper.toIntAsBig(bstartpos);
                    int strlen = ByteArrayHelper.toIntAsBig(blen);

                    // Now it's a complete word
                    if (tword != null && tword.trim().length() > 0 && strlen < 10000)
                        words.put(tword, new WordPosition(startpos, strlen));

                    mark += 8;
                } else {
                    // If there are less than eight
                    // After the jump has been skipped, we point to two eight-byte numbers
                    System.arraycopy(buf, smark, buf, 0, len - smark);
                    break;
                }
            }
        }
    }
}
