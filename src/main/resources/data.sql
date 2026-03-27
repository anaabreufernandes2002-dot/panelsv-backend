-- USERS com senha {noop}* (sem encoder)
INSERT INTO users (id, username, password, role) VALUES
                                                     (1, 'admin',  'admin123', 'ADMIN'),
                                                     (2, 'ana',    'ana123',   'SELLER'),
                                                     (3, 'bianca', 'bianca123','SELLER'),
                                                     (4, 'fab',    'fab123',   'FABRICATION');

-- JOBS demo
INSERT INTO jobs (id, vendor, seller, customer, material, status, install_date, notes) VALUES
                                                                                           (1, '-', 'Ana',    'Fisher', 'Taj Mahal Quartzite', 'located', DATE '2025-11-20', 'myredge'),
                                                                                           (2, '-', 'Bianca', 'Smith',  'White Ice Granite',   'cut',     DATE '2025-11-22', 'special order');
