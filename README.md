# COMP47660_Reubbish_Security

## Services
- User Management [Evan]
- Vaccinations [Reuben]
- Statistics [John]
- Forum Page [Whoever finishes their part first]

## Task Breakdown
- Users can register to the Vaccination System [Evan] [DONE]
    - Register page [DONE]
    - User database [DONE]
- Registered users can login/logout [Evan] [TODO]
    - Login page [DONE]
    - Logout button [DONE]
    - Configure Spring Security [DONE]
    - Break page into template fragments using th:fragment [DONE]
- Registered users should be able to retrieve a record of their last activity [Evan]
    - For each user, we should store last activity in DB [DONE]
    - Wire into backend [DONE]
    - Make frontned user page and wire this into it [TODO]
- Registered users can book a vaccination appointment [Reuben]
    - Book Your Vaccination page
    - Vaccination Database
    - Input validation
- HSE staff who are in charge of administering the vaccination can update vaccination info about a user [Reuben/Evan]
    - Vaccine-Admin page
    - Backend wiring to allow updates to patients
- Any user should be able to visualise aggregated statistics [John]
    - Question: Does "any user" include anonymous, non-logged-in users?
    - Stats DB
    - Stats page
    - Backend calculation functions
- Any user should be able to ask questions in a public forum
    - Forum page
    - Forum post & comment DB
- HSE Staff can answer questions that users ask in the forum
    - User restrictions for answering questions based on role
    - Frontend update to include comments
