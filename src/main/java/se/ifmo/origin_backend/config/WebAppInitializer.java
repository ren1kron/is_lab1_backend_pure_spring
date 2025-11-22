package se.ifmo.origin_backend.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
    // location where files will be stored temporarily (can be null = default)
    private static final String TMP_FOLDER = null; // or "/tmp" or servletContext.getTmpDir()
    // in bytes:
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final long MAX_REQUEST_SIZE = 20 * 1024 * 1024; // 20 MB
    private static final int FILE_SIZE_THRESHOLD = 0; // write to disk immediately


    @Override
    public void onStartup(ServletContext servletContext) {
        var appCtx = new AnnotationConfigWebApplicationContext();
        appCtx.register(RootConfig.class, WebSocketConfig.class);

        var dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(appCtx));
        dispatcher.setLoadOnStartup(1);
        dispatcher.setAsyncSupported(true);
        dispatcher.addMapping("/api/*");

        MultipartConfigElement multipartConfig = new MultipartConfigElement(
            TMP_FOLDER,
            MAX_FILE_SIZE,
            MAX_REQUEST_SIZE,
            FILE_SIZE_THRESHOLD);
        dispatcher.setMultipartConfig(multipartConfig);
    }
}
