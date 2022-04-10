import unittest
from log_report import *

class Test_Log_Report(unittest.TestCase):
  @classmethod
  def setUpClass(self):
    load_report("programming-task-example-data.log")

  def test_ip_addresses(self):
    self.assertTrue(ip_addresses.get("168.41.191.40") == 4)
    self.assertTrue(ip_addresses.get("50.112.00.11") == 3)
    self.assertTrue(ip_addresses.get("72.44.32.11") == 1)

  def test_top_most_visited_urls(self):
    visited_urls = top_most_visited_urls(3)
    (url, nbrOfVisited) = visited_urls[0]
    self.assertTrue(url == "/docs/manage-websites/" and nbrOfVisited == 2)

    (url, nbrOfVisited) = visited_urls[1]
    self.assertTrue(nbrOfVisited == 1)
    
    (url, nbrOfVisited) = visited_urls[2]
    self.assertTrue(nbrOfVisited == 1)
      
  def test_top_most_active_ip_address(self):
    top_active_ip_addressess = top_most_active_ip_address(3)
    self.assertTrue(len(top_active_ip_addressess) == 3)

    (ip, nbrOfRows) = top_active_ip_addressess[0]
    self.assertTrue(ip == "168.41.191.40" and nbrOfRows == 4)

    (ip, nbrOfRows) = top_active_ip_addressess[1]
    self.assertTrue(nbrOfRows == 3)

    (ip, nbrOfRows) = top_active_ip_addressess[2]
    self.assertTrue(nbrOfRows == 3)
      
  def test_nbr_of_unique_ip_address(self):
    self.assertTrue(nbr_of_unique_ip_address() == 11)

  def test_unmapped_rows(self):
    self.assertTrue(len(unmapped_rows) == 0)
    
  def test_build_log_record(self):
    line = '72.44.32.10 - - [09/Jul/2018:15:48:20 +0200] "GET /download/counter/ HTTP/1.1" 200 3574 "-" "Mozilla/5.0 (X11; U; Linux x86; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Epiphany/2.30.6 Safari/534.7"'
    record = build_log_record(line)
    self.assertTrue(record)
    self.assertTrue(record[0] == "72.44.32.10")
    self.assertTrue(record[1] == "09/Jul/2018:15:48:20 +0200")
    self.assertTrue(record[2] == "GET")
    self.assertTrue(record[3] == "/download/counter/")
    self.assertTrue(record[4] == "HTTP/1.1")
    self.assertTrue(record[5] == "200")
    self.assertTrue(record[6] == '3574 "-" "Mozilla/5.0 (X11; U; Linux x86; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Epiphany/2.30.6 Safari/534.7"')
        
    line = '50.112.00.11 - admin [11/Jul/2018:17:31:05 +0200] "GET /hosting/ HTTP/1.1" 200 3574 "-" "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6"' 
    record = build_log_record(line)
    self.assertTrue(record)
    self.assertTrue(record[0] == "50.112.00.11")
    self.assertTrue(record[1] == "11/Jul/2018:17:31:05 +0200")
    self.assertTrue(record[2] == "GET")
    self.assertTrue(record[3] == "/hosting/")
    self.assertTrue(record[4] == "HTTP/1.1")
    self.assertTrue(record[5] == "200")
    self.assertTrue(record[6] == '3574 "-" "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6"')

if __name__ == '__main__':
    unittest.main()
