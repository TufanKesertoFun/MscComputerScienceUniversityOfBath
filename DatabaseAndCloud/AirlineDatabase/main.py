from DatabaseConnection import DatabaseConnection
from TableDefinitions import TableDefinitions
from MockData import MockData
from TableOperations import TableOperations
from DatabaseStatistics import DatabaseStatistics

def main():
    db_conn = DatabaseConnection()  # Initialize the database connection
    table_operations = TableOperations(db_conn)  # Initialize TableOperations with db connection
    table_definitions = TableDefinitions().table_definitions  # Retrieve table definitions
    mock_data = MockData().mock_data  # Initialize MockData
    db_summary = DatabaseStatistics(db_conn)  # Initialize DatabaseStatistics

    while True:
        print("\nMain Menu:")
        print("1. Drop All Tables")
        print("2. Create All Tables")
        print("3. Insert Mock Data to All Tables")
        print("4. Select All Tables")
        print("5. Table-specific Operations")
        print("6. Calculate Database Statistics")
        print("7. Exit")

        try:
            choice = int(input("Enter your choice: "))
        except ValueError:
            print("Invalid input! Please enter a number.")
            continue

        if choice == 1:
            # Drop all tables
            for table in table_definitions:
                table_operations.ddl.drop_table(table)
        elif choice == 2:
            # Create all tables
            for table, sql in table_definitions.items():
                table_operations.ddl.create_table(table, sql)
        elif choice == 3:
            # Insert mock data into all tables
            for table in mock_data:
                table_operations.dml.insert_mock_data(table)

        elif choice == 4:
            # Select all tables
            for table in table_definitions:
                table_operations.dml.select_all(table)

        elif choice == 5:
            # Table-specific operations
            print("\nSelect a table for specific operations:")
            for idx, table in enumerate(table_definitions.keys(), start=1):
                print(f"{idx}. {table}")
            table_choice = int(input("Enter table number: "))
            selected_table = list(table_definitions.keys())[table_choice - 1]
            table_operations.perform_operations(selected_table)

        elif choice == 6:
            # Calculate database statistics
            db_summary.calculate_summary_statistics("AirlineDB.db")

        elif choice == 7:
            # Exit the program
            db_conn.close()  # Close the database connection
            break
        else:
            print("Invalid choice. Please select again.")


if __name__ == "__main__":
    main()
