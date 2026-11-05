# URL Shortener

A serverless URL Shortener built with Java and AWS. The application allows users to convert long URLs into short, shareable links and redirects visitors to the original URLs.

## Architecture

The application uses a serverless architecture based on AWS managed services.

```text
                Client / Browser
                       |
                       v
                Amazon API Gateway
                       |
                       v
                   AWS Lambda
                    (Java 21)
                       |
                       v
                 Amazon DynamoDB
                       |
                       v
              URL Mapping Storage
```

### Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend implementation |
| Maven | Dependency management and build |
| AWS Lambda | Serverless application execution |
| Amazon API Gateway | HTTP API and request routing |
| Amazon DynamoDB | URL mapping storage |
| AWS SDK for Java 2.x | AWS service integration |
| AWS CDK (Java) | Infrastructure as Code |
| AWS CloudFormation | Infrastructure provisioning |
| Amazon CloudWatch | Logging and monitoring |

## Project Structure

```text
url-shortener/
│
├── backend/
│   ├── pom.xml
│   └── src/
│       └── main/java/com/example/
│           ├── handler/
│           │   └── UrlHandler.java
│           ├── service/
│           │   └── UrlService.java
│           ├── repository/
│           │   └── UrlRepository.java
│           ├── model/
│           │   ├── UrlMapping.java
│           │   └── CreateUrlRequest.java
│           └── util/
│               └── ShortCodeGenerator.java
│
├── infrastructure/
│   ├── pom.xml
│   ├── cdk.json
│   └── src/
│       └── main/java/com/example/
│           ├── App.java
│           └── infra/
│               └── UrlShortenerStack.java
│
└── pom.xml
```

### Module Responsibilities

**backend**

Contains the application logic:
- `handler` — AWS Lambda request handlers.
- `service` — business logic and URL validation.
- `repository` — DynamoDB data access.
- `model` — application data models.
- `util` — utility classes, including short code generation.

**infrastructure**

Contains AWS infrastructure definitions implemented using AWS CDK.

## Prerequisites

Make sure the following tools are installed:

- Java Development Kit (JDK) 21 or later.
- Apache Maven (or Maven Wrapper).
- Node.js and npm.
- AWS CLI.
- AWS CDK CLI.

Verify the installations:

```bash
java -version
mvn -version
node --version
npm --version
aws --version
cdk --version
```

## AWS Configuration

### 1. Configure AWS credentials

Configure your AWS CLI credentials:

```bash
aws configure
```

Provide your:
- AWS Access Key ID.
- AWS Secret Access Key.
- Default region (e.g. `eu-north-1`).
- Default output format (`json`).

Verify your AWS identity:

```bash
aws sts get-caller-identity
```

### 2. Install AWS CDK

```bash
npm install -g aws-cdk
```

### 3. Bootstrap AWS CDK

Bootstrap the AWS environment (once per account and region):

```bash
cd infrastructure
cdk bootstrap aws://YOUR_ACCOUNT_ID/eu-north-1
```

Replace `YOUR_ACCOUNT_ID` with your AWS account ID.

## Build and Deployment

### 1. Build the backend

Navigate to the backend module:

```bash
cd backend
```

Build the application:

```bash
mvn clean package
```

If Maven Wrapper is available, you can use:

```bash
./mvnw clean package
```

The build produces a deployable JAR in the `target/` directory.

### 2. Deploy AWS infrastructure

Navigate to the infrastructure module:

```bash
cd ../infrastructure
```

Review the infrastructure changes:

```bash
cdk diff
```

Deploy the stack:

```bash
cdk deploy
```

AWS CDK provisions the required AWS resources, including:
- DynamoDB table.
- AWS Lambda function.
- API Gateway HTTP API.
- IAM permissions.

After successful deployment, retrieve the API endpoint from the CloudFormation stack outputs or the deployment logs.

Example:

```text
https://YOUR_API_ID.execute-api.eu-north-1.amazonaws.com
```

## API Usage

Assuming the deployed API endpoint is:

```bash
API_URL="https://YOUR_API_ID.execute-api.eu-north-1.amazonaws.com"
```

### 1. Create a short URL

**Request**

```bash
curl -X POST "$API_URL/urls" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://en.wikipedia.org/wiki/Distributed_computing"
  }'
```

**Expected response**

```json
{
  "code": "aB92xK",
  "shortUrl": "https://YOUR_API_ID.execute-api.eu-north-1.amazonaws.com/aB92xK",
  "originalUrl": "https://en.wikipedia.org/wiki/Distributed_computing"
}
```

### 2. Redirect to the original URL

Use the generated short code:

```bash
curl -I "$API_URL/aB92xK"
```

**Expected response**

```http
HTTP/2 302
location: https://en.wikipedia.org/wiki/Distributed_computing
```

The API returns an HTTP 302 redirect to the original URL.

### 3. Test a non-existent short code

```bash
curl -i "$API_URL/unknown123"
```

Expected result:

```http
HTTP/2 404
```

## Running Tests

Run backend unit tests:

```bash
cd backend
mvn test
```

Or use Maven Wrapper:

```bash
./mvnw test
```

Run the complete Maven build:

```bash
mvn clean package
```

Additional integration testing can be performed against the deployed AWS environment using `curl` or Postman.

## Infrastructure Management

Preview infrastructure changes:

```bash
cd infrastructure
cdk diff
```

Deploy changes:

```bash
cdk deploy
```

Destroy the stack and its resources:

```bash
cdk destroy
```

**Warning:** Destroying the stack may delete the DynamoDB table and its data, depending on the configured removal policy. Do not use this command against production resources without checking the configuration.

## Monitoring

Application logs and execution metrics are available through Amazon CloudWatch.

Useful metrics include:
- Lambda invocations.
- Lambda execution duration.
- Lambda errors and throttles.
- API Gateway 4XX and 5XX responses.
- DynamoDB read and write activity.

## Future Improvements

Potential enhancements:

- Custom aliases for short URLs.
- Configurable URL expiration.
- Click analytics and statistics.
- Rate limiting and abuse protection.
- Caching for frequently accessed URLs.
- User authentication and URL ownership.
- Custom domain support.
- CI/CD pipeline with GitHub Actions.

## License

This project is intended for educational purposes and for exploring serverless application development, AWS services, and backend architecture with Java.
