#! /usr/bin/env python3

import os
import time
import re

# DigIo - Programming Task
# python 3.9
# author: Carlos Lucena

pattern = r"^(\d{1,3}\.\d\d{1,3}\.\d{1,3}\.\d{1,3}) .* \[(.*)\] \"([A-Z]{3,7}) (.+?) (.+?)\".(\d{3}) (.*)$"

def build_log_record(line):
  """
  (ip_address, log_date, http_method, url, http_version, http_response_status, remaining_text)
  return a  mappedRow or  None 
  """
  match = re.match(pattern, line)
  if match:
    return (match.group(1), match.group(2), match.group(3), match.group(4), match.group(5), match.group(6), match.group(7))
  else:
    return None  

ip_addresses = {}
visited_urls = {}
unmapped_rows = []

def load_report(logfile):
  with open(logfile, mode="r") as reader:
    for line in reader.readlines():
      row = build_log_record(line)
      if row:
        ip_addresses[row[0]] = ip_addresses.get(row[0], 0) + 1
        visited_urls[row[3]] = visited_urls.get(row[3], 0) + 1
      else:
        unmapped_rows.append(line)

def nbr_of_unique_ip_address():
 """
 () -> total_Number_Of_Unique_Ip_Address
 """
 return len(ip_addresses)
  
def top_most_visited_urls(limit):
  """
  (limitOfRows) -> List((urlName, numberOfVisits)) 
  """
  return sorted(visited_urls.items(), key=lambda x: x[1], reverse=True)[0:limit]

def top_most_active_ip_address(limit):
  """
  (limitOfRows) -> List((ipAddress, numberOfRows)) 
  """
  return sorted(ip_addresses.items(), key=lambda x: x[1], reverse=True)[0:limit]
          
def main(logfile):
  start_time = time.time()
  print("*** Log Report ***")
  load_report(logfile)
  
  print(f"Number of unique IP addresses: {nbr_of_unique_ip_address()}")

  mostVisited = ("\n  ").join([f"{url}({nbrOfVisits})" for (url, nbrOfVisits) in top_most_visited_urls(3)])
  print("Top 3 most visited URLs:\n  " + mostVisited);

  mostActiveIpAddress = ("\n  ").join([f"{ipAddress}({nbrOfRows})" for (ipAddress, nbrOfRows) in top_most_active_ip_address(3)])
  print("Top 3 most active IP addresses:\n  " + mostActiveIpAddress);

  if len(unmapped_rows) > 0:
    print(f"Warning: unable to match {len(unmapped_rows)} row(s)");


  duration = time.time() - start_time
  print(f"Report total elapse time: {duration}")
  print(f"*** End of Log Report ***")
  
if __name__ == "__main__":
  logfile = "programming-task-example-data.log"
  main(logfile)
