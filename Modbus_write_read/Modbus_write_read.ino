/**
 *  Modbus master example 2:
 *  The purpose of this example is to query several sets of data
 *  from an external Modbus slave device. 
 *  The link media can be USB or RS232.
 *
 *  Recommended Modbus slave: 
 *  diagslave http://www.modbusdriver.com/diagslave.html
 *
 *  In a Linux box, run 
 *  "./diagslave /dev/ttyUSB0 -b 19200 -d 8 -s 1 -p none -m rtu -a 1"
 * 	This is:
 * 		serial port /dev/ttyUSB0 at 19200 baud 8N1
 *		RTU mode and address @1
 */

#include <ModbusRtu.h>
#include <SoftwareSerial.h>

uint16_t au16data[16]; //!< data array for modbus network sharing
uint8_t u8state; //!< machine state
uint8_t u8query; //!< pointer to message query
uint16_t q[9];

/**
 *  Modbus object declaration
 *  u8id : node id = 0 for master, = 1..247 for slave
 *  u8serno : serial port (use 0 for Serial)
 *  u8txenpin : 0 for RS-232 and USB-FTDI 
 *               or any pin number > 1 for RS-485
 */
Modbus master(0); // this is master and RS-232 or USB-FTDI

/**
 * This is an structe which contains a query to an slave device
 */
modbus_t telegram[4];
modbus_t telegram2[4];

unsigned long u32wait;
SoftwareSerial mySerial(10, 11);
int regg=0;
int regg2=528;
int data;

void setup() {
  // Odczyt rejestrów
  telegram[0].u8id = 1; // slave address
  telegram[0].u8fct = 3; // function code (this one is registers read)
  telegram[0].u16RegAdd = 5; // start address in slave
  telegram[0].u16CoilsNo = 1; // number of elements (coils or registers) to read
  telegram[0].au16reg = au16data; // pointer to a memory array in the Arduino

  
  telegram[1].u8id = 1; // slave address
  telegram[1].u8fct = 3; // function code (this one is write a single register)
  telegram[1].u16RegAdd = 7; // start address in slave
  telegram[1].u16CoilsNo = 1; // number of elements (coils or registers) to read
  telegram[1].au16reg = au16data+1; // pointer to a memory array in the Arduino

  telegram[2].u8id = 1; // slave address
  telegram[2].u8fct = 3; // function code (this one is write a single register)
  telegram[2].u16RegAdd = 528; // start address in slave 4608-timer / 4684 - licznik (problem z zapisem wartosci zadanej )
  telegram[2].u16CoilsNo = 2; // number of elements (coils or registers) to read
  telegram[2].au16reg = au16data+2; // pointer to a memory array in the Arduino

  telegram[3].u8id = 1; // slave address
  telegram[3].u8fct = 3; // function code (this one is write a single register)
  telegram[3].u16RegAdd = 2833; // start address in slave 4608-timer / 4684 - licznik (problem z zapisem wartosci zadanej )
  telegram[3].u16CoilsNo = 1; // number of elements (coils or registers) to read
  telegram[3].au16reg = au16data+6; // pointer to a memory array in the Arduino
  
// Zapis rejestrów
 
  telegram2[0].u8id = 1; // slave address
  telegram2[0].u8fct = 6; // function code (this one is write a single register)
  telegram2[0].u16RegAdd = 7; // Zapis wartosci zadanej do wyjsc nieuzywanych w programie czyt. Q7/Q8
  telegram2[0].u16CoilsNo = 1; // number of elements (coils or registers) to read
  telegram2[0].au16reg = au16data+8; // pointer to a memory array in the Arduino

  telegram2[1].u8id = 1; // slave address
  telegram2[1].u8fct = 6; // function code (this one is write a single register)
  telegram2[1].u16RegAdd = 4864; // Licznik zapis wartości zadanej 
  telegram2[1].u16CoilsNo = 1; // number of elements (coils or registers) to read
  telegram2[1].au16reg = au16data+9; // pointer to a memory array in the Arduino

  telegram2[2].u8id = 1; // slave address
  telegram2[2].u8fct = 6; // function code (this one is write a single register)
  telegram2[2].u16RegAdd = 9729; // Miejsce klejenia I - DR02 - zapis wartosci zadanej
  telegram2[2].u16CoilsNo = 1; // number of elements (coils or registers) to read
  telegram2[2].au16reg = au16data+10; // pointer to a memory array in the Arduino

  telegram2[3].u8id = 1; // slave address
  telegram2[3].u8fct = 6; // function code (this one is write a single register)
  telegram2[3].u16RegAdd = 9731; // Miejsce klejenia II- DR04- zapis wartosci zadanej
  telegram2[3].u16CoilsNo = 1; // number of elements (coils or registers) to read
  telegram2[3].au16reg = au16data+11; // pointer to a memory array in the Arduino

/* DR02 i DR04 2601 i 2603 - dlugosc klejenia */
  
	Serial.begin(9600);
  master.begin(&mySerial, 9600 ); // baud-rate
 master.setTimeOut(300 ); // if there is no answer in 50 ms, roll over
  u32wait = millis()+200 ;
  u8state = u8query = 0; 
  pinMode(9, OUTPUT);
  //pinMode(2, INPUT_PULLUP);
  //attachInterrupt(digitalPinToInterrupt(2), onStep, FALLING);

}

void loop() {


/*if(Serial.read()==49)
digitalWrite(2, LOW);
else {digitalWrite(2,HIGH);*/
  switch( u8state ) {
  case 0: 
  digitalWrite(9, LOW);
    if (millis() > u32wait) u8state++; // wait state
    break;
  case 1: 
  digitalWrite(9, HIGH);
    master.query( telegram[u8query] ); // send query (only once)
    u8state++;
	u8query++;
	if (u8query > 5) u8query = 0;
    break;
  case 2:
  digitalWrite(9, LOW);
    master.poll(); // check incoming messages
    if (master.getState() == COM_IDLE) {
      u8state = 0;
     u32wait = millis()+200;
       Serial.print("I");
       Serial.println(au16data[0]);
       delay(50);
       Serial.print("O");
       Serial.println(au16data[1]);
       delay(50);
       Serial.print("XC");
       Serial.println(au16data[2]);
       delay(50);
       Serial.print("SK");
       Serial.println(au16data[6]/2.5);
       delay(50);
       
  // au16data[6]=30;
   //Serial.print((int)au16data[6]);
 //data=Serial.parseInt();
 //au16data[4]=data;
 //Serial.println(au16data[4]);
   //data=Serial.read();
      //String A = A+data;
     //Serial.println(data);
    }
    break;
  } 
//au16data[4]=regg;



}  

/* void onStep()
{
  static unsigned long lastTime;
  unsigned long timeNow = millis();
  if (timeNow - lastTime < 50)
    return;

  lastTime = timeNow;} */

