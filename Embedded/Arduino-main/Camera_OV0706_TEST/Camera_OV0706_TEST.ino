#include <camera_VC0706.h>
#include <SD.h>
#include <SoftwareSerial.h>

#define chipSelect 10
#if ARDUINO >= 100
SoftwareSerial cameraconnection = SoftwareSerial(2, 3);
#else
NewSoftSerial cameraconnection = NewSoftSerial(2, 3);
#endif
camera_VC0706 cam = camera_VC0706(&cameraconnection);
void setup() {


#if !defined(SOFTWARE_SPI)
#if defined(__AVR_ATmega1280__) || defined(__AVR_ATmega2560__)
	if(chipSelect != 53) pinMode(53, OUTPUT); // SS on Mega
#else
	if(chipSelect != 10) pinMode(10, OUTPUT); // SS on Uno, etc.
#endif
#endif

	pinMode(7,INPUT_PULLUP);
	Serial.begin(9600);
	Serial.println("VC0706 Camera test");

	//SD card detection
	if (!SD.begin(chipSelect)) {
		Serial.println("Card failed, or not present");
		return;
	}

	// Query camera
	if (cam.begin()) {
		Serial.println("Camera Found:");
	} else {
		Serial.println("No camera found?");
		return;
	}
	// Camera version number
	char *reply = cam.getVersion();
	if (reply == 0) {
		Serial.print("Failed to get version");
	} else {
		Serial.println("-----------------");
		Serial.print(reply);
		Serial.println("-----------------");
	}

	// Choose the right picture size 640x480, 320x240 or 160x120
	// The larger the picture, the slower the transmission speed
	cam.setImageSize(VC0706_640x480);
	//cam.setImageSize(VC0706_320x240);
	//cam.setImageSize(VC0706_160x120);

	uint8_t imgsize = cam.getImageSize();
	Serial.print("Image size: ");
	if (imgsize == VC0706_640x480) Serial.println("640x480");
	if (imgsize == VC0706_320x240) Serial.println("320x240");
	if (imgsize == VC0706_160x120) Serial.println("160x120");

	Serial.println("Get ready !");

}

void loop() {

	if(digitalRead(7)== 0) { //Button detection
		delay(10);
		if(digitalRead(7)== 0) {
			if (! cam.takePicture())
				Serial.println("Failed to snap!");
			else
				Serial.println("Picture taken!");
			char filename[13];
			strcpy(filename, "IMAGE00.JPG");
			for (int i = 0; i < 100; i++) {
				filename[5] = '0' + i/10;
				filename[6] = '0' + i%10;
				// create if does not exist, do not open existing, write, sync after write
				if (! SD.exists(filename)) {
					break;
				}
			}
			File imgFile = SD.open(filename, FILE_WRITE);
			uint16_t jpglen = cam.frameLength();
			Serial.print(jpglen, DEC);
			Serial.println(" byte image");

			Serial.print("Writing image to ");
			Serial.print(filename);

			while (jpglen > 0) {
				// 一Read 32bytes at a time
				uint8_t *buffer;
				uint8_t bytesToRead =  min(32, jpglen); // Adjust the size of one-time read data, from 32-64byte, too large and easy to not work
				buffer = cam.readPicture(bytesToRead);
				imgFile.write(buffer, bytesToRead);
				jpglen -= bytesToRead;
			}
			imgFile.close();
			Serial.println("...Done!");
	        cam.resumeVideo();
		}
	}
}
