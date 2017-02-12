Introduction
---
`aiscli` is a command line utility intended to parse, filter and manipulate NMEA armoured AIS messages.

NMEA-armoured AIS messages look like this:

```
!AIVDM,1,1,,B,403t?hAuho;N>`Pc:j>Kgq700D2D,0*2C
!AIVDM,2,1,8,A,569r?PP000000000000P4UQDr3737000000000000000040000000000,0*08
!AIVDM,2,2,8,A,000000000000000,2*2C
```

And by using `aiscli` they can for example be converted into more manageable formats, such as JSON or CSV:

```
received,sourceMmsi,digest,msgtype,valid,lat,lng,cog,sog,cls,shipname,callsigns,shiptype,bow,stern,port,starboard,destination,draught,eta,imo,dev,day,month,year,hour,minute,second
2017-02-12T07:49:00.490Z,4132801,A8F19241A6BA2700E6495F440BCC7D5C78654836,4,true,25.224487,118.98596,,,,,,,,,,,,,,,,14,3,2012,11,30,14
2017-02-12T07:49:00.545Z,413044610,6670FA1D340465261E9482C5FAF0279949AD107F,5,true,,,,,,HAIXUN 1010,,NotAvailable,0,0,0,0,,0.0,00-00 00:00,0,Gps,,,,,,
```

Usage
---
`aiscli` is used from the command line like this:

```
usage: java -jar aiscli.jar -o <format> [-v]
 -o,--output <format>   Output received messages on stdout in given format
                        ('csv', 'json').
 -v,--verbose           Produce verbose output.
```
 
The tool operates on the shell's standard input and standard output, and can therefore be combined with other command
line tools using  Linux style _piping_ like this:

```
cat src/test/resources/ais-sample.nmea | sed -n '/^!AIVDM/p' | java -jar aiscli.jar -o csv > output.csv
```

License
---
Free for non-commercial use. See details in the [LICENSE](LICENSE) file.

Roadmap
---
- Stateful conversion based on _tracks_ instead of _messages_
- Expression-based filtering based on aisutils

Notes
---
When using a non-release version of `aiscli` the command line looks like this:

```
java -jar target/aiscli-1.0-SNAPSHOT-jar-with-dependencies.jar -o csv
```