class MockData:

    def __init__(self):
        self.mock_data = {
            "Aircraft":
            [(10001, 'Boeing 737', 160, 'Boeing', 2005, 3200, 'Active'),
             (10002, 'Airbus A320', 180, 'Airbus', 2010, 3500, 'Active'),
             (10003, 'Embraer 190', 100, 'Embraer', 2008, 2800, 'Active'),
             (10004, 'Boeing 787', 242, 'Boeing', 2012, 5100, 'Active'),
             (10005, 'Airbus A380', 555, 'Airbus', 2015, 8300, 'Active'),
             (10006, 'Boeing 747', 416, 'Boeing', 2000, 7800, 'Active'),
             (10007, 'Bombardier CRJ', 90, 'Bombardier', 2003, 2300, 'Active'),
             (10008, 'Airbus A350', 314, 'Airbus', 2016, 5900, 'Active'),
             (10009, 'Boeing 777', 396, 'Boeing', 2006, 6100, 'Active'),
             (10010, 'Embraer E195', 120, 'Embraer', 2011, 3100, 'Active')],
            
            "Flight":
            [(20001, 'AA123', '2024-01-15 08:00:00', '2024-01-15 12:00:00', 'JFK',
              10001, 240, 'A1', 'On Time', 10001),
             (20002, 'DL456', '2024-01-16 09:00:00', '2024-01-16 11:30:00', 'LAX',
              10002, 210, 'B2', 'Delayed', 10002),
             (20003, 'UA789', '2024-01-17 07:00:00', '2024-01-17 13:00:00', 'ORD',
              10003, 360, 'C3', 'On Time', 10003),
             (20004, 'AA234', '2024-01-18 08:30:00', '2024-01-18 12:30:00', 'LAX',
              10004, 240, 'A2', 'On Time', 10004),
             (20005, 'DL567', '2024-01-19 09:45:00', '2024-01-19 11:00:00', 'MIA',
              10005, 150, 'B3', 'Cancelled', 10005),
             (20006, 'UA891', '2024-01-20 06:00:00', '2024-01-20 10:00:00', 'SFO',
              10006, 240, 'C1', 'On Time', 10006),
             (20007, 'AA345', '2024-01-21 07:00:00', '2024-01-21 13:00:00', 'ATL',
              10007, 360, 'A4', 'Delayed', 10007),
             (20008, 'DL678', '2024-01-22 08:00:00', '2024-01-22 12:00:00', 'SEA',
              10008, 240, 'B1', 'On Time', 10008),
             (20009, 'UA912', '2024-01-23 09:00:00', '2024-01-23 13:30:00', 'DEN',
              10009, 270, 'C2', 'Delayed', 10009),
             (20010, 'AA456', '2024-01-24 10:00:00', '2024-01-24 14:00:00', 'BOS',
              10010, 240, 'A5', 'On Time', 10010)],
            
            "Pilot":
            [(30001, 'John Doe', 'L12345', '1980-01-01', 15, 'Commercial', 1000),
             (30002, 'Jane Smith', 'L67890', '1975-03-12', 20, 'Private', 1500),
             (30003, 'Robert Brown', 'L54321', '1990-07-25', 10, 'Commercial', 800),
             (30004, 'Emily Davis', 'L98765', '1982-09-30', 18, 'Private', 1100),
             (30005, 'Michael Wilson', 'L13579', '1985-11-05', 12, 'Commercial', 900),
             (30006, 'Sarah Thompson', 'L97531', '1992-04-22', 8, 'Private', 700),
             (30007, 'David Clark', 'L24680', '1988-02-14', 14, 'Commercial', 1200),
             (30008, 'Emma Taylor', 'L86420', '1983-06-17', 17, 'Private', 1300),
             (30009, 'James White', 'L10293', '1981-10-10', 16, 'Commercial', 1400),
             (30010, 'Olivia Martin', 'L56478', '1987-08-08', 13, 'Private', 950)],

            "Destination":
            [(40001, 'John F. Kennedy International Airport', 'New York', 'USA'),
             (40002, 'Los Angeles International Airport', 'Los Angeles', 'USA'),
             (40003, 'Chicago O\'Hare International Airport', 'Chicago', 'USA'),
             (40004, 'Miami International Airport', 'Miami', 'USA'),
             (40005, 'San Francisco International Airport', 'San Francisco', 'USA'),
             (40006, 'Hartsfield-Jackson Atlanta International Airport', 'Atlanta', 'USA'),
             (40007, 'Seattle-Tacoma International Airport', 'Seattle', 'USA'),
             (40008, 'Denver International Airport', 'Denver', 'USA'),
             (40009, 'Boston Logan International Airport', 'Boston', 'USA'),
             (40010, 'Dallas/Fort Worth International Airport', 'Dallas', 'USA')],
            
            "Passenger":
            [(50001, 'Alice Walker', 'P1234567', 'alice@example.com', '123-456-7890'),
             (50002, 'Bob Johnson', 'P2345678', 'bob@example.com', '234-567-8901'),
             (50003, 'Charlie King', 'P3456789', 'charlie@example.com', '345-678-9012'),
             (50004, 'Diana Green', 'P4567890', 'diana@example.com', '456-789-0123'),
             (50005, 'Ethan Lee', 'P5678901', 'ethan@example.com', '567-890-1234'),
             (50006, 'Fiona Scott', 'P6789012', 'fiona@example.com', '678-901-2345'),
             (50007, 'George Baker', 'P7890123', 'george@example.com', '789-012-3456'),
             (50008, 'Hannah Young', 'P8901234', 'hannah@example.com', '890-123-4567'),
             (50009, 'Isaac Reed', 'P9012345', 'isaac@example.com', '901-234-5678'),
             (50010, 'Jack Cooper', 'P0123456', 'jack@example.com', '012-345-6789')]
        }
