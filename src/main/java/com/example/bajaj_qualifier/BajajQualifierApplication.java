package bajajqualifier.dto;


import bajajqualifier.dto.GenerateWebhookRequest;
import bajajqualifier.dto.GenerateWebhookResponse;
import bajajqualifier.dto.SubmitAnswerRequest;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BajajQualifierApplication {

    private static final String GENERATE_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    public static void main(String[] args) {
        SpringApplication.run(BajajQualifierApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ApplicationRunner run(RestTemplate restTemplate) {
        return args -> {
            // 1) Generate webhook
            GenerateWebhookRequest req = new GenerateWebhookRequest(
                    "Noor Fathima AI",        // ✅ your name
                    "1RF22IS052",             // ✅ your RegNo
                    "ainoorfathima2003@gmail.com" // ✅ your email
            );

            ResponseEntity<GenerateWebhookResponse> resp =
                    restTemplate.postForEntity(GENERATE_URL, req, GenerateWebhookResponse.class);

            GenerateWebhookResponse body = resp.getBody();
            if (body == null) {
                throw new RuntimeException("No response from generateWebhook!");
            }

            String webhookUrl = (body.getWebhook() != null && !body.getWebhook().isEmpty())
                    ? body.getWebhook()
                    : SUBMIT_URL;
            String token = body.getAccessToken();

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + token);

            // 2) Decide which question applies
            String regNo = req.getRegNo();
            String lastTwo = regNo.replaceAll("[^0-9]", "");
            if (lastTwo.length() >= 2) {
                int num = Integer.parseInt(lastTwo.substring(lastTwo.length() - 2));
                if (num % 2 == 0) {
                    System.out.println("Assigned Question: EVEN → Q2");
                } else {
                    System.out.println("Assigned Question: ODD → Q1");
                }
            }

            // 3) ✅ Final SQL Query (solution for Q2)
            String finalQuery =
                    "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, "
                    + "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT "
                    + "FROM EMPLOYEE e1 "
                    + "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID "
                    + "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT "
                    + "AND e2.DOB > e1.DOB "
                    + "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME "
                    + "ORDER BY e1.EMP_ID DESC;";

            // 4) Submit final query
            SubmitAnswerRequest answer = new SubmitAnswerRequest(finalQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token); // ⚠️ if API requires Bearer, use: "Bearer " + token

            HttpEntity<SubmitAnswerRequest> entity = new HttpEntity<>(answer, headers);

            ResponseEntity<String> submitResp =
                    restTemplate.postForEntity(webhookUrl, entity, String.class);

            System.out.println("Submission Status: " + submitResp.getStatusCode());
            System.out.println("Server Response: " + submitResp.getBody());
        };
    }
}
