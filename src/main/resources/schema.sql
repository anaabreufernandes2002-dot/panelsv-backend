DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS job;

CREATE TABLE user (
                      id          INTEGER PRIMARY KEY AUTOINCREMENT,
                      username    TEXT NOT NULL UNIQUE,
                      password    TEXT NOT NULL,
                      role        TEXT NOT NULL
);

CREATE TABLE job (
                     id           INTEGER PRIMARY KEY AUTOINCREMENT,
                     contractor   TEXT NOT NULL,
                     customer     TEXT NOT NULL,
                     material     TEXT NOT NULL,
                     stage        TEXT NOT NULL
                         CHECK (stage IN (
                                          'TO_BE_LOCATED',
                                          'LOCATED',
                                          'PROGRAMMED',
                                          'CUT',
                                          'POLISHED'
                             )),
                     install_date TEXT,
                     notes        TEXT,
                     completed    INTEGER NOT NULL DEFAULT 0
);
