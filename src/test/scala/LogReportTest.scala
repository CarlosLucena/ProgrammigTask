import org.scalatest.flatspec.AnyFlatSpec
import LogReport.*

class LogReportTest extends AnyFlatSpec {
  it should "match regex line 1" in {
    val line =
      """72.44.32.10 - - [09/Jul/2018:15:48:20 +0200] "GET /download/counter/ HTTP/1.1" 200 3574 "-" "Mozilla/5.0 (X11; U; Linux x86; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Epiphany/2.30.6 Safari/534.7""""
    LogReport.buildRecord(line) match {
      case Some(logRecord) =>
        assert(logRecord.ipAddress == "72.44.32.10")
        assert(logRecord.logDate == "09/Jul/2018:15:48:20 +0200")
        assert(logRecord.httpMethod == "GET")
        assert(logRecord.url == "/download/counter/")
        assert(logRecord.httpVersion == "HTTP/1.1")
        assert(logRecord.httpResponseStatus == "200")
        assert(
          logRecord.remainingText == """3574 "-" "Mozilla/5.0 (X11; U; Linux x86; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Epiphany/2.30.6 Safari/534.7""""
        )
      case _ => fail()
    }
  }

  it should "match regex line 2" in {
    val line =
      """50.112.00.11 - admin [11/Jul/2018:17:31:05 +0200] "GET /hosting/ HTTP/1.1" 200 3574 "-" "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6""""
    LogReport.buildRecord(line) match {
      case Some(logRecord) =>
        assert(logRecord.ipAddress == "50.112.00.11")
        assert(logRecord.logDate == "11/Jul/2018:17:31:05 +0200")
        assert(logRecord.httpMethod == "GET")
        assert(logRecord.url == "/hosting/")
        assert(logRecord.httpVersion == "HTTP/1.1")
        assert(logRecord.httpResponseStatus == "200")
        assert(
          logRecord.remainingText == """3574 "-" "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6""""
        )
      case _ => fail()
    }
  }

  val logReport = LogReport("programming-task-example-data.log")
  it should "have 11 unique IPs" in {
    assert(logReport.nbrOfUniqueIpAddress() == 11)
  }

  it should "map all test rows" in {
    assert(logReport.unmappedRows.isEmpty)
  }

  it should "map IP address" in {
    assert(logReport.ipAddresses("168.41.191.40") == 4)
    assert(logReport.ipAddresses("50.112.00.11") == 3)
    assert(logReport.ipAddresses("72.44.32.11") == 1)
  }

  it should "have top visited urls" in {
    val topVisited = logReport.topMostVisitedUrls(3)
    assert(topVisited.size == 3)

    assert(topVisited(0) == ("/docs/manage-websites/", 2))
  }

  it should "have top active ip address" in {
    val topActive = logReport.topMostActiveIpAddresses(3)
    assert(topActive.size == 3)

    assert(topActive(0) == ("168.41.191.40", 4))
  }
}
