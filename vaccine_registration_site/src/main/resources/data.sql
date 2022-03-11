INSERT INTO users (id, name, surname, dob, pps, address, phone_number, email, nationality, password, lastactivity, gender) VALUES (100, 'admin', 'admin', '1990-01-01', '0', 'N/A', 'N/A', 'admin@hse.ie', '0', '$2a$10$RCr../maQgaq24SD9xVPC..dX5BwAfN.dnTXuuBMA3W.0qg5JEg5m', 4,0);
INSERT INTO users (id, name, surname, dob, pps, address, phone_number, email, nationality, password, lastactivity, gender) VALUES (1, 'staff', 'staff', '1980-01-01', '1', 'N/A', '0', 'staff@hse.ie', '2', '$2a$10$PA3CZJ0/s21xyph1reHhnO309htdArBVkUsf83IpNz/3uaUHvHDzK', 4,1);
INSERT INTO users (id, name, surname, dob, pps, address, phone_number, email, nationality, password, lastactivity, gender) VALUES (2, 'vaccinator', 'vaccinator', '1990-01-01', '2', 'N/A', '00', 'vaccinator@hse.ie', '20', '$2a$10$WgASl3pXw5UKSLe94HAvi.LGj3vWwF9NVO1MOWoYZogxmYW2kR/q6', 4,2);
INSERT INTO users (id, name, surname, dob, pps, address, phone_number, email, nationality, password, lastactivity, gender) VALUES (3, 'user', 'user', '2002-01-01', '3', 'N/A', '000', 'user@hse.ie', '10', '$2a$10$YsUF7gdAsfP7JGdFM0zyJub/xE2BQQMUN938LzE5j61dw.LQszjZS', 4,3);
INSERT INTO users (id, name, surname, dob, pps, address, phone_number, email, nationality, password, lastactivity, gender) VALUES (4, 'user2', 'user2', '2012-01-01', '4', 'N/A', '002', 'user2@hse.ie', '12', '$2a$10$YsUF7gdAsfP7JGdFM0zyJub/xE2BQQMUN938LzE5j61dw.LQszjZS', 1,4);
INSERT INTO users (id, name, surname, dob, pps, address, phone_number, email, nationality, password, lastactivity, gender) VALUES (5, 'user3', 'user3', '2016-01-01', '5', 'N/A', '003', 'user3@hse.ie', '15', '$2a$10$YsUF7gdAsfP7JGdFM0zyJub/xE2BQQMUN938LzE5j61dw.LQszjZS', 2,2);
INSERT INTO users (id, name, surname, dob, pps, address, phone_number, email, nationality, password, lastactivity, gender) VALUES (6, 'user4', 'user4', '1996-01-01', '6', 'N/A', '004', 'user4@hse.ie', '14', '$2a$10$YsUF7gdAsfP7JGdFM0zyJub/xE2BQQMUN938LzE5j61dw.LQszjZS', 3,1);
INSERT INTO users (id, name, surname, dob, pps, address, phone_number, email, nationality, password, lastactivity, gender) VALUES (7, 'user5', 'user5', '1991-01-01', '7', 'N/A', '005', 'user5@hse.ie', '5', '$2a$10$YsUF7gdAsfP7JGdFM0zyJub/xE2BQQMUN938LzE5j61dw.LQszjZS', 4,0);
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('STAFF');
INSERT INTO roles (name) VALUES ('VACCINATOR');
INSERT INTO user_roles(user_id, role_id) VALUES (100, 1);
INSERT INTO user_roles(user_id, role_id) VALUES (2, 3);
INSERT INTO user_roles(user_id, role_id) VALUES (2, 4);
INSERT INTO user_roles(user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (3, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (4, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (5, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (6, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (7, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (100, 2);
INSERT INTO appointments(date, time, available, vaccination_center, appointment_type, complete, user_id) VALUES ('10/3/2022', '9:00', false, 1, 1, false, 4);
INSERT INTO appointments(date, time, available, vaccination_center, appointment_type, complete, user_id) VALUES ('10/3/2022', '9:15', false, 2, 2, false, 1);
INSERT INTO appointments(date, time, available, vaccination_center, appointment_type, complete, user_id) VALUES ('10/3/2022', '9:30', false, 2, 2, false, 6);
