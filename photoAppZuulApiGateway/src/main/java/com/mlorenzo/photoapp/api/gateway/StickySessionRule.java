package com.mlorenzo.photoapp.api.gateway;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.zuul.context.RequestContext;

// Como ahora todas las urls correspondientes al acceso a la consola H2 del microservicio "users-ws" pasan por este proxy inverso Zuul,
// si hay más de una réplica o instancia de dicho microservicio, se producirán redirecciones infinitas en el acceso a la consola H2 porque
// una de estas peticiones irá a una instancia determinada y puede ser que otra petición vaya a otra instancia distinta provocando que la
// consola H2 no llegue a cargarse nunca. Por esta razón, creamos, mediante esta clase, una regla personalizada para crear una "Sticky Session",
// o sesión estable, en el balanceador de carga Ribbon que utiliza este proxy inverso Zuul. Lo que hace esta regla es mandar siempre todas las
// peticiones correspondientes a la consola H2 a la misma instancia del microservicio "users-ws" para evitar el problema de las redirecciones
// infinitas y, para el resto de peticiones que no tienen que ver con el acceso a la consola H2, las distribuye uniformemente entre todas
// las réplicas disponibles del microservico "users-ws" usando el algoritmo por defecto(Creo que es Round Robin). Para establecer esta "Sticky
// Session", o sesión estable, se crea una cookie de sesión en la respuesta de la primera petición http a la consola H2 que identifica a la
// instancia del microservicio "users-ws" que la ha tratado y, de esta forma, el balanceador de carga Ribbon utilizará el valor de esta cookie
// para enviar las siguientes peticiones http correspondientes a la consola H2 a la instancia que trato a esta primera petición http.

@Component
public class StickySessionRule extends ZoneAvoidanceRule {
	public static final String COOKIE_NAME_SUFFIX = "-" + StickySessionRule.class.getSimpleName();
	
	private Environment env;
	
	@Autowired
	public void setEnvironment(Environment env) {
		this.env = env;
	}
	
	@Override
    public Server choose(Object key) {
		AntPathRequestMatcher matcher = new AntPathRequestMatcher(env.getProperty("api.h2console.url.path"));
		if(!matcher.matches(RequestContext.getCurrentContext().getRequest()))
			return super.choose(key);
        Optional<Cookie> optionslCookie = getCookie(key);
        if(optionslCookie.isPresent()) {
            Cookie cookie = optionslCookie.get();
            List<Server> servers = getLoadBalancer().getReachableServers();
            Optional<Server> optionalServer = servers.stream()
                    .filter(s -> s.isAlive() && s.isReadyToServe())
                    // La expresión '"" + server.hashCode()' convierte el HashCode en un String
                    .filter(s -> cookie.getValue().equals("" + s.hashCode()))
                    .findFirst();
            if (optionalServer.isPresent())
                return optionalServer.get();
        }
        return useNewServer(key);
    }

    private Server useNewServer(Object key) {
        Server server = super.choose(key);
        HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
        if (response != null) {
            String cookieName = getCookieName(server);
            // La expresión '"" + server.hashCode()' convierte el HashCode en un String
            Cookie newCookie = new Cookie(cookieName, "" + server.hashCode());
            newCookie.setPath("/");
            response.addCookie(newCookie);
        }
        return server;
    }

    private Optional<Cookie> getCookie(Object key) {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        if(request != null) {
        	Server server = super.choose(key);
            String cookieName = getCookieName(server);
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                return Arrays.stream(cookies)
                        .filter(c -> c.getName().equals(cookieName))
                        .findFirst();
            }
        }
        return Optional.empty();
    }

    private String getCookieName(Server server) {
        return server.getMetaInfo().getAppName() + COOKIE_NAME_SUFFIX;
    }
}
