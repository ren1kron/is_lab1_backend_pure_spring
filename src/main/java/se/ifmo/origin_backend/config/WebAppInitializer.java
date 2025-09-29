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
        AnnotationConfigWebApplicationContext rootCtx = new AnnotationConfigWebApplicationContext();
        rootCtx.register(RootConfig.class);
//        rootCtx.refresh();
        servletContext.addListener(new ContextLoaderListener(rootCtx));

        AnnotationConfigWebApplicationContext mvcCtx = new AnnotationConfigWebApplicationContext();
        mvcCtx.register(WebMvcConfig.class);
        mvcCtx.setParent(rootCtx);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
                new DispatcherServlet(mvcCtx));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/api/*");
    }
}
