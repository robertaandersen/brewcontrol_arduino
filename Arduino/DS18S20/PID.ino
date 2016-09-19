//Timers
long previousMillis = 0;
long interval = 250;
int duration = 0;
float sum = 0;


//Heat
float temperature = 0;

void setup()
{
  Serial.begin(9600);
  //blinkSetup();

}

void loop()
{
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval)
  {
    previousMillis = currentMillis;
    float t = getTemp();

    //Assume positive celsius
    int retry = 0;
    if (t <= 0 ) {
      while (retry <= 100 || t <= 0) {
        t = getTemp();
        retry++;
      }
    }

    if (t > 0) {
      sum += t;
    }
    else {
      sum += getAverage();
    }

    setCurrentMeasure(duration, t, getAverage(), lastError);
    //Serial.println(getJSON());
    Serial.println(t);


  }
  delay(10);
  duration += 1;
}

float getAverage() {
  if (duration == 0 ) {
    return sum;
  }
  else {
    return sum / duration;
  }
}




