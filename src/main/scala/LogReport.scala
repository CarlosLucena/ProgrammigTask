import scala.io.Source

case class LogRecord(ipAddress:String, logDate:String, httpMethod:String,
  url:String, httpVersion:String, httpResponseStatus:String, 
  remainingText:String) 

class LogReport( val ipAddresses: Map[String, Int], 
  val visitedUrls: Map[String, Int], 
  val unmappedRows: List[String]):

  def nbrOfUniqueIpAddress(): Int = ipAddresses.size

  def topMostVisitedUrls(limit: Int): List[(String, Int)] =
    visitedUrls.toList.sortBy(- _._2).take(limit)

  def topMostActiveIpAddresses(limit: Int): List[(String, Int)] =
    ipAddresses.toList.sortBy(- _._2).take(limit)

object LogReport { 
  import java.time.*

  def apply(logfile: String): LogReport = 
    def incrementMapCounter(map: Map[String, Int], key: String): (String, Int) = 
      map.get(key).map(x => key -> (x + 1)).getOrElse(key -> 1)

    var ipAddresses = Map[String, Int]()
    var visitedUrls = Map[String, Int]()
    var unmappedRows = List[String]();
    val reader = Source.fromFile(logfile)
    try
      for (line <- reader.getLines) 
        buildRecord(line) match 
          case Some(logRecord) =>  
            ipAddresses += incrementMapCounter(ipAddresses, logRecord.ipAddress)
            visitedUrls += incrementMapCounter(visitedUrls, logRecord.url)
          case None => line :: unmappedRows
    finally 
      reader.close()

    new LogReport(ipAddresses, visitedUrls, unmappedRows)

  def main(args: Array[String]): Unit = 
    val startTime = LocalTime.now
    println("*** Log Report ***");

    val logfile = 
      if args.size > 0 then args(0)
      else "programming-task-example-data.log"

    val logReport = LogReport(logfile)
    println(s"Number of unique IP addresses: ${logReport.nbrOfUniqueIpAddress()}")
    
    val mostVisited = logReport.topMostVisitedUrls(3)
      .map {(url, nbrOfVisits) => s"$url ($nbrOfVisits)"} 
      .mkString("\n  ")
    println(s"Top 3 most visited URLs:\n  $mostVisited");

    val mostActiveIpAddress = logReport.topMostActiveIpAddresses(3)
      .map {(ip, nbrOfRows) => s"$ip ($nbrOfRows)"}
      .mkString("\n  ") 
    println(s"Top 3 most active IP addresses:\n  $mostActiveIpAddress");

    if logReport.unmappedRows.size > 0 then
      println(s"Warning: unable to match ${logReport.unmappedRows} row(s)")

    val elapseTime = Duration.between(startTime, LocalTime.now)
    println(s"Report total elapse time: ${elapseTime.getSeconds()}s")
    println("*** End of Log Report ***")

  import scala.util.matching.Regex
  private val pattern = """^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}) .* \[(.*)\] \"([A-Z]{3,7}) (.+?) (.+?)\".(\d{3}) (.*)$""".r
  def buildRecord(text: String): Option[LogRecord] =
    text match {
      case pattern(ipAddress, logDate, httpMethod, url, httpVersion, httpResponseStatus, remainingText) =>
        Some(LogRecord(ipAddress, logDate, httpMethod, url, httpVersion, httpResponseStatus, remainingText))
      case _ => None
    }
}
