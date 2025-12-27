# Smart Parking Lot System (Java)

## How to run

- **Run demo (no build tool; no Maven/Gradle needed)**

```bash
# compile (PowerShell)
if (Test-Path out) { Remove-Item -Recurse -Force out }
New-Item -ItemType Directory -Force out | Out-Null
$src = Get-ChildItem -Recurse -Filter *.java src/main/java | ForEach-Object { $_.FullName }
javac -d out $src

# run
java -cp out com.airtribe.smartparkinglot.Main
```

- **Gradle (Java 25)**

Requires **Gradle 9.1+** for Java 25 support.

```bash
gradle test
gradle run
```
