package lucena;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LogReportTest {
  static LogReport logReport;

  @BeforeAll
  static void beforeAll() {
    Path logfile = LogReport.getProgrammingTaskLogfile();
    try {
      logReport = LogReport.loadReport(logfile);
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  void testGetIpAddresses() {
    Map<String, Integer> ipAddresses = logReport.getIpAddresses();
    assertTrue(ipAddresses.get("168.41.191.40") == 4);
    assertTrue(ipAddresses.get("50.112.00.11") == 3);
    assertTrue(ipAddresses.get("72.44.32.11") == 1);
  }

  @Test
  void testGetVisitedUrls() {
    Map<String, Integer> visitedUrls = logReport.getVisitedUrls();
    assertTrue(visitedUrls.get("/docs/manage-websites/") == 2);
    assertTrue(visitedUrls.get("/newsletter/") == 1);
    assertTrue(visitedUrls.get("/asset.css") == 1);
  }

  @Test
  void testGetUnmappedRow() {
    assertTrue(logReport.getUnmappedRow().size() == 0);
  }

  @Test
  void testGetNbrOfUniqueIpAddress() {
    assertTrue(logReport.getNbrOfUniqueIpAddress() == 11);
  }

  @Test
  void testGetTopMostVisitedURLs() {
    List<Entry<String,Integer>> topMostVisitedURLs = logReport.getTopMostVisitedURLs(1);
    assertTrue(topMostVisitedURLs.size() == 1);
    assertTrue(topMostVisitedURLs.get(0).getKey().equals("/docs/manage-websites/"));
    assertTrue(topMostVisitedURLs.get(0).getValue() == 2);

    topMostVisitedURLs = logReport.getTopMostVisitedURLs(Integer.MAX_VALUE);
    assertTrue(topMostVisitedURLs.size() == logReport.getVisitedUrls().size());
  }

  @Test
  void testGetTopMostActiveIpAddress() {
    List<Entry<String,Integer>> topMostActiveIpAddress = logReport.getTopMostActiveIpAddress(3);
    assertTrue(topMostActiveIpAddress.size() == 3);
    assertTrue(topMostActiveIpAddress.get(0).getKey().equals("168.41.191.40"));
    assertTrue(topMostActiveIpAddress.get(0).getValue() == 4);

    assertTrue(topMostActiveIpAddress.get(1).getValue() == 3);
  }

  @Test
  void testBuildLogRecord() {
    var line = "72.44.32.10 - - [09/Jul/2018:15:48:20 +0200] \"GET /download/counter/ HTTP/1.1\" 200 3574 \"-\" \"Mozilla/5.0 (X11; U; Linux x86; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Epiphany/2.30.6 Safari/534.7";
    var recordOp = LogReport.buildLogRecord(line);
    assertTrue(recordOp.isPresent());

    var record = recordOp.get();
    assertTrue(record.ipAddress().equals("72.44.32.10"));
    assertTrue(record.logDate().equals("09/Jul/2018:15:48:20 +0200"));
    assertTrue(record.httpMethod().equals("GET"));
    assertTrue(record.url().equals("/download/counter/"));
    assertTrue(record.httpVer().equals("HTTP/1.1"));
    assertTrue(record.httpResponseStatus().equals("200"));
    assertTrue(record.otherLogText().equals("3574 \"-\" \"Mozilla/5.0 (X11; U; Linux x86; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Epiphany/2.30.6 Safari/534.7"));
        
    line = "50.112.00.11 - admin [11/Jul/2018:17:33:01 +0200] \"GET /asset.css HTTP/1.1\" 200 3574 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6";
    recordOp = LogReport.buildLogRecord(line);
    assertTrue(recordOp.isPresent());

    record = recordOp.get();
    assertTrue(record.ipAddress().equals("50.112.00.11"));
    assertTrue(record.logDate().equals("11/Jul/2018:17:33:01 +0200"));
    assertTrue(record.httpMethod().equals("GET"));
    assertTrue(record.url().equals("/asset.css"));
    assertTrue(record.httpVer().equals("HTTP/1.1"));
    assertTrue(record.httpResponseStatus().equals("200"));
    assertTrue(record.otherLogText().equals("3574 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6"));
  }

}
