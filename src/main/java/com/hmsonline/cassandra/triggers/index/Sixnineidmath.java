package com.hmsonline.cassandra.triggers.index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Id generator based on  Twitter's Snowflake
 * http://github.com/twitter/snowflake
 *
 *
 */
public class Sixnineidmath {

    private static final Logger log = LoggerFactory.getLogger(Sixnineidmath.class);

    private static final long sixnineid = 1288834974657L;    // give`s ids for roughly 69 year`s thus sinxnine

    private static final long workerIdBits = 5L;
    private static final long datacenterIdBits = 5L;
    private static final long sequenceBits = 12L;

    private static final long workerIdShift = sequenceBits;
    private static final long datacenterIdShift = sequenceBits + workerIdBits;
    private static final long timestampShift = sequenceBits + workerIdBits + datacenterIdBits;

    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);
    private static final long workerIdMask = -1L ^ (-1L << workerIdBits);
    private static final long datacenterIdMask = -1L ^ (-1L << datacenterIdBits);

    private final long workerId;
    private final long datacenterId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public Sixnineidmath(final long datacenterId, final long workerId) {
        this.datacenterId = datacenterId & datacenterIdMask;
        this.workerId = workerId & workerIdMask;
    }

    public synchronized long nextId() {
        long timestamp = timestamp();

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                System.out.println("retitning last timestamp");
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        if (timestamp < lastTimestamp) {
            log.error("Clock is moving backwards. Rejecting requests until " + lastTimestamp + ".");
            throw new IllegalStateException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }

        lastTimestamp = timestamp;
        return ((timestamp - sixnineid) << timestampShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = timestamp();
        }
        return timestamp;
    }

    protected long timestamp() {
        return System.currentTimeMillis();
    }


        /*

        //Simple check if the generation is working

       public static void main(String[] args) {
           Sixnineidmath sixnineidmath = new Sixnineidmath(3,1);

             int i=0;
             while(i++<2) {
               System.out.println(sixnineidmath.nextId());
             }
      }
         */

}