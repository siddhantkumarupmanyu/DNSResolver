package sku.dnsresolver;

public class SamplePackets {

    public static final short DEFAULT_ID = 1;
    public static final boolean DEFAULT_RECURSION = true;
    public static final short DEFAULT_QTYPE = 1;
    public static final short DEFAULT_QCLASS = 1;

    public static final byte[] QUERY_WWW_EXAMPLE_COM = {
            // header
            0x00, 0x01, // id
            0x01, // QR, OP Code, AA, TC, RD = 1
            0x00, // RA, Z, RCode
            0x00, 0x01, // QDCount
            0x00, 0x00, // ANCount
            0x00, 0x00, // Authoritative NSCount
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
            0x00, 0x00, // Authoritative NSCount
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
    public static final byte[] QUERY_CNAME_EXAMPLE_COM = {
            // header
            0x00, 0x01, // id
            0x01, // QR, OP Code, AA, TC, RD = 1
            0x00, // RA, Z, RCode
            0x00, 0x01, // QDCount
            0x00, 0x00, // ANCount
            0x00, 0x00, // Authoritative NSCount
            0x00, 0x00, // ARCount
            // body
            0x05, 0x63, 0x6e, 0x61, 0x6d, 0x65, // 4 cname
            0x07, 0x65, 0x78, 0x61, 0x6d, 0x70, 0x6c, 0x65, // 7 example
            0x03, 0x63, 0x6f, 0x6d, // 3 com
            0x00, // null label or termination
            0x00, 0x01, // QType
            0x00, 0x01, // QClass
    };

    public static final byte[] RESPONSE_CNAME_EXAMPLE_COM = {
            // header
            0x00, 0x01, // id
            (byte) 0x81, // QR = 1, OP Code = 0000, AA = 0, TC = 0, RD = 1
            (byte) 0x80, // RA = 1, Z = 0, 0, 0, RCode = 0000
            0x00, 0x01, // QDCount
            0x00, 0x02, // ANCount
            0x00, 0x00, // Authoritative NSCount
            0x00, 0x00, // ARCount
            // body
            //      query
            0x05, 0x63, 0x6e, 0x61, 0x6d, 0x65, // 4 cname
            0x07, 0x65, 0x78, 0x61, 0x6d, 0x70, 0x6c, 0x65, // 7 example
            0x03, 0x63, 0x6f, 0x6d, // 3 com
            0x00, // null
            0x00, 0x01, // QType
            0x00, 0x01, // QClass
            //      answer 1
            (byte) 0xc0, 0x0c, // Name, used pointer offset = 0x0c
            0x00, 0x05, // Type = CNAME
            0x00, 0x01, // Class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x6, // RDLength
            0x03, 0x77, 0x77, 0x77, (byte) 0xc0, 0x12, // 3 com pointer offset = 0x12
            //      answer 2
            (byte) 0xc0, 0x2f, // Name, used pointer offset = 0x2f
            0x00, 0x01, // Type
            0x00, 0x01, // Class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x04, // RDLength
            0x7f, 0x00, 0x00, 0x02 // RData
    };


    public static final byte[] QUERY_ROOT_NS = {
            // header
            0x00, 0x01, // id
            0x00, // QR, OP Code, AA, TC, RD = 0
            0x00, // RA, Z, RCode
            0x00, 0x01, // QDCount
            0x00, 0x00, // ANCount
            0x00, 0x00, // Authoritative NSCount
            0x00, 0x00, // ARCount
            // body
            0x00, // null label or termination
            0x00, 0x02, // QType
            0x00, 0x01, // QClass
    };

    public static final byte[] RESPONSE_ROOT_NS = {
            // header
            0x00, 0x01, // id
            (byte) 0x80, // QR = 1, OP Code = 0000, AA = 0, TC = 0, RD = 0
            (byte) 0x80, // RA = 1, Z = 0, 0, 0, RCode = 0000
            0x00, 0x01, // QDCount
            0x00, 0x02, // ANCount
            0x00, 0x00, // Authoritative NSCount
            0x00, 0x00, // ARCount
            // body
            //      query
            0x00, // null
            0x00, 0x02, // QType
            0x00, 0x01, // QClass
            //      answer
            0x00,
            0x00, 0x02, // Type
            0x00, 0x01, // Class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x14, // RDLength
            // RData
            0x01, 0x61, // 1 a
            0x0c, 0x72, 0x6f, 0x6f, 0x74, 0x2d, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73, // 12 root-servers
            0x03, 0x6e, 0x65, 0x74, // 3 net
            0x00, // terminator
            0x00,
            0x00, 0x02, // Type
            0x00, 0x01, // Class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x4, // RDLength
            0x01, 0x62, // 1 b
            (byte) 0xc0, 0x1e // pointer offset = 0x1e
    };
    public static final byte[] QUERY_ROOT_NS_IP_ADDRESS = {
            0x00, 0x01, // id
            0x00, // QR = 0, OP Code = 0000, AA = 0, TC = 0, RD = 0
            0x00, // RA, Z = 0, 0, 0, RCode = 0000
            0x00, 0x01, // QDCount
            0x00, 0x02, // ANCount
            0x00, 0x00, // Authoritative NSCount
            0x00, 0x00, // ARCount
            // body
            0x01, 0x61, // 1 a
            0x0c, 0x72, 0x6f, 0x6f, 0x74, 0x2d, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73, // 12 root-servers
            0x03, 0x6e, 0x65, 0x74, // 3 net
            0x00, // null
            0x00, 0x01, // QType
            0x00, 0x01 // QClass
    };
    public static final byte[] RESPONSE_ROOT_NS_IP_ADDRESS = {
            0x00, 0x01, // id
            (byte) 0x80, // QR = 1, OP Code = 0000, AA = 0, TC = 0, RD = 0
            (byte) 0x80, // RA = 1, Z = 0, 0, 0, RCode = 0000
            0x00, 0x01, // QDCount
            0x00, 0x01, // ANCount
            0x00, 0x00, // Authoritative NSCount
            0x00, 0x00, // ARCount
            // body
            //      query
            0x01, 0x61,// 1 a
            0x0c, 0x72, 0x6f, 0x6f, 0x74, 0x2d, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73, // 12 root-servers
            0x03, 0x6e, 0x65, 0x74, // 3 net
            0x00, // null
            0x00, 0x01, // type
            0x00, 0x01, // class
            (byte) 0xc0, 0x0c, // pointer offset = 0x0c
            0x00, 0x01, // type
            0x00, 0x01, // class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x04, // RD Length
            0x7f, 0x00, 0x00, 0x01 // address
    };


    public static final byte[] QUERY_COM_NS = {
            // header
            0x00, 0x01, // id
            0x00, // QR, OP Code, AA, TC, RD = 0
            0x00, // RA, Z, RCode
            0x00, 0x01, // QDCount
            0x00, 0x00, // ANCount
            0x00, 0x00, // Authoritative NSCount
            0x00, 0x00, // ARCount
            // body
            0x03, 0x63, 0x6f, 0x6d, // 3 com
            0x00, // null label or termination
            0x00, 0x02, // QType
            0x00, 0x01, // QClass
    };
    public static final byte[] RESPONSE_COM_NS = {
            0x00, 0x01, // id
            (byte) 0x80, // QR = 1, OP Code = 0000, AA = 0, TC = 0, RD = 0
            0x00, // RA = 0, Z = 0, 0, 0, RCode = 0000
            0x00, 0x01, // QDCount
            0x00, 0x00, // ANCount
            0x00, 0x02, // Authoritative NSCount
            0x00, 0x04, // ARCount
            // body
            //      query
            0x03, 0x63, 0x6f, 0x6d, // 3 com
            0x00, // null
            0x00, 0x02, // type
            0x00, 0x01, // class
            // authoritative name servers for com
            // NS - 1
            (byte) 0xc0, 0x0c, // pointer offset = 0x0c
            0x00, 0x02, // type
            0x00, 0x01, // class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x14, // RD length
            // address
            0x01, 0x61, // 1 a
            0x0c, 0x67, 0x74, 0x6c, 0x64, 0x2d, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73, // 12 gtld-servers.net
            0x03, 0x6e, 0x65, 0x74, // 3 net
            0x00, // null
            // NS - 2
            (byte) 0xc0, 0x0c, // pointer offset = 0x0c
            0x00, 0x02, // type
            0x00, 0x01, // class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x04, // RD Length
            // address
            0x01, 0x62, // 1 b
            (byte) 0xc0, 0x23, // pointer offset = 0x23
            // additional Section
            // for a.gtld-servers.net
            (byte) 0xc0, 0x21, // pointer
            0x00, 0x01, // type
            0x00, 0x01, // class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x04, // data length
            (byte) 0xc0, 0x0c, 0x5e, 0x1e, // address = 192.12.94.30
            // ipv6 address for above address; we do not support it yet.
            (byte) 0xc0, 0x21,// pointer
            0x00, 0x1c, // type = IPv6
            0x00, 0x01, // class
            0x00, 0x02, (byte) 0xa3, 0x00, // ttl
            0x00, 0x10, // data length
            0x20, 0x01, 0x05, 0x02, 0x1c, (byte) 0xa1, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30, // address
            // for b.gtld-servers.net
            (byte) 0xc0, 0x41, // pointer
            0x00, 0x01, // type
            0x00, 0x01, // class
            0x00, 0x00, 0x00, 0x73, // TTL
            0x00, 0x04, // data length
            (byte) 0xc0, 0x21, 0x0e, 0x1e, // address = 192.33.14.30
            // ipv6 address for above address; we do not support it yet.
            (byte) 0xc0, 0x41, // pointer
            0x00, 0x1c, // type = IPv6
            0x00, 0x01, // class
            0x00, 0x02, (byte) 0xa3, 0x00, // ttl
            0x00, 0x10, // data length
            0x20, 0x01, 0x05, 0x03, 0x23, 0x1d, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x30, // address

    };

    // TODO:
    public static final byte[] QUERY_EXAMPLE_NS = {};
    public static final byte[] RESPONSE_EXAMPLE_NS = {};
    public static final byte[] QUERY_EXAMPLE_NS_IP_ADDRESS = {};
    public static final byte[] RESPONSE_EXAMPLE_NS_IP_ADDRESS = {};


    public static final byte[] QUERY_WWW_EXAMPLE_NS = {};
    public static final byte[] RESPONSE_WWW_EXAMPLE_NS = {};
}
