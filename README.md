# DNSResolver
A simple DNS Resolver in Java. <br />
Currently it supports resolving domain name with/without recursion desired. <br />
As of now, it does not support IPv6. <br />
SamplePacket.java contains the DNS sample packets captured with Wireshark.

## Running Application
```
./gradlew run
```

## Running Tests
This will run both end-to-end(acceptence) as well as unit tests.
```
./gradlew test
```

## Todo
Add remaining features found on https://digwebinterface.com/

## Tools Used
- Wireshark

## References
- https://courses.cs.duke.edu//fall16/compsci356/DNS/DNS-primer.pdf
- Wireshark's Packet Details Pane
