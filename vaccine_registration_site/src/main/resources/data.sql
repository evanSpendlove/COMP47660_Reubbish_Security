INSERT INTO users (name, surname, dob, pps, address, phone_number, email, nationality, password) VALUES ('admin', 'admin', '1900-01-01', '0', 'N/A', 'N/A', 'admin@hse.ie', 'irish', '$2a$10$RCr../maQgaq24SD9xVPC..dX5BwAfN.dnTXuuBMA3W.0qg5JEg5m');
INSERT INTO users (name, surname, dob, pps, address, phone_number, email, nationality, password) VALUES ('staff', 'staff', '1900-01-01', '1', 'N/A', '0', 'staff@hse.ie', 'irish', '$2a$10$PA3CZJ0/s21xyph1reHhnO309htdArBVkUsf83IpNz/3uaUHvHDzK');
INSERT INTO users (name, surname, dob, pps, address, phone_number, email, nationality, password) VALUES ('vaccinator', 'vaccinator', '1900-01-01', '2', 'N/A', '00', 'vaccinator@hse.ie', 'irish', '$2a$10$WgASl3pXw5UKSLe94HAvi.LGj3vWwF9NVO1MOWoYZogxmYW2kR/q6');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('STAFF');
INSERT INTO roles (name) VALUES ('VACCINATOR');
INSERT INTO user_roles(user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles(user_id, role_id) VALUES (2, 3);
INSERT INTO user_roles(user_id, role_id) VALUES (3, 4);
