module lab04.client {
    exports client_Package;

    opens client_Package to com.google.gson;

    requires java.net.http;
    requires com.google.gson;

}