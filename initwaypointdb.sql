DROP TABLE waypoint;

CREATE TABLE waypoint (
  name TEXT,
  address TEXT,
  category TEXT,
  elevation DECIMAL(8,3),
  latitude DECIMAL(8,6),
  longitude DECIMAL(9,6),
  PRIMARY KEY (name)
);

INSERT INTO waypoint VALUES
  ('Machu Picchu', 'Aguas Calientes, Peru', 'TRAVEL', 7972, -13.163141, 72.544963),
  ('Mount Everest', 'Nepal', 'HIKING', 29029, 27.984286, 86.927217),
  ('ASU-Tempe', 'Tempe, Arizona', 'SCHOOL', 1200, 33.417959, -111.934397);