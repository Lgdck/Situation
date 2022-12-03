package situation;

import com.situation.util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author lgd
 * @date 2021/10/27 20:57
 */
@SpringBootApplication
@MapperScan(basePackages = {"situation.dao"})
@EnableAsync //开启异步
public class PacketSendApplication {
    public static void main(String[] args){
        SpringApplication.run(PacketSendApplication.class,args);
    }
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }

    //允许http请求含有字符[]{}
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory (){
        // 修改内置的 tomcat 容器配置
        TomcatServletWebServerFactory tomcatServlet = new TomcatServletWebServerFactory();
        tomcatServlet.addConnectorCustomizers(
                (TomcatConnectorCustomizer) connector -> connector.setProperty("relaxedQueryChars", "[]")
        );
        return tomcatServlet ;
    }
}
