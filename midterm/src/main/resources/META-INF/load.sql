-- id, name, opponent_id, skill, version
INSERT INTO team VALUES(1, 'Awesome Team', 'Java', 1, NULL);
INSERT INTO team VALUES(2, 'Baylor Team', 'Java', 1, 1);
INSERT INTO team VALUES(3, 'Half Filled Team', 'Python', 1, NULL);
INSERT INTO team VALUES(4, 'Empty Team', 'Python', 1, 3);

-- id, birthdate, led_team_id, name, team_id, version
INSERT INTO person VALUES(1, '1985-02-01 00:00:00.000', 'Will Smith', 1, 1, NULL);
INSERT INTO person VALUES(2, '1996-05-01 00:00:00.000', 'Adam Ragsdale', 1, NULL, 1);
INSERT INTO person VALUES(3, '1998-03-01 00:00:00.000', 'Arnold Palmer', 1, NULL, 1);
INSERT INTO person VALUES(4, '1997-02-01 00:00:00.000', 'Fred Johns', 1, NULL, 1);

INSERT INTO person VALUES(5, '1984-07-07 00:00:00.000', 'Archie Caldwell', 1, 2, NULL);
INSERT INTO person VALUES(6, '1996-03-26 00:00:00.000', 'Carol Taylor', 1, NULL, 2);
INSERT INTO person VALUES(7, '1998-05-03 00:00:00.000', 'Harold Hill', 1, NULL, 2);
INSERT INTO person VALUES(8, '1997-12-25 00:00:00.000', 'James Bartlett', 1, NULL, 2);

INSERT INTO person VALUES(9, '1990-01-05 00:00:00.000', 'Susan Jones', 1, 3, NULL);
INSERT INTO person VALUES(10, '2000-03-26 00:00:00.000', 'Robert Spence', 1, NULL, 3);

INSERT INTO person VALUES(11, '1990-01-05 00:00:00.000', 'Bill Nye', 1, NULL, NULL);
INSERT INTO person VALUES(12, '2000-03-26 00:00:00.000', 'Greg Johns', 1, NULL, NULL);


-- -- id, age, email, name, version
-- INSERT INTO team VALUES(1, 30, 'sam@houston.com', 'Sam Houston', 1);
-- INSERT INTO team VALUES(2, 24, 'kbarker@gmail.com', 'Kane Barker', 1);
-- INSERT INTO team VALUES(3, 56, 'otto@gmail.com', 'Otto von Bismark', 1);
-- INSERT INTO team VALUES(4, 19, 'rob@yahoo.net', 'Rob Robbie', 1);
-- INSERT INTO team VALUES(5, 41, 'jsmith@fake.com', 'John Smith', 1);
-- -- id, brand, license plate, owner, type, version
-- INSERT INTO person VALUES(1, 'Chevrolet', 'ABC 123', 'Silverado', 1, 1);
-- INSERT INTO person VALUES(2, 'Chevrolet', 'PRD 845', 'Suburban', 1, 1);
-- INSERT INTO person VALUES(3, 'Nissan', 'HKJ3456', 'Ultima', 1, 2);
-- INSERT INTO person VALUES(4, 'Toyota', 'BTN4377', 'Highlander', 1, 3);
-- INSERT INTO person VALUES(5, 'Ford', 'FGR5591', 'Focus', 1, 3);
-- INSERT INTO person VALUES(6, 'Ford', 'OOP9191', 'F-150', 1, 3);
-- INSERT INTO person VALUES(7, 'Lamborghini', 'LKL0987', 'Murcielago', 1, 4);
-- INSERT INTO person VALUES(8, 'Ford', 'MBT2048', 'Taurus', 1, 4);
-- INSERT INTO person VALUES(9, 'Hyundai', 'BVB6655', 'Sonata', 1, 5);
-- INSERT INTO person VALUES(10, 'Chevrolet', 'OMG4242', 'Blazer', 1, 5);
-- INSERT INTO person VALUES(11, 'Chevrolet', 'LNB1234', 'Traverse', 1, 5);
