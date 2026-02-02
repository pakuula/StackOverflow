/* Печатает адреса на интерфейсах, подключенных к локальным сетям */
import java.net.Inet4Address;
import java.util.List;

public class PrintLocalAddressesIPv4 {
    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();
        System.out.println("Detected OS: " + os);
        if (os.indexOf("win") != -1) {
            List<Inet4Address> addresses = WindowsAddresses.localAddresses();
            for (Inet4Address addr : addresses) {
                System.out.println("Local address: " + addr.getHostAddress());
            }
            return;
        }
        if (os.indexOf("linux") != -1) {
            List<Inet4Address> addresses = LinuxAddresses.localAddresses();
            for (Inet4Address addr : addresses) {
                System.out.println("Local address: " + addr.getHostAddress());
            }
            return;
        }
        System.out.println("Unsupported OS: " + os);
    }
}