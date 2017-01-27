Introduction
---
Note to self:

```
cat ../nmeaais.txt | sed -n '/^!AIVDM/p' | java -jar target/aiscli-1.0-SNAPSHOT-jar-with-dependencies.jar  > output.csv
```
