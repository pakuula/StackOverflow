import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class WindowsAddresses {
    /// Парсит вывод команды `route print -4`.
    /// Возвращает список маршрутов IPv4.
    public static List<RouteInfo> parseRoutePrint() {
        // Parse output of `route print -4` command
        ProcessBuilder builder = new ProcessBuilder("route", "print", "-4");
        builder.redirectErrorStream(true);
        List<RouteInfo> routes = new ArrayList<>();

        try {
            Process process = builder.start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                int delimiterCounter = 0;
                boolean inTable = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("=======")) {
                        delimiterCounter++;
                        // Сначала идёт таблица интерфейсов (2 разделителя),
                        // потом таблица маршрутов (начинается с 3-го)
                        if (delimiterCounter == 3) {
                            inTable = true;
                            // Пропустить 2 строки заголовка
                            // Active Routes:
                            // Network Destination Netmask Gateway Interface Metric
                            reader.readLine();
                            reader.readLine();
                        } else if (delimiterCounter > 3) {
                            // Конец таблицы маршрутов
                            inTable = false;
                        }
                        continue;
                    }
                    if (!inTable) {
                        continue;
                    }
                    // Каждый маршрут имеет вид:
                    // Network Destination Netmask Gateway Interface Metric
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length < 5) {
                        continue;
                    }
                    try {
                        RouteInfo route = new RouteInfo(parts[0], parts[1], parts[2], parts[3], parts[4]);
                        routes.add(route);
                    } catch (UnknownHostException e) {
                        System.err.println("WARNING: Unable to parse route line: " + line);
                    }
                }
            }
            process.waitFor();
        } catch (java.io.IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return routes;
    }

    /// Возвращает список маршрутов по умолчанию, отсортированных по метрике/приоритету.
    public static List<RouteInfo> getDefaultRoutes() {
        List<RouteInfo> routes = parseRoutePrint();
        List<RouteInfo> defaultRoutes = new ArrayList<>();
        for (RouteInfo route : routes) {
            // TODO: улучшить логику выбора маршрута по умолчанию
            // Наиболее частый случай - маршрут по умолчанию имеет адрес назначения 0.0.0.0
            // с маской 0.0.0.0/0.
            // Но бывает, что маски используются ненулевые, и прописаны несколько
            // маршрутов через один интерфейс.
            if (route.destination.isAnyLocalAddress() && route.prefixLength == 0) {
                defaultRoutes.add(route);
            }
        }
        return defaultRoutes;
    }

    /// Возвращает список локальных IPv4 адресов на интерфейсах,
    /// используемых для маршрутов по умолчанию.
    public static List<Inet4Address> localAddresses() {
        List<Inet4Address> addresses = new ArrayList<>();

        List<RouteInfo> routes = getDefaultRoutes();
        routes.sort(new RouteInfo.ComparatorByMetric());
        for (RouteInfo route : routes) {
            addresses.add(route.interfaceAddress);
        }
        return addresses;
    }
}
