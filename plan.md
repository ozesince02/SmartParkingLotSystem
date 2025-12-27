# 🚗 Smart Parking Lot – Low Level Design (Java)

## 1. Objective

Design and implement a **backend system for a smart parking lot** that:

* Manages **vehicle entry and exit**
* Allocates parking spots based on **vehicle type**
* Tracks parking duration
* Calculates parking fees
* Handles **concurrent entries/exits safely**

---

## 2. High-Level Architecture

### System Boundary

* **ParkingLot** is the **root aggregate**
* All **entry and exit operations happen via EntryGate and ExitGate**
* Floors and spots are internal to ParkingLot

---

## 3. Recommended Package Structure

```
src.main.java.com.airtribe.smartparkinglot
│
├── entities
│   ├── ParkingLot.java
│   ├── ParkingFloor.java
│   ├── ParkingSpot.java
│   ├── ParkingTicket.java
│   ├── Vehicle.java
│
├── enums
│   ├── VehicleType.java
│
├── services
│   ├── ParkingLotService.java
│   ├── AllocationService.java
│   ├── FeeCalculator.java
│
├── strategies
│   ├── FeeStrategy.java
│   ├── CarFeeStrategy.java
│   ├── BikeFeeStrategy.java
│   ├── BusFeeStrategy.java
│
├── gates
│   ├── EntryGate.java
│   ├── ExitGate.java
│
├── repository
│   ├── ParkingTicketRepository.java
│
├── exceptions
│   ├── ParkingFullException.java
│   ├── InvalidTicketException.java
│
├── utils
│   ├── TimeUtil.java
│
└── Main.java   (optional demo / test runner)
```

---

## 4. Package-wise Responsibilities

### 4.1 `entities`

Core domain objects (no business logic leakage)

* **ParkingLot**

  * Holds list of `ParkingFloor`
  * Delegates entry/exit to services
  * Does not expose internal structures

* **ParkingFloor**

  * Contains multiple `ParkingSpot`
  * Provides available spot lookup

* **ParkingSpot**

  * Has `spotId`, `allowedVehicleType`, `occupied`
  * Thread-safe `park()` and `remove()`

* **ParkingTicket**

  * Stores entry time, exit time
  * Linked to `Vehicle` and `ParkingSpot`

* **Vehicle**

  * Contains vehicle number and type

---

### 4.2 `enums`

```java
VehicleType { MOTORCYCLE, CAR, BUS }
```

---

### 4.3 `services`

* **ParkingLotService**

  * Entry point for EntryGate and ExitGate
  * Uses AllocationService & FeeCalculator

* **AllocationService**

  * Allocates parking spot
  * Uses synchronized / locking to avoid race conditions
  * Allocation strategy: first-fit floor → spot

* **FeeCalculator**

  * Delegates fee calculation using FeeStrategy
  * Computes duration from ParkingTicket

---

### 4.4 `strategies`

Implements **Strategy Design Pattern**

* **FeeStrategy**

  * `double calculateFee(long hours)`

* Concrete strategies:

  * `BikeFeeStrategy`
  * `CarFeeStrategy`
  * `BusFeeStrategy`

Each vehicle type has a different hourly rate.

---

### 4.5 `gates`

* **EntryGate**

  * Accepts Vehicle
  * Calls `ParkingLotService.checkIn()`
  * Returns ParkingTicket

* **ExitGate**

  * Accepts Ticket ID
  * Calls `ParkingLotService.checkOut()`
  * Returns parking fee

> Entry & Exit are ALWAYS via gates

---

### 4.6 `repository`

Simulates persistence layer (in-memory)

* **ParkingTicketRepository**

  * Stores active tickets
  * Use `ConcurrentHashMap<String, ParkingTicket>`
  * Supports `save`, `find`, `remove`

---

### 4.7 `exceptions`

* **ParkingFullException**

  * Thrown when no spot is available

* **InvalidTicketException**

  * Thrown when ticket ID is not found

---

### 4.8 `utils`

* **TimeUtil**

  * Calculates duration in hours
  * Centralizes time-related logic

---

## 5. Concurrency Requirements (MANDATORY)

* `ParkingSpot`

  * `synchronized park()`
  * `synchronized remove()`

* `AllocationService`

  * synchronized allocation method

* `ParkingTicketRepository`

  * Use `ConcurrentHashMap`

---

## 6. Parking Spot Allocation Algorithm

1. Receive vehicle at EntryGate
2. ParkingLotService calls AllocationService
3. Iterate floors sequentially
4. Find first available compatible spot
5. Lock and allocate spot
6. Generate ticket

---

## 7. Parking Fee Calculation Logic

* Duration = `exitTime - entryTime`
* Minimum billing = 1 hour
* Fee = `hourlyRate × duration`

| Vehicle Type | Rate (₹/hour) |
| ------------ | ------------- |
| Bike         | 10            |
| Car          | 20            |
| Bus          | 50            |

---

## 8. Entry → Exit Flow

```
Vehicle
  ↓
EntryGate
  ↓
ParkingLotService
  ↓
AllocationService → ParkingFloor → ParkingSpot
  ↓
ParkingTicket

Exit:
ExitGate → ParkingLotService → FeeCalculator → Payment
```

---

## 9. Design Principles Followed

* SOLID principles
* Strategy Pattern
* Encapsulation of domain
* Thread safety
* Clean separation of concerns
* Extensible for:

  * New vehicle types
  * New pricing strategies
  * Database integration

---

## 10. Deliverables for Coding Agent

✅ Create package structure
✅ Implement all classes
✅ Ensure thread safety
✅ No business logic in entities
✅ Clean naming and documentation

---