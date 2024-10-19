from Validator import Validator


class DDL:

    def __init__(self, db_connection):
        self.conn = db_connection.connect()
        self.validator = Validator(self.conn)

    def create_table(self, table_name, sql):
        if not self.validator.table_exists(table_name):
            cursor = self.conn.cursor()
            cursor.execute(sql)
            self.conn.commit()
            print(f"Table {table_name} created successfully.")
        else:
            print(f"Table {table_name} already exists.")

    def drop_table(self, table_name):
        if self.validator.table_exists(table_name):
            cursor = self.conn.cursor()
            cursor.execute(f"DROP TABLE {table_name}")
            self.conn.commit()
            print(f"Table {table_name} dropped successfully.")
        else:
            print(f"Table {table_name} does not exist.")
