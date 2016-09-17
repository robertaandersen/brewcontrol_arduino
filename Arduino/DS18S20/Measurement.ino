//JSON
String JSONBegin = "{";
String JSONEnd = "}";
String currentReading;

void setCurrentMeasure(int dur, float t, float average, String lastError) {
  currentReading = "\"temperature\":" + String(t) + ",\"duration\":" + String(dur) + ",\"averageTemp\":" + String(average) + ",\"error\":\""+lastError+"\"";
}

String getJSON() {
  String ret = JSONBegin;
  ret += "\"currentReading\":{"+ currentReading +"}";
  ret += JSONEnd;
  return ret; 
  
  /*if (ret.length() > 0) {
    return ret.substring(0, ret.length() - 1);
  }
  else {
    return ret;
  }*/
  //return JSONBegin +currentJSON +JSONEnd ;
}
