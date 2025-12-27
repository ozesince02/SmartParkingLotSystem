# Smart Parking Lot System (Java)

Implementation based on `plan.md` (LLD).

## How to run

- **Run demo (no Maven needed)**

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

- **Run tests (requires Maven installed)**

```bash
mvn test
```

- **Run demo**

```bash
mvn -q exec:java -Dexec.mainClass=com.airtribe.smartparkinglot.Main
```

If you don't have the Maven Exec plugin installed, you can also run `Main` from your IDE directly.


