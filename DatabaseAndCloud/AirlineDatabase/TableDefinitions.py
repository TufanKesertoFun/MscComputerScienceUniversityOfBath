class TableDefinitions:

    def __init__(self):
        self.table_definitions = {
            "Aircraft":
            """
              CREATE TABLE IF NOT EXISTS Aircraft (
                  AircraftID INT PRIMARY KEY,
                  Model VARCHAR(50),
                  Capacity INT,
                  Manufacturer VARCHAR(50),
                  YearManufactured INT,
                  Range INT,
                  Status VARCHAR(20)
              );
          """,
            "Flight":
            """
              CREATE TABLE IF NOT EXISTS Flight (
                  FlightID INT PRIMARY KEY,
                  FlightNumber VARCHAR(10),
                  DepartureTime DATETIME,
                  ArrivalTime DATETIME,
                  Origin VARCHAR(50),
                  DestinationID INT,
                  Duration INT,
                  Gate VARCHAR(10),
                  Status VARCHAR(20),
                  AircraftID INT,
                  FOREIGN KEY (AircraftID) REFERENCES Aircraft(AircraftID)
              );
          """,
            "Pilot":
            """
              CREATE TABLE IF NOT EXISTS Pilot (
                  PilotID INT PRIMARY KEY,
                  Name VARCHAR(100),
                  LicenseNumber VARCHAR(50),
                  DateOfBirth DATE,
                  YearsExperience INT,
                  LicenseType VARCHAR(50),
                  Experience INT
              );
          """,
            "Destination":
            """
              CREATE TABLE IF NOT EXISTS Destination (
                  DestinationID INT PRIMARY KEY,
                  AirportName VARCHAR(100),
                  City VARCHAR(50),
                  Country VARCHAR(50)
              );
          """,
            "Passenger":
            """
              CREATE TABLE IF NOT EXISTS Passenger (
                  PassengerID INT PRIMARY KEY,
                  Name VARCHAR(100),
                  PassportNumber VARCHAR(20),
                  Email VARCHAR(100),
                  PhoneNumber VARCHAR(20)
              );
          """
        }
