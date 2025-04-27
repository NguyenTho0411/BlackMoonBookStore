package com.shopme;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "user-photos";
        Path userPhotosDir = Paths.get(dirName);
        String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotosPath + "/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/webfonts/**")
                .addResourceLocations("classpath:/static/webfonts/");
        registry.addResourceHandler("/fontawesome/**")
                .addResourceLocations("classpath:/static/fontawesome/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/richtext/**")
                .addResourceLocations("classpath:/static/richtext/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        String categoryImagesDirName = "../category-images";
        Path categoryImageDir = Paths.get(categoryImagesDirName);
        String categoryImagesPath = categoryImageDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/category-images/**").addResourceLocations("file:/" + categoryImagesPath + "/");
        
        String publisherLogosDirName = "../publisher-logos";
        Path publisherLogosDir = Paths.get(publisherLogosDirName);
        String publisherLogosPath = publisherLogosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/publisher-logos/**").addResourceLocations("file:/" + publisherLogosPath + "/");
        
        
        String bookLogosDirName = "../book-images";
        Path bookLogosDir = Paths.get(bookLogosDirName);
        String bookLogosPath = bookLogosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/book-images/**").addResourceLocations("file:/" + bookLogosPath + "/");

        
        String siteLogosDirName = "../site-logo";
        Path siteLogosDir = Paths.get(siteLogosDirName);
        String siteLogosPath = siteLogosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/site-logo/**").addResourceLocations("file:/" + bookLogosPath + "/");
    }
    
    
}
