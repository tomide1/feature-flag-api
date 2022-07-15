## Project for M****e Interview

# Steps to run application
- Download this [config file](https://drive.google.com/file/d/1eYm1EcosTGaiuRhb_iJObvrgvGy00_zj/view?usp=sharing), note the path to downloaded file
- Add the following environment variable - I'll recommend, if using IntelliJ, to edit the 'Run Configuration' then add the following to environment variables:
> GOOGLE_APPLICATION_CREDENTIALS=[replace with path to downloaded config file]
- Run the application

## Steps to make api calls to application
- Get an access token to this service following steps [here](https://github.com/tomide1/feature-flag-ui#readme)
- Import Postman Collection [here](https://documenter.getpostman.com/view/18788415/UVsHUTt8); outlines the endpoints for this service.
- Substitute Auth Bearer token with token from step 1

## Technologies Used:
- Firebase - For authentication/Security
- Firestore - Database
