package sku.dnsresolver;

public class SamplePackets {

    public static final short DEFAULT_ID = 1;
    public static final boolean DEFAULT_RECURSION = true;
    public static final short DEFAULT_QTYPE = 1;
    public static final short DEFAULT_QCLASS = 1;

    public static final byte[] QUERY_WWW_EXAMPLE_COM = {
            // header
            0x00, 0x01, // id
            0x01, // QR, OP Code, AA, TC, RD = 0
            0x00, // RA, Z, RCode
            0x00, 0x01, // QDCount
            0x00, 0x00, // ANCount
            0x00, 0x00, // NSCount
            0x00, 0x00, // ARCount
            // body
            0x03, 0x77, 0x77, 0x77, // 3 www
            0x07, 0x65, 0x78, 0x61, 0x6d, 0x70, 0x6c, 0x65, // 7 example
            0x03, 0x63, 0x6f, 0x6d, // 3 com
            0x00, // null label or termination
            0x00, 0x01, // QType
            0x00, 0x01, // QClass
    };

    public static final byte[] RESPONSE_WWW_EXAMPLE_COM = {
            // header
            0x00, 0x01, // id
            (byte) 0x81, // QR = 1, OP Code = 0000, AA = 0, TC = 0, RD = 1
            (byte) 0x80, // RA = 1, Z = 0, 0, 0, RCode = 0000
            0x00, 0x01, // QDCount
            0x00, 0x01, // ANCount
            0x00, 0x00, // NSCount
            0x00, 0x00, // ARCount
            // body
            //      query
            0x03, 0x77, 0x77, 0x77, // 3 www
            0x07, 0x65, 0x78, 0x61, 0x6d, 0x70, 0x6c, 0x65, // 7 example
            0x03, 0x63, 0x6f, 0x6d, // 3 com
            0x00, // null
            0x00, 0x01, // QType
            0x00, 0x01, // QClass
            //      answer
            (byte) 0xc0, 0x0c, // Name, used pointer offset = 0x0c
            0x00, 0x01, // Type
            0x00, 0x01, // Class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x04, // RDLength
            0x7f, 0x00, 0x00, 0x01 // RData
    };


}
