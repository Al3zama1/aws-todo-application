# AWS Todo Application

## Description
Developed Todo application where users can create todos and collaborate with other users by sharing
todos with each other. 

## Application Features
- **Registration and Login:** Spring security is used in conjunction with Amazon Cognito
to achieve user authentication and authorization.
- **CRUD Operations**: Users are able to create, view, and delete todos.
- **Share todos and Email Notifications:** Users are able to share todos with other application users through email
notifications that are sent via Amazon Simple Email Service (SES) and Amazon Simple Queue Service (SQS).
- **Push Notifications:** Todo owners get notified in the browser in real time when a collaboration request for a particular todo was 
accepted by the invited user. This is achieved using WebSockets and a managed Apache ActiveMQ message broker running
on Amazon MQ.

## Technologies
- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- Amazon Web Services
- AWS CDK, SES, Cognito, RDS, SQS, ECS, and ECR


