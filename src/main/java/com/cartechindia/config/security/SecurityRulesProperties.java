package com.cartechindia.config.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityRulesProperties {

    private List<Rule> rules;

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public static class Rule {
        private String pattern;
        private String roles;

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getRoles() {
            return roles;
        }

        public void setRoles(String roles) {
            this.roles = roles;
        }
    }
}
