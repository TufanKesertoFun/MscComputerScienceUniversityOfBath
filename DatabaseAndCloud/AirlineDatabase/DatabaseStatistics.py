import os

class DatabaseStatistics:

    def __init__(self, db_connection):
        self.conn = db_connection
        self.cursor = self.conn.cursor()

    def total_tables(self):
        """Calculate the total number of tables in the database."""
        self.cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
        tables = self.cursor.fetchall()
        table_count = len(tables)
        print(f"Total number of tables: {table_count}")
        return table_count, tables

    def total_rows(self, tables):
        """Calculate the total number of rows (records) across all tables."""
        total_rows = 0
        for table in tables:
            table_name = table[0]
            self.cursor.execute(f"SELECT COUNT(*) FROM {table_name}")
            row_count = self.cursor.fetchone()[0]
            total_rows += row_count

        print(f"Total number of rows across all tables: {total_rows}")
        return total_rows

    def largest_table(self, tables):
        """Find the table with the largest number of rows."""
        largest_table = None
        max_rows = 0

        for table in tables:
            table_name = table[0]
            self.cursor.execute(f"SELECT COUNT(*) FROM {table_name}")
            row_count = self.cursor.fetchone()[0]
            if row_count > max_rows:
                max_rows = row_count
                largest_table = table_name

        print(f"Largest table by row count: {largest_table} ({max_rows} rows)")
        return largest_table, max_rows

    def smallest_table(self, tables):
        """Find the table with the smallest number of rows."""
        smallest_table = None
        min_rows = float('inf')

        for table in tables:
            table_name = table[0]
            self.cursor.execute(f"SELECT COUNT(*) FROM {table_name}")
            row_count = self.cursor.fetchone()[0]
            if row_count < min_rows:
                min_rows = row_count
                smallest_table = table_name

        print(f"Smallest table by row count: {smallest_table} ({min_rows} rows)")
        return smallest_table, min_rows

    def average_rows_per_table(self, total_rows, total_tables):
        """Calculate the average number of rows per table."""
        if total_tables > 0:
            avg_rows = total_rows / total_tables
        else:
            avg_rows = 0
        print(f"Average number of rows per table: {avg_rows:.2f}")
        return avg_rows

    def database_size(self, db_file_path):
        """Print the size of the database on disk."""
        if os.path.exists(db_file_path):
            db_size = os.path.getsize(db_file_path) / (1024 * 1024)  # Convert bytes to MB
            print(f"Database size on disk: {db_size:.2f} MB")
        else:
            print(f"Database file {db_file_path} not found.")

    def calculate_summary_statistics(self, db_file_path):
        """Calculate and display all summary statistics about the database."""
        print("\nDatabase Summary Statistics:")

        total_tables_count, tables = self.total_tables()
        total_rows_count = self.total_rows(tables)
        self.largest_table(tables)
        self.smallest_table(tables)
        self.average_rows_per_table(total_rows_count, total_tables_count)
        self.database_size(db_file_path)
