package se.ifmo.origin_backend.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        var appCtx = new AnnotationConfigWebApplicationContext();
        appCtx.register(RootConfig.class, WebSocketConfig.class);

        var dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(appCtx));
        dispatcher.setLoadOnStartup(1);
        dispatcher.setAsyncSupported(true);
        dispatcher.addMapping("/api/*");
    }
}
