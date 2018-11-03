# Insurance Blocking

This is the blocking version of the Insurance sample application.

See insurance-reactive project for the reactive version.

It is based on Spring Boot 2, Spring 5, Spring Data, Spring Security 5, MongoDB.

Run with `mvn spring-boot:run`.

Application URL (also see Postman configuration file in the `postman` directory):
- <http://localhost:8081/init> to initialize the data
- <http://localhost:8081/companies> to list the insurance companies (this URL is secured)
- <http://localhost:8081/quote/1> to get the quote for a given company
- <http://localhost:8081/bestQuote> to get the best quote from registered companies
