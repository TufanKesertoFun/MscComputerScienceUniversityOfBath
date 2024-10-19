from Validator import Validator
from MockData import MockData
import sqlite3

class DML:
    def __init__(self, db_connection):
        self.conn = db_connection.connect()
        self.validator = Validator(self.conn)
        self.mock_data = MockData().mock_data

    def insert_data(self, table_name, data):
        try:
            if self.validator.table_exists(table_name):
                # Get column info to validate types
                cursor = self.conn.cursor()
                cursor.execute(f"PRAGMA table_info({table_name})")
                columns_info = cursor.fetchall()

                # Prepare to store valid data
                valid_data = {}

                for col_info in columns_info:
                    col_name = col_info[1]
                    col_type = col_info[2]

                    # Check if the user has provided data for this column
                    if col_name in data:
                        input_value = data[col_name]
                        is_valid, typed_value = self.validator.validate_input(input_value, col_type)

                        # If input is valid, store the typed value
                        if is_valid:
                            valid_data[col_name] = typed_value
                        else:
                            print(f"Invalid input for column {col_name} ({col_type}). Please try again.")
                            return

                # Prepare SQL query
                columns = ', '.join(valid_data.keys())
                placeholders = ', '.join('?' for _ in valid_data)
                sql = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders})"

                # Execute the query with validated data
                cursor.execute(sql, list(valid_data.values()))
                self.conn.commit()
                print(f"Data inserted into {table_name} successfully.")
            else:
                print(f"Table {table_name} does not exist.")
        except sqlite3.Error as e:
            print(f"Error inserting data into {table_name}: {e}")



    def insert_mock_data(self, table_name):
        if self.validator.table_exists(table_name):
            cursor = self.conn.cursor()
            mock_data_rows = self.mock_data[table_name]
            for row in mock_data_rows:
                placeholders = ', '.join('?' for _ in row)
                sql = f"INSERT INTO {table_name} VALUES ({placeholders})"
                cursor.execute(sql, row)
            self.conn.commit()
            print(f"Mock data inserted into {table_name} successfully.")
        else:
            print(f"Table {table_name} does not exist.")

    def update_data(self, table_name, set_clause, condition):
        """Attempt to update data in a table with proper error handling."""
        try:
            cursor = self.conn.cursor()
            update_query = f"UPDATE {table_name} SET {set_clause} WHERE {condition}"
            cursor.execute(update_query)
            if cursor.rowcount == 0:
                print("No rows updated; check your condition.")
            else:
                print(f"{cursor.rowcount} rows updated successfully.")
            self.conn.commit()
        except sqlite3.IntegrityError as e:
            print(f"Error updating data: {e}")
        except Exception as e:
            print(f"Unexpected error: {e}")

    def delete_data(self, table_name, id_column):
        if self.validator.table_exists(table_name):
            id_value = input(f"Enter the {id_column} of the row you want to delete: ")
            if not id_value.isdigit():
                print(f"Invalid {id_column}. It must be a number.")
                return

            cursor = self.conn.cursor()
            sql = f"DELETE FROM {table_name} WHERE {id_column} = ?"
            cursor.execute(sql, (id_value,))
            self.conn.commit()

            if cursor.rowcount > 0:
                print(f"Row with {id_column} = {id_value} deleted from {table_name} successfully.")
            else:
                print(f"No row found with {id_column} = {id_value} in {table_name}.")

    def select_all(self, table_name):
        if self.validator.table_exists(table_name):
            cursor = self.conn.cursor()
            cursor.execute(f"SELECT * FROM {table_name}")
            rows = cursor.fetchall()
            print(f"-------{table_name}-------")
            for row in rows:
                print(row)
