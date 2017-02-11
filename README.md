Introduction
---
Notes to self:

Getting started - try this:

```
$ cat src/test/resources/nmea.sample | java -jar target/aiscli-1.0-SNAPSHOT-jar-with-dependencies.jar -o csv
```

or 

```
cat ../nmeaais.txt | sed -n '/^!AIVDM/p' | java -jar target/aiscli-1.0-SNAPSHOT-jar-with-dependencies.jar  > output.csv
```
