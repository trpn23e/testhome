/*
 * (c)BOC
 */
package net.pis;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * web.xml 디스패처 CXF를 포함한다는 것 이외에 특별한 정보는 없다.
 *
 * @author jh,Seo
 */
public class Initializer implements WebApplicationInitializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onStartup(ServletContext sc) throws ServletException {

        logger.trace("Web descriptor is initializing...");

        //XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

        //rootContext.setConfigLocation("WEB-INF/applicationContext.xml");
        // rootContext.register(AppConfig.class);
        rootContext.register(ExApplication.class);

        sc.addListener(new ContextLoaderListener(rootContext));

        this.addDispatcherServlet(rootContext, sc);
        this.addApacheCxfServlet(sc);

        this.addUtf8CharacterEncodingFilter(sc);

        logger.trace("Web descriptor is completed.");
    }

    private void addApacheCxfServlet(ServletContext sc) {

        logger.trace("CXF Servlet is initializing...");

        CXFServlet servlet = new CXFServlet();

        ServletRegistration.Dynamic appServlet = sc.addServlet("cxf", servlet);

        appServlet.addMapping("/services/*");

        appServlet.setLoadOnStartup(1);
        appServlet.setInitParameter("dispatchOptionsRequest", "true"); // CORS 를 위해서 option request 도 받아들인다.

        logger.trace("CXF Servlet is completed.");
    }

    /**
     * Dispatcher Servlet 을 추가한다. CORS 를 가능하게 하기 위해서 dispatchOptionsRequest 설정을
     * true 로 한다.
     *
     * @param sc
     */
    private void addDispatcherServlet(AnnotationConfigWebApplicationContext rootContext,
        ServletContext sc) {

        logger.trace("SBMS Servlet is initializing...");

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.setParent(rootContext);
        applicationContext.getEnvironment().addActiveProfile("production");
        //applicationContext.register(AppConfig.class);

        ServletRegistration.Dynamic dispatcher = sc.addServlet("sbms", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(2);
        //dispatcher.addMapping("/");
        dispatcher.addMapping("/");

        dispatcher.setInitParameter("dispatchOptionsRequest", "true"); // CORS 를 위해서 option request 도 받아들인다.

        logger.trace("SBMS Servlet is completed.");
    }

    /**
     * UTF-8 캐릭터 인코딩 필터를 추가한다.
     *
     * @param servletContext
     */
    private void addUtf8CharacterEncodingFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic filter = servletContext.addFilter("CHARACTER_ENCODING_FILTER", CharacterEncodingFilter.class);
        filter.setInitParameter("encoding", "UTF-8");
        filter.setInitParameter("forceEncoding", "true");
        filter.addMappingForUrlPatterns(null, false, "/*");
    }
}
