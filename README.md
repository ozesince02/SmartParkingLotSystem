# Smart Parking Lot System (LLD)

A **low-level design (LLD)** project in Java that models a multi-floor parking lot with:
- **Entry/Exit gates**
- **Spot allocation** (first-fit)
- **Ticket lifecycle** (issue on entry, close on exit)
- **Fee calculation** using a **Strategy** per vehicle type
- Thread-safety for core operations (allocation + spot occupy/release)

> This repo includes a small, deterministic **demo flow** in `Main.java` to quickly visualize the end-to-end lifecycle.
> For exhaustive coverage of edge-cases (parking full, invalid ticket, concurrency, fee rules), refer to the **JUnit tests** under `src/test/java`.

---

## Class Diagram

The class diagram below summarizes the main components and their relationships. The image can be found in the repository as `SmartParkingLotSystem-Class-Diagram.png`.

![Smart Parking Lot System - Class Diagram](./SmartParkingLotSystem-Class-Diagram.png)

---

## Tech stack
- Java (toolchain configured to **Java 25** in Gradle)
- Gradle (wrapper included)
- JUnit 5

---

## Project structure (high level)

- `entities/` – core domain
  - `ParkingLot` → `ParkingFloor` → `ParkingSpot`
  - `Vehicle`, `ParkingTicket`
- `services/`
  - `AllocationService` – allocates a spot (synchronized, first-fit)
  - `ParkingLotService` – entry/exit orchestration (creates tickets, frees spots)
  - `FeeCalculator` – delegates fee computation to strategies
- `strategies/` – pricing strategies (`BikeFeeStrategy`, `CarFeeStrategy`, `BusFeeStrategy`)
- `repository/` – `ParkingTicketRepository` (in-memory `ConcurrentHashMap`)
- `gates/` – `EntryGate`, `ExitGate` (thin wrappers over `ParkingLotService`)
- `utils/` – `TimeUtil` (billable hours: min 1 hour, ceil rounding)

---

## Core flows

### Check-in (Entry)
1. `EntryGate.enter(vehicle)`
2. `ParkingLotService.checkIn(vehicle)`
3. `AllocationService.allocateSpot(lot, vehicle)` selects the first suitable spot and calls `spot.park(vehicle)`
4. A `ParkingTicket` is created and saved in `ParkingTicketRepository`

### Check-out (Exit)
1. `ExitGate.exit(ticketId)`
2. `ParkingLotService.checkOut(ticketId)` atomically removes the ticket from repository
3. Ticket is marked exited, spot is freed (`spot.remove()`)
4. `FeeCalculator.calculateFee(ticket)` computes total using `TimeUtil.billableHours` + vehicle strategy

---

## Extending the design
- Add a new vehicle type:
  1. Update `VehicleType`
  2. Create a new `FeeStrategy` implementation
  3. Register it in `FeeCalculator`
- Change allocation logic:
  - Replace/extend `AllocationService` (e.g., nearest gate, compact parking, priority floors)

---

## Run

### Option A: Run tests (recommended)
Use the Gradle wrapper:

```powershell
.\gradlew.bat test
```

### Option B: Run the sample demo
The demo lives in `src/main/java/com/airtribe/smartparkinglot/Main.java`.

```powershell
# Compile
if (Test-Path out) { Remove-Item -Recurse -Force out }
New-Item -ItemType Directory -Force out | Out-Null
$src = Get-ChildItem -Recurse -Filter *.java src/main/java | ForEach-Object { $_.FullName }
javac -d out $src

# Run
java -cp out com.airtribe.smartparkinglot.Main
```

---

## Test coverage
The test suite includes:
- Allocation behavior + concurrency safety
- Gate-level integration flow (entry → exit)
- Fee calculation (unit + parameterized tests across vehicle types/durations)
- Repository semantics
- Time rounding/billing rules

---

## Notes
- `AllocationService.allocateSpot(...)` is `synchronized` to avoid double allocation.
- `ParkingSpot.park(...)` and `ParkingSpot.remove()` are `synchronized` to make spot state changes thread-safe.
