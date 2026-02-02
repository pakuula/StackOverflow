// Класс для хранения информации о маршруте
// Таблица маршрутизации Windows имеет следующий вид:
// Network Destination Netmask Gateway Interface Metric
// 0.0.0.0 0.0.0.0 192.168.1.1 192.168.1.46 36

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RouteInfo {
    public final Inet4Address destination;

    public final Inet4Address netmask;
    public final int prefixLength;

    // Гейтвей маршрута может быть адресом, а может быть флагом "On-link"
    public final String gateway;
    public final Inet4Address gatewayAddress;

    // Интерфейс может быть адресом, а может быть строковым именем
    public final String interfaceName;
    public final Inet4Address interfaceAddress;
    public final int metric;

    // Вместо использования InetAddress.getByName, реализуем парсер IPv4-адресов
    // вручную
    // так как getByName может обращаться к DNS, что очень долго и не нужно здесь
    static private Inet4Address parseInet4Address(String addr) throws UnknownHostException {
        String[] parts = addr.split("\\.");
        if (parts.length != 4) {
            throw new UnknownHostException("Invalid IPv4 address: " + addr);
        }
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) Integer.parseInt(parts[i]);
        }
        return (Inet4Address) InetAddress.getByAddress(bytes);
    }

    // Длина непрерывной последовательности единичных битов в маске подсети
    static private int calculatePrefixLength(Inet4Address netmask) {
        byte[] maskBytes = netmask.getAddress();
        int prefixLength = 0;
        for (byte b : maskBytes) {
            for (int i = 7; i >= 0; i--) {
                if ((b & (1 << i)) != 0) {
                    prefixLength++;
                } else {
                    return prefixLength;
                }
            }
        }
        return prefixLength;
    }

    public RouteInfo(String destination, String netmask, String gateway, String interfaceStr, String metric)
            throws UnknownHostException {
        // В линуксе маршрут по умолчанию может быть указан как "default"
        if (destination == "default") {
            destination = "0.0.0.0";
        }

        this.destination = parseInet4Address(destination);

        this.netmask = parseInet4Address(netmask);
        this.prefixLength = calculatePrefixLength(this.netmask);

        this.gateway = gateway;
        Inet4Address addr = null;
        try {
            addr = parseInet4Address(gateway);
        } catch (UnknownHostException e) {
            // Ignore, gateway might be "On-link"
        }
        this.gatewayAddress = addr;

        this.interfaceName = interfaceStr;
        try {
            addr = parseInet4Address(interfaceStr);
        } catch (UnknownHostException e) {
            // Ignore, interface might be a name
            addr = null;
        }
        this.interfaceAddress = addr;

        this.metric = Integer.parseInt(metric);
    }

    public RouteInfo(Inet4Address destination, Inet4Address netmask, Inet4Address gateway, String interfaceStr, String metric) {
        this.destination = destination;
        this.netmask = netmask;
        this.prefixLength = calculatePrefixLength(this.netmask);
        this.gateway = gateway.getHostAddress();
        this.gatewayAddress = gateway;
        this.interfaceName = interfaceStr;
        this.interfaceAddress = null;
        this.metric = Integer.parseInt(metric);
    }

    @Override
    public String toString() {
        return String.format(
                "RouteInfo{destination=%s, netmask=%s, prefixLength=%d, gateway=%s, interface=%s, address=%s, metric=%d}",
                destination.getHostAddress(), netmask.getHostAddress(), prefixLength, gateway, interfaceName,
                interfaceAddress, metric);
    }

    /// Сортирует маршруты по возрастанию метрики
    public static class ComparatorByMetric implements java.util.Comparator<RouteInfo> {
        @Override
        public int compare(RouteInfo o1, RouteInfo o2) {
            return Integer.compare(o1.metric, o2.metric);
        }
    }
}