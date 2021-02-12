package icu.cyclone.avigilon;

import icu.cyclone.avigilon.services.CliService;
import java.io.IOException;

/**
 * @author Aleksey Babanin
 * @since 2021/01/26
 */
public class App {
    public static void main(String[] args) {
        new CliService(args, System.out).start();
    }
}
