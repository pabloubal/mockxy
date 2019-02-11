package io.github.pabloubal.mockxy.starter;

import io.github.pabloubal.mockxy.core.ChainHandler;
import io.github.pabloubal.mockxy.core.cache.CacheManager;
import io.github.pabloubal.mockxy.core.cache.strategies.impl.FileCache;
import io.github.pabloubal.mockxy.core.handlers.HandlerType;
import io.github.pabloubal.mockxy.core.handlers.generic.GetCache;
import io.github.pabloubal.mockxy.core.handlers.generic.HandlerEnd;
import io.github.pabloubal.mockxy.core.handlers.generic.Mappings;
import io.github.pabloubal.mockxy.core.handlers.generic.RemoveCache;
import io.github.pabloubal.mockxy.core.handlers.generic.SetCache;
import io.github.pabloubal.mockxy.core.handlers.http.RemoteHTTPCall;
import io.github.pabloubal.mockxy.core.handlers.socket.RemoteTCPCall;
import io.github.pabloubal.mockxy.core.requests.Proxy;
import io.github.pabloubal.mockxy.core.utils.Mapping;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.stream.Collectors;

/**
 * Spring Boot Autoconfiguration.
 *
 * Creates and configures the whole Mockxy ecosystem.
 *
 * @author Pablo Ubal - pablo.ubal@gmail.com
 */
@EnableConfigurationProperties
@Configuration
@ConditionalOnProperty(value = "mockxy.enabled", havingValue = "true")
public class MockxyAutoConfiguration {

    @Bean
    public HandlerEnd handlerEnd(){
        return new HandlerEnd();
    }

    @Bean
    public Mappings mappings(){
        return new Mappings();
    }

    @Bean
    public RemoveCache removeCache(){
        return new RemoveCache();
    }
    @Bean
    public GetCache getCache(){
        return new GetCache();
    }
    @Bean
    public RemoteHTTPCall remoteHTTPCall(){
        return new RemoteHTTPCall();
    }
    @Bean
    public SetCache setCache(){
        return new SetCache();
    }
    @Bean
    public RemoteTCPCall remoteTCPCall(){
        return new RemoteTCPCall();
    }

    @Bean
    public ChainHandler chainHandler(
            Mappings mappings,
            RemoveCache removeCache,
            GetCache getCache,
            RemoteHTTPCall remoteHTTPCall,
            SetCache setCache,
            RemoteTCPCall remoteTCPCall){

        ChainHandler chainHandler = new ChainHandler();

        chainHandler.use(HandlerType.HTTP, mappings);
        chainHandler.use(HandlerType.HTTP, removeCache);
        chainHandler.use(HandlerType.HTTP, getCache);
        chainHandler.use(HandlerType.HTTP, remoteHTTPCall);
        chainHandler.use(HandlerType.HTTP, setCache);

        chainHandler.use(HandlerType.SOCKET, mappings);
        chainHandler.use(HandlerType.SOCKET, removeCache);
        chainHandler.use(HandlerType.SOCKET, getCache);
        chainHandler.use(HandlerType.SOCKET, remoteTCPCall);
        chainHandler.use(HandlerType.SOCKET, setCache);

        return chainHandler;
    }

    @Bean
    public Proxy proxy(){
        return new Proxy();
    }

    @Bean
    @ConditionalOnExpression("'${mockxy.cache.strategy:FileCache}' == 'FileCache'")
    public FileCache cacheStrategyFile(){
        return new FileCache();
    }

    @Bean
    public CacheManager cacheManager(){
        return new CacheManager();
    }

    @Bean//TODO check if there's a better way to hook to a PostConfiguration
    public Object init(@Value("${mockxy.socksNonProxyHosts:}") String socksNonProxyHosts,
                      @Value("${mockxy.httpNonProxyHosts:}") String httpNonProxyHosts,
                      @Value("${mockxy.httpPort:}") String httpProxyPort,
                      @Value("${mockxy.socksPort:}") String socksProxyPort,
                      Mappings mappings) {

        if (!StringUtils.isEmpty(httpProxyPort)) {
            System.setProperty("http.proxyHost", "localhost");
            System.setProperty("http.proxyPort", httpProxyPort);
            System.setProperty("http.nonProxyHosts", httpNonProxyHosts);
        }

        if (!StringUtils.isEmpty(socksProxyPort)) {
            System.setProperty("socksProxyHost", "localhost");
            System.setProperty("socksProxyPort", socksProxyPort);

            if(StringUtils.isEmpty(socksNonProxyHosts)) {
                socksNonProxyHosts = mappings.getMappings().entrySet().stream()
                                        .filter(e -> e.getValue().getType().equals(Mapping.MappingType.HTTP))
                                        .map(e->e.getKey()+"*")
                                        .collect(Collectors.joining("|"));
            }
            System.setProperty("socksNonProxyHosts", socksNonProxyHosts);
        }

        return null;
    }

}