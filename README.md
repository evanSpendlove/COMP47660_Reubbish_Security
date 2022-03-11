# COMP47660_Reubbish_Security

## How to run
Prerequisites are docker, docker-compose {3 or greater} and maven installed with your Java Home set.

Java version must be 11

Navigate to vaccine_registration_site folder and run 
```
chmod +x refresh.sh
./refresh.sh
```
Note: if communication link error, SQL container is taking to long to start, run following commands:
```
docker-compose down
docker-compose up -d mysqldb
<Wait approximately 2-3 minutes>
./refresh.sh
```

## User Roles
There are 4 user roles: 
 - Admin - Can change user roles using localhost:8080/update-roles
 - Staff - Can respond to forum threads
 - Vaccinator - Can update user vaccination status in vaccine portal
 - User - Can create threads in forum and book themselves appointments via vaccine portal, can cancel appointments and reschedule once cancelled.

## Test Login Credentials
We have created some test users {username, password, roles}:
 - 0, admin, ADMIN
 - 1, staff, STAFF
 - 2, vaccinator, VACCINATOR
 - 3, user, USER

We also created some other fake data for statistics page.

## Note
2nd dose appointments are automatically generated once vaccinator has updated that a user has received their first dose. Also, if a user tries to book an appointment which violates the outlined rules (<3 weeks, etc) then it will fail and redirect to index with no new appointment.

## Services
- User Management [Evan] 
- Forum Page [Evan] 
- Vaccination Portal [Primarily: Reuben | Also: Evan / John] 
- Statistics [John] 

