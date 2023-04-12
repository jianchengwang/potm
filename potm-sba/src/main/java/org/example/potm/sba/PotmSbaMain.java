package org.example.potm.sba;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@EnableAdminServer
@SpringBootApplication
public class PotmSbaMain {
    public static void main(String[] args) {
        SpringApplication.run(PotmSbaMain.class, args);
    }
}
