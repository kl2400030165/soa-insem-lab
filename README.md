# Round-Robin Load Balancing using Spring Cloud Gateway

## Aim
To implement and demonstrate client-side **Round-Robin Load Balancing** using **Spring Cloud Gateway** and **Spring Cloud LoadBalancer** across multiple instances of a microservice (`driver-service`) running on different ports without using an external service registry (Eureka).

---

## Architecture Overview
- **driver-service**: A simple Spring Boot REST microservice with an endpoint `/drivers`. It prints a console log and returns its instance name and running port. Three instances run on ports **8081**, **8082**, and **8083**.
- **api-gateway**: Spring Cloud Gateway running on port **8080**. It receives client requests at `/drivers`, uses Spring Cloud LoadBalancer (`lb://driver-service`) with a Round-Robin algorithm, and distributes traffic evenly among the 3 driver-service instances.

```
Client (Browser / curl)
        |
        v  http://localhost:8080/drivers
+------------------------------------+
|     API Gateway (Port 8080)        |
|   (Spring Cloud LoadBalancer)      |
+------------------------------------+
       /          |          \
 Request 1    Request 2    Request 3
     /            |            \
    v             v             v
[Instance 1]  [Instance 2]  [Instance 3]
 Port: 8081    Port: 8082    Port: 8083
```

---

## Project Structure
```
SOA-inlab/
├── driver-service/
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── driverservice/
│           │               ├── DriverServiceApplication.java
│           │               └── DriverController.java
│           └── resources/
│               └── application.properties
├── api-gateway/
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── apigateway/
│           │               └── ApiGatewayApplication.java
│           └── resources/
│               └── application.properties
└── README.md
```

---

## Prerequisites
- **Java 17+** (or Java 21)
- **Apache Maven 3.8+**

---

## Step-by-Step Procedure

### Step 1: Build the Applications
Open PowerShell in the root directory (`c:\Users\sayya\Desktop\SOA-inlab`) and package both projects:

```powershell
# Build driver-service
cd driver-service
mvn clean package -DskipTests
cd ..

# Build api-gateway
cd api-gateway
mvn clean package -DskipTests
cd ..
```

---

### Step 2: Run the Three Driver Service Instances
Open **3 separate Windows PowerShell windows** and run one instance in each:

#### Terminal 1 (Driver Service - Instance 1 on Port 8081):
```powershell
cd c:\Users\sayya\Desktop\SOA-inlab\driver-service
java -jar target/driver-service-0.0.1-SNAPSHOT.jar --server.port=8081
```

#### Terminal 2 (Driver Service - Instance 2 on Port 8082):
```powershell
cd c:\Users\sayya\Desktop\SOA-inlab\driver-service
java -jar target/driver-service-0.0.1-SNAPSHOT.jar --server.port=8082
```

#### Terminal 3 (Driver Service - Instance 3 on Port 8083):
```powershell
cd c:\Users\sayya\Desktop\SOA-inlab\driver-service
java -jar target/driver-service-0.0.1-SNAPSHOT.jar --server.port=8083
```

---

### Step 3: Run the API Gateway
Open a **4th Windows PowerShell window** and start the Gateway:

#### Terminal 4 (API Gateway on Port 8080):
```powershell
cd c:\Users\sayya\Desktop\SOA-inlab\api-gateway
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

Wait until you see:
```
Netty started on port 8080 (http)
Started ApiGatewayApplication
```

---

### Step 4: Testing the Load Balancing

#### Option A: In a Web Browser
Open your browser and navigate to:
```
http://localhost:8080/drivers
```
Refresh the page multiple times (**F5**). Notice that the port number rotates in Round-Robin order:
- 1st Refresh: Port `8081` (or next in order)
- 2nd Refresh: Port `8082`
- 3rd Refresh: Port `8083`
- 4th Refresh: Port `8081` ...

#### Option B: In PowerShell (Quick Loop Test)
Open a 5th PowerShell terminal and run:
```powershell
1..6 | ForEach-Object { Invoke-RestMethod http://localhost:8080/drivers }
```
Or using `curl`:
```powershell
for ($i = 1; $i -le 6; $i++) { curl.exe -s http://localhost:8080/drivers; Write-Host "" }
```

---

## Expected Output

### HTTP Responses
```text
Hello from driver-service running on port: 8081
Hello from driver-service running on port: 8082
Hello from driver-service running on port: 8083
Hello from driver-service running on port: 8081
Hello from driver-service running on port: 8082
Hello from driver-service running on port: 8083
```

### Console Logs in Service Terminals
- **Terminal 1 (Port 8081)**:
  ```text
  >>> [driver-service] Request handled on port: 8081
  >>> [driver-service] Request handled on port: 8081
  ```
- **Terminal 2 (Port 8082)**:
  ```text
  >>> [driver-service] Request handled on port: 8082
  >>> [driver-service] Request handled on port: 8082
  ```
- **Terminal 3 (Port 8083)**:
  ```text
  >>> [driver-service] Request handled on port: 8083
  >>> [driver-service] Request handled on port: 8083
  ```

---

## Conclusion
The API Gateway successfully intercepted client requests sent to `http://localhost:8080/drivers` and routed them across three instances of `driver-service` in a strict **Round-Robin** sequence using Spring Cloud Gateway and Spring Cloud LoadBalancer.
