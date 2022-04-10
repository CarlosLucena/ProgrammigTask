package lucena;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * DigIo - Programming Task
 * jdk-17
 * @author Carlos Lucena
 */
public class LogReport {
  private Map<String, Integer> ipAddresses = new HashMap<>();
  private Map<String, Integer> visitedUrls = new HashMap<>();
  private List<String> unmappedRow = new ArrayList<>();
  
  private static BiFunction<Integer, Integer, Integer> add = (Integer x, Integer y) -> x + y; 
  private static Comparator<Entry<String, Integer>> descCompartor = 
      Comparator.comparing((Map.Entry<String, Integer> entry) -> entry.getValue()).reversed();

  protected static record LogRecord(String ipAddress, String logDate, String httpMethod, String url, String httpVer,
      String httpResponseStatus, String otherLogText) {
  }

  private LogReport(Path logfile) throws IOException {
    try (var stremReader = Files.lines(logfile)) {
      stremReader.forEach( line -> {
        var logRecordOp = buildLogRecord(line);
        if (logRecordOp.isPresent()) {
          var logRecord = logRecordOp.get();
          ipAddresses.merge(logRecord.ipAddress, 1, add);
          visitedUrls.merge(logRecord.url, 1, add);
        } else {
          unmappedRow.add(line);
        }
      });
    }
  }

  /**
   * () -> Map(ipAddress, numberOfRows)
   */
  public Map<String, Integer> getIpAddresses() {
    return ipAddresses;
  }

  /**
   * () -> Map(URL, numberOfVisits)
   */
  public Map<String, Integer> getVisitedUrls() {
    return visitedUrls;
  }

  /**
   * () -> List(UnmappedRow) 
   */
  public List<String> getUnmappedRow() {
    return unmappedRow;
  }
  
  /**
   * () -> total_Number_Of_Unique_Ip_Address 
   */
  public int getNbrOfUniqueIpAddress(){
    return ipAddresses.size();
  }
  
  /**
   * (limitOfRows) -> List<Map.Entry<urlName, numberOfVisits>> 
   */
  public List<Map.Entry<String, Integer>> getTopMostVisitedURLs(int limit){
    return collectDescOrder(visitedUrls, limit);
  }
  
  /**
   * (limitOfRows) -> List<Map.Entry<ipAddress, numberOfRequest>> 
   */
  public List<Map.Entry<String, Integer>>  getTopMostActiveIpAddress(int limit){
    return collectDescOrder(ipAddresses, limit);
  }
  
  private List<Map.Entry<String, Integer>> collectDescOrder(Map<String, Integer> map, int limit) {
    return map.entrySet().stream()
        .sorted(descCompartor)
        .limit(limit)
        .toList();
  }
  
  /**
   * Build a LogReport instances.
   * @throws IOException 
   */
  public static LogReport loadReport(Path logfile) throws IOException {
    return new LogReport(logfile);
  }
  
  public static void main(String[] args) throws IOException {
    var startTime = LocalTime.now();
    var logfile = getLogfile(args); 
    var logReport = new LogReport(logfile);
    System.out.println("*** Log Report ***");
    System.out.println("Number of unique IP addresses: " + logReport.ipAddresses.size());

    var mostVisited = logReport.getTopMostVisitedURLs(3).stream()
        .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
        .collect(Collectors.joining("\n  ", "  ", ""));
    System.out.println("Top 3 most visited URLs:\n" + mostVisited);

    var mostActiveIpAddress = logReport.getTopMostActiveIpAddress(3).stream()
        .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
        .collect(Collectors.joining("\n  ", "  ", ""));
    System.out.println("Top 3 most active IP addresses:\n" + mostActiveIpAddress);

    if (logReport.getUnmappedRow().size() > 0)
      System.out.println("Warning: unable to match " + logReport.getUnmappedRow().size() + " row(s)");
    
    Duration elapseTime = Duration.between(startTime, LocalTime.now());
    System.out.println("Report total elapse time: " + elapseTime.getSeconds() + "s");
    System.out.println("*** End of Log Report ***");
  }

  private static final String msg1 = """ 
    Missing log file.
      - Pass the log file as a Java parameter
        e.g. java lucena.LogReport logfile.log
      - Or create a LogReport.properties file and add "logreport.logfile:<logfile.log>"
    """;
  private static final String msg2 = """ 
      Invalid log file. 
      Check: 
        - Java logfile parameter
          e.g. java lucena.LogReport logfile.log
        - Or the "logreport.logfile:"property in LogReport.properties.
      """;
  protected static Path getLogfile(String[] args) {
    String logfile = null;
    if (args.length > 0) {
      logfile = args[0];
    } else {
      InputStream inputStream = LogReport.class.getClassLoader().getResourceAsStream("LogReport.properties");
      Properties properties = new Properties();
      try {
        properties.load(inputStream);
      } catch (Throwable e) {
        throw new RuntimeException(msg1);
      }
      logfile = (String)properties.get("logreport.logfile");
    }

    var path = Paths.get(logfile);
    if (!Files.isReadable(path)) {
      throw new RuntimeException(msg2);      
    };
    
    return path;  
  }

  protected static Path getProgrammingTaskLogfile() {
    try {
      var url = LogReport.class.getClassLoader().getResource("programming-task-example-data.log");
      return Paths.get(url.toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * group 1 = ip address 
   * group 2 = logDate 
   * group 3 = http method 
   * group 4 = url
   * group 5 = http version 
   * group 6 = http response status 
   * group 7 = remaining text
   */
  private static Pattern pattern = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) .* \\[(.*)\\] \\\"([A-Z]{3,7}) (.+?) (.+?)\\\".(\\d{3}) (.*)$");

  static Optional<LogRecord> buildLogRecord(String line) {
    var matcher = pattern.matcher(line);
    if (matcher.matches()) {
      return Optional.of(new LogRecord(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4),
          matcher.group(5), matcher.group(6), matcher.group(7)));
    }
    return Optional.empty();
  }
}
