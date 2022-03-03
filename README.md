# COMP47660_Reubbish_Security

## Services
- User Management [Evan] [DONE]
- Forum Page [Evan] [DONE]
- Vaccinations [Reuben]
- Statistics [John]

## Task Breakdown
- Registered users can book a vaccination appointment [Reuben]
    - Book Your Vaccination page
    - Vaccination Database
    - Input validation

- HSE staff who are in charge of administering the vaccination can update vaccination info about a user [Reuben]
    - Vaccine-Admin page
    - Backend wiring to allow updates to patients

- Any user should be able to visualise aggregated statistics [John]
    - Question: Does "any user" include anonymous, non-logged-in users?
    - Stats DB
    - Stats page
    - Backend calculation functions

## Complete
- Any user should be able to ask questions in a public forum [Evan] [DONE]
    - Forum page [DONE]
    - New Thread page [DONE]
    - Thread page [DONE]
    - Forum post & comment DB [DONE]
- HSE Staff can answer questions that users ask in the forum [Evan] [DONE]
    - User restrictions for answering questions based on role [DONE]
    - Frontend update to include comments [DONE]
- Users can register to the Vaccination System [Evan] [DONE]
    - Register page [DONE]
    - User database [DONE]
- Registered users can login/logout [Evan] [DONE]
    - Login page [DONE]
    - Logout button [DONE]
    - Configure Spring Security [DONE]
    - Break page into template fragments using th:fragment [DONE]
- Registered users should be able to retrieve a record of their last activity [Evan] [DONE]
    - For each user, we should store last activity in DB [DONE]
    - Wire into backend [DONE]
