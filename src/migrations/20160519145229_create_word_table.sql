CREATE TABLE words (
  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  value TEXT NOT NULL,
  quantity INTEGER NOT NULL,
  page_id INTEGER NOT NULL,
  FOREIGN KEY(page_id) REFERENCES pages(id)
  );