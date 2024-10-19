class Validator:

    def __init__(self, db_connection):
        self.db_connection = db_connection

    def table_exists(self, table_name):
        """Check if a table exists in the database."""
        cursor = self.db_connection.cursor()
        query = f"SELECT name FROM sqlite_master WHERE type='table' AND name='{table_name}';"
        cursor.execute(query)
        result = cursor.fetchall()
        return bool(result)  # Returns True if table exists, False otherwise

    def validate_insert(self, table_name, values, non_nullable_columns):
        """Validate non-nullable columns."""
        for idx, col in enumerate(non_nullable_columns):
            if not values[idx]:
                raise ValueError(f"Column {col} cannot be null.")
        return True  # Further validation logic can be added here

    def validate_input(self, value, col_type):
        """Validate the input value based on the column type."""
        if col_type.startswith("INT"):
            return value.isdigit(), int(value) if value.isdigit() else None
        elif col_type.startswith("VARCHAR") or col_type.startswith("TEXT"):
            return True, value  # Accept any input for VARCHAR or TEXT
        else:
            return False, None  # Unsupported type for now
