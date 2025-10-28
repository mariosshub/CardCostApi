# CardCostApi

**CardCostApi** is a Spring Boot service designed to determine the clearing cost applied to a payment card transaction based on the card’s Bank Identification Number (BIN).

 ## Features
 - BIN Lookup: Retrieves the issuing country of a payment card by querying an external BIN Lookup API [binTable](https://bintable.com/get-api) using the first six digits of the card number.
 - Clearing Cost Resolution: Determines the applicable clearing cost by referencing an internal clearing-cost table, which maps country codes to their respective costs.
 - CRUD operations on the clearing-cost table.
   
## Technologies Used
- Language: Java 21
- Maven: For building the application and depedency management
- Framework: Spring Boot
- Caffeine Cache: Used to efficiently cache BIN lookup results and improve performance
- RestTemplate: Spring’s built-in HTTP client for making external API calls to the BIN lookup service
- PostgreSQL Database: Used as the primary database for persisting the clearing-cost table
- H2 Databse: An in-memory database utilized for integration and unit testing
- Docker: For containerization

## Endpoints
### Resolve card cost
- POST on `/api/payment-cards-cost`
- Request Body
```bash
{
    card_number: "4242424242424242"
}
```
- Response (200)
```bash
{
    "status": 200,
    "message": "SUCCESS",
    "data": {
        "country": "US",
        "cost": 5.00
     }
}
```
- Response (400) Bad Request Validation Failed
```bash
{
    "status": 400,
    "message": "Validation Failed",
    "data": {
        "card_number": "PAN number must be 8 to 19 digits"
     }
}
```
- Response (404) Not Found
```bash
{
    "status": 404,
    "message": "No clearing cost found",
    "data": null
}
```
- Response (404) Not Found (If there is an error from bin lookup service)
```bash
{
    "status": 404,
    "message": "No country code found from BIN number lookup",
    "data": null
}
```
- Response (503) Service Unavailable (Bin lookup api overloaded or down)
```bash
{
    "status": 503,
    "message": "Bin lookup api connection timeout or network error",
    "data": null
}
```

### CRUD for clearing costs
#### Create clearing cost
- POST on `/api/clearingCosts`
- Request Body
```bash
{
    "countryCode": "GR",
    "cost": "5.00"
}
```
- Response (201) Created
```bash
{
    "status": 201,
    "message": "Successfully created clearing cost"
}
```
#### Read all clearing costs
- GET on `/api/clearingCosts`
- Response (200) OK
```bash
{
    "status": 200,
    "message": "SUCCESS",
    "data": [
        {
            "country": "US",
            "cost": 5.00
        },
        {
            "country": "GR",
            "cost": 10.00
        }
    ]
}
```
#### Read clearing cost by given countryCode
- GET on `/api/clearingCosts/GR`
- Response (200) OK
```bash
{
    "status": 200,
    "message": "SUCCESS",
    "data": {
        "countryCode": "US",
        "cost": 5.00
     }
}
```
- Response (404) Not Found
```bash
{
    "status": 404,
    "message": "No such country code found",
    "data": null
}
```
#### Update clearing cost
- PUT on `/api/clearingCosts`
- Request body
```bash
{
    "countryCode": "GR",
    "cost": "5.00"
}
```
- Response (201) Created
```bash
{
    "status": 201,
    "message": "Successfully updated clearing cost",
    "data": null
}
```
#### Delete a clearing cost by countryCode
- DELETE on `/api/clearingCosts/US`
- Response (200) OK
```bash
{
    "status": 200,
    "message": "Successfully deleted clearing cost",
    "data": null
}
```
- Response (404) Not Found
```bash
{
    "status": 404,
    "message": "No such country code found",
    "data": null
}
```

## Configuration (environment variables)
The application reads configuration values from an .env file inside project's main directory.
These configurations are used in application.properties and docker-compose.yaml file.

### .env ###
```bash
SPRING_DATASOURCE_URL: <jdbc_datasource_url>
SPRING_DATASOURCE_USERNAME: <db_username>
SPRING_DATASOURCE_PASSWORD: <db_password>
SPRING_DB_NAME: <db_name>
BIN_API_URL: <bin_api_url>
BIN_API_KEY: <bin_api_key>
```
## Run with docker
1. Have Docker installed in your system
2. Clone this repository and navigate to the main project directory
3. Make sure you create .env file
```plaintext
CardCostApi
├── src
├── .env
├── docker-compose.yaml
├── Dockerfile
└── pom.xml
```
4. Run `docker-compose up --build`
```
- Builds the card cost api image from the DockerFile run all tests and expose the api at `http://localhost:8080`
- Starts a PostgreSQL image and configures the clearing cost Database
- Starts adminer an interface for managing the PostgreSQL
```
## Author
Marios Stamatopoulos

