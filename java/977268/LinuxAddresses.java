import java.io.BufferedReader;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.List;

public class LinuxAddresses {

    /// Парсит IPv4 адрес из /proc/net/route
    public static Inet4Address parseHexAddress(String hexAddr) throws UnknownHostException {
        long addrLong = Long.parseLong(hexAddr, 16);
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (addrLong & 0xFF);
        bytes[1] = (byte) ((addrLong >> 8) & 0xFF);
        bytes[2] = (byte) ((addrLong >> 16) & 0xFF);
        bytes[3] = (byte) ((addrLong >> 24) & 0xFF);
        return (Inet4Address) InetAddress.getByAddress(bytes);
    }

    /// Парсит /proc/net/route на Linux
    public static List<RouteInfo> parseLinuxRoutingTable() {
        List<RouteInfo> routes = new ArrayList<>();
        try (InputStream is = new java.io.FileInputStream("/proc/net/route");
                BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(is))) {
            // Пропустить заголовок
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                // каждая строка имеет вид:
                // Iface Destination Gateway Flags RefCnt Use Metric Mask MTU Window IRTT
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length < 11) {
                    continue;
                }
                Inet4Address destination = parseHexAddress(parts[1]);
                Inet4Address netmask = parseHexAddress(parts[7]);
                Inet4Address gateway = parseHexAddress(parts[2]);
                String iface = parts[0];
                String metric = parts[6];
                RouteInfo route = new RouteInfo(destination, netmask, gateway, iface, metric);
                routes.add(route);
            }
        } catch (java.io.IOException e) {
            System.err.println("ERROR: Unable to read /proc/net/route");
            e.printStackTrace();
        }
        return routes;
    }

    /// Возвращает список имен интерфейсов, через которые идут маршруты по умолчанию
    public static List<String> linkInterfaces(List<RouteInfo> routes) {
        List<String> interfaces = new ArrayList<>();
        for (RouteInfo route : routes) {
            if (route.destination.isAnyLocalAddress() && route.prefixLength == 0) {
                interfaces.add(route.interfaceName);
            }
        }
        return interfaces;
    }

    /// Возвращает список локальных IPv4 адресов на интерфейсах,
    /// используемых для маршрутов по умолчанию
    public static List<Inet4Address> localAddresses() {
        List<RouteInfo> routes = parseLinuxRoutingTable();
        routes.sort(new RouteInfo.ComparatorByMetric());
        List<String> interfaces = linkInterfaces(routes);

        List<Inet4Address> addresses = new ArrayList<>();
        // Перебрать все сетевые интерфейсы и найти IPv4 адреса
        try {
            List<NetworkInterface> nets = list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface iface : nets) {
                if (interfaces.contains(iface.getName())) {
                    List<InetAddress> inetAddresses = list(iface.getInetAddresses());
                    for (InetAddress inetAddress : inetAddresses) {
                        if (inetAddress instanceof Inet4Address) {
                            addresses.add((Inet4Address) inetAddress);
                        }
                    }
                }
            }
        } catch (java.net.SocketException e) {
            System.err.println("ERROR: Unable to get network interfaces");
            e.printStackTrace();
        }
        return addresses;
    }
}
