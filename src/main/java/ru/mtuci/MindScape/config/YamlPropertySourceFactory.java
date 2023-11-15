/**
 * <p>Описание:</p>
 * Класс для загрузки свойств из YAML-файлов в Spring Environment. Реализует интерфейс PropertySourceFactory.
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>createPropertySource</b> - Загружает свойства из указанного ресурса и создает PropertySource.</li>
 * </ul>
 */

package ru.mtuci.MindScape.config;

import lombok.NonNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    @NonNull
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties properties = factory.getObject();
        assert properties != null;
        return new PropertiesPropertySource(name, properties);
    }
}