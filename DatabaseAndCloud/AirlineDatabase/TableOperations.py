from DDL import DDL
from DML import DML
from TableDefinitions import TableDefinitions
from Validator import Validator


class TableOperations:

    def __init__(self, db_connection):
        self.db_conn = db_connection
        self.ddl = DDL(db_connection)
        self.dml = DML(db_connection)
        self.table_definitions = TableDefinitions().table_definitions
        self.validator = Validator(self.db_conn)

    def get_table_columns(self, table_name):
        if self.validator.table_exists(table_name):
            conn = self.db_conn.connect()
            cursor = conn.cursor()
            cursor.execute(f"PRAGMA table_info({table_name})")
            columns_info = cursor.fetchall()
            print(f"\nColumns in {table_name}:")
            for idx, col in enumerate(columns_info):
                print(f"{idx+1}. Column: {col[1]}, Type: {col[2]}")
            return columns_info
        else:
            print(f"Table {table_name} does not exist.")
            return None

    def get_row_by_id(self, table_name, id_value):
        """Retrieve a row by its ID."""
        if self.validator.table_exists(table_name):
            cursor = self.db_conn.cursor()
            primary_key_column = self.get_table_columns(table_name)[0][1]
            cursor.execute(f"SELECT * FROM {table_name} WHERE {primary_key_column} = ?", (id_value,))
            row = cursor.fetchone()
            return row
        else:
            print(f"Table {table_name} does not exist.")
            return None

    def display_row(self, table_name, row):
        """Display a row in a readable format."""
        columns_info = self.get_table_columns(table_name)
        if row:
            print(f"\nCurrent row data for {table_name}:")
            for col_info, value in zip(columns_info, row):
                print(f"{col_info[1]} ({col_info[2]}): {value}")
        else:
            print("No data found for the given ID.")

    def perform_operations(self, table_name):
        while True:
            print(f"\nOperations for {table_name}:")
            print("1. Create Table")
            print("2. Drop Table")
            print("3. Insert Data into Table")
            print("4. Insert 10 rows of Mock Data")
            print("5. Update Table")
            print("6. Delete from Table")
            print("7. View Table")
            print("8. Back to Main Menu")

            try:
                choice = int(input("Enter your choice: "))
            except ValueError:
                print("Invalid input! Please enter a number. ")
                continue

            if choice == 1:
                self.ddl.create_table(table_name, self.table_definitions[table_name])
            elif choice == 2:
                self.ddl.drop_table(table_name)
            elif choice == 3:
                columns_info = self.get_table_columns(table_name)
                if columns_info:
                    print("Enter values for the following columns:")
                    values = []
                    for col in columns_info:
                        col_name = col[1]
                        col_type = col[2]
                        value = input(f"Enter value for {col_name} ({col_type}): ")
                        values.append(f"{col_name}={value}")
                    data_dict = dict(item.split('=') for item in values)
                    self.dml.insert_data(table_name, data_dict)
            elif choice == 4:
                self.dml.insert_mock_data(table_name)
            elif choice == 5:
                # Step 1: Ask which row (based on ID) they want to update
                id_value = input(f"Enter the {table_name} ID you want to update: ")

                # Step 2: Fetch and display the current row
                current_row = self.get_row_by_id(table_name, id_value)
                if current_row:
                    self.display_row(table_name, current_row)

                    # Step 3: Show available columns and let the user choose which one to update
                    columns_info = self.get_table_columns(table_name)
                    column_choice = int(input("Select the column number you want to update: "))
                    col_name = columns_info[column_choice - 1][1]  # Get the column name
                    col_type = columns_info[column_choice - 1][2]  # Get the column type

                    # Step 4: Ask for the new value
                    new_value = input(f"Enter the new value for {col_name} ({col_type}): ")

                    # Step 5: Perform the update
                    set_clause = f"{col_name} = '{new_value}'"
                    condition = f"{columns_info[0][1]} = {id_value}"  # Use the primary key as condition
                    self.dml.update_data(table_name, set_clause, condition)

                    # Step 6: Fetch and display the updated row
                    updated_row = self.get_row_by_id(table_name, id_value)
                    self.display_row(table_name, updated_row)
                else:
                    print(f"No row found for {table_name} ID: {id_value}")

            elif choice == 6:
                # Step 1: Show the columns
                columns_info = self.get_table_columns(table_name)

                # Step 2: Ask the user to select a row for deletion based on the primary key
                primary_key_column = columns_info[0][1]
                id_value = input(f"Enter the {primary_key_column} of the row you want to delete: ")

                # Step 3: Fetch and display the row to confirm deletion
                row_to_delete = self.get_row_by_id(table_name, id_value)
                if row_to_delete:
                    self.display_row(table_name, row_to_delete)
                    confirmation = input("Are you sure you want to delete this row? (y/n): ")
                    if confirmation.lower() == 'y':
                        condition = f"{primary_key_column} = {id_value}"
                        self.dml.delete_data(table_name, condition)
                    else:
                        print("Deletion cancelled.")
                else:
                    print(f"No row found for {primary_key_column} = {id_value}.")

            elif choice == 7:
                self.dml.select_all(table_name)
            elif choice == 8:
                break
            else:
                print("Invalid choice. Please try again.")
