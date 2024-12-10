---
name: Feature request
about: Suggest new feature
title: "feat: "
labels: ''
assignees: ''

---

### Title

*Example: Create Members GET API*

### Description

Description of the new feature and the problem it is solving.

*Example: Create Members GET API to retrieve a list of members from the file (database) repository.
GET
API* *does not modify data. It will also handle GET API Response Codes:*
*- HTTP response code 200 (OK)*
*- HTTP response code 404 (NOT FOUND)*
*- HTTP response code 400 (BAD REQUEST)*
*GET API will return all member's data from the repository*

### Expected behavior

Description of the expected behavior.

*Example:  When Client use GET to access members resources that are located at the specified URL on
the
server the server will respond with the list of members and all its data.*

### Solution

Describe the solution

*Ex: Use the Controller-Service-Repository pattern from Spring Boot applications. The Controller is
responsible for exposing the GET functionality to the client. The Repository is responsible for
storing and retrieving members data. The Service gets the request from the Controller and use
Repository functions to get data from the database.*

### Screenhoots

Add screenshots
