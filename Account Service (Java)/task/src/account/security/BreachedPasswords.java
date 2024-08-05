package account.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import jakarta.annotation.PostConstruct;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@Configuration
public class BreachedPasswords {

    private List<String> breachedPasswords;
    private final String fileName = "breached_passwords.json";

    @PostConstruct
    public void loadBreachedPasswords() {
        try {
            Resource r = new ClassPathResource(fileName);
            ObjectMapper o = new ObjectMapper();
            this.breachedPasswords = o.readValue(
                    r.getInputStream(), new TypeReference<>() {}
            );
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading file: " + fileName);
        }
    }

    public boolean isBreached(String password) {
        return breachedPasswords.contains(password);
    }
}