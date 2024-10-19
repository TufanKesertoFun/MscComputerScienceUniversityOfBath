import sqlite3


class DatabaseConnection:

  def __init__(self, db_name="AirlineDB.db"):
    self.db_name = db_name
    self.conn = None

  def connect(self):
    self.conn = sqlite3.connect(self.db_name)
    return self.conn

  def close(self):
    if self.conn:
      self.conn.close()

  def cursor(self):
    return self.conn.cursor()  # Correctly return the cursor object
