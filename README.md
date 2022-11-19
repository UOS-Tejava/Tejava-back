# 🍽️ Tejava (2022, Software Engineering course project)


![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=Java&logoColor=white)
![Spring-Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=Spring-Boot&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-FFCC00?style=flat-square&logo=Swagger&logoColor=white)
![EC2](https://img.shields.io/badge/Amazon_EC2-2496ED?style=flat-square&logo=Amazon-EC2&logoColor=white)
![RDS](https://img.shields.io/badge/Amazon_RDS-4285F4?style=flat-square&logo=Amazon-RDS&logoColor=white)

This is an application that provides food ordering service to its users.

## 📖 Explanation

Garfield, the owner of Mr. DaeBak Restaurant, came to think that he wanted to run a restaurant online as well.   
As a result, He wanted to outsource and create custom web pages.  

Accordingly, UOS-Tejava team has developed a suitable service. It provides ordering and delivery services, and customer can freely modify options or menu styles after shopping or ordering. Customer can order menus by voice recognition and non-members can also use the service.


## 🖥️ UI Design
[Figma Link](http://www.google.co.kr](https://www.figma.com/file/8YPLCXj4B1yUX7FXvgCyKG/%EC%86%8C%EA%B3%B5-%EB%8D%B0%EC%9E%90%EC%99%80?node-id=0%3A1&t=WrwlcREh4TcUvzIH-1))


## 💎 Main Features

- HTTP REST API Server configured With Spring Boot Application.
- Implemented an interceptor to check whether the user is logged in or not.
- Deploying using Amazon EC2 instances with swap memory implemented.
- DB management using Amazon RDS.


## 📐 Deployment/Diagram

<center><img src="https://user-images.githubusercontent.com/43805087/202497724-762edb81-a533-4ac6-8ec7-2e2b6c37ec64.png" width="900" height="700"></center>

- Build with Gradle.
- Build Jar file and deploy it in Amazon EC2 system.
- Use Amazon RDS to manage DB easily.


## 🖥️ Build Environment

This project uses Gradle & Amazon EC2 system.  
To build and run this project, first build `.jar` with Gradle, remote access to Amazon EC2 server using FileZilla and move the jar file.

<center><img width="250" height="300" alt="스크린샷 2022-11-18 오전 1 25 09" src="https://user-images.githubusercontent.com/43805087/202501861-29510868-65f0-41ce-942c-84d0ce5af4d4.png"></center>

Make sure check if tejava-0.0.1-SNAPSHOT.jar file is in the ../build/libs directory.

<img width="1200" height="300" alt="스크린샷 2022-11-18 오전 1 25 48" src="https://user-images.githubusercontent.com/43805087/202501884-f24eaa65-e1ac-4e9e-b407-7e06620194ef.png">

After connecting to the ec2 instance using the terminal, deploy the jar file in a way of Zero Downtime Deployment.
```
nohup java -jar tejava-0.0.1-SNAPSHOT.jar &
```

Optionally, we can do port forwarding.
```
sudo iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080
```

## 📃 API Specification

This project utilize swagger Specification 2.0 and Swagger UI for communication with client.

Below is our project's HTTP REST API server swagger UI endpoint.

<http://43.200.93.146:8080/swagger-ui/#/>

![스크린샷 2022-11-18 오전 1 31 38](https://user-images.githubusercontent.com/43805087/202503174-f3545e82-da6d-451c-bfdb-110830da54a1.png)
![스크린샷 2022-11-18 오전 1 31 47](https://user-images.githubusercontent.com/43805087/202503259-1df2c0bc-1600-4717-bdd9-dec4a03007b2.png)


## 🏛️ Depedency Used

- Spring Boot
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`

- Swagger
  - `springfox-boot-starter:3.0.0`

#### Contributors

[Chanmin Nam](https://github.com/namssaeng)|[Sejeong Min](https://github.com/SejeongMin)|[Jaeuk Im](https://github.com/iju1633)
|:---:|:---:|:---:|
FRONTEND|FRONTEND|BACKEND|
