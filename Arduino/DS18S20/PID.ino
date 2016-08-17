//Timers
long previousMillis = 0; 
long interval = 250;

//Heat
float temperature = 0;

void setup()
{
  Serial.begin(9600);
  blinkSetup();

}

void loop()
{
  unsigned long currentMillis = millis();
  if(currentMillis - previousMillis >= interval)
  {
    previousMillis = currentMillis; 
    temperature = getTemp();
    Serial.println(temperature);
    blink();
  }  
  delay(1); 
}
