package service;

public class ContextService {

    public static boolean isLocalHost() {
        return getServerUrl().equals("http://localhost:8080/openmrs");
    }

    //TODO: Use real url when it'll be configurable
    public static String getServerUrl() {
        return "http://cfl-dev-lb-149730096.us-east-1.elb.amazonaws.com/openmrs";
    }

    //TODO: Use real url when it'll be configurable
    public static String getUsername() {
        return "admin";
    }

    //TODO: Use real url when it'll be configurable
    public static String getPassword() {
        return "Admin123";
    }
}
