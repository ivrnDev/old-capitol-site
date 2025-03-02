package com.ivnrdev.connectodo.Configuration;

import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.stereotype.Component;

@Component
public class CustomNamingStrategy implements NamingStrategy {
    @Override
    public String getColumnName(RelationalPersistentProperty property) {
        return property.getName();
    }
}
