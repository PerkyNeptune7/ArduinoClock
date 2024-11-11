//importing libraries for project
import java.time.*;
import java.util.Scanner;
import org.firmata4j.I2CDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.MonochromeCanvas;
import org.firmata4j.ssd1306.SSD1306;
import java.time.Clock;
import java.io.IOException;

public class Main {
    static final int D5 = 5; // buzzer pin
    public static void main(String[] args)
            throws IOException, InterruptedException {


        //Making scanner object
        Scanner hoursinput = new Scanner(System.in);

        Scanner minuteinput = new Scanner(System.in);
        System.out.print("Enter time of alarm in hours: ");

        //Scanning input of hours
        int scan = hoursinput.nextInt();

        //Scanning input of minutes
        System.out.print("Enter time of alarm in minutes: ");
        int scan2 = minuteinput.nextInt();

        //initialising arduino
        IODevice arduinoObject = new FirmataDevice("COM3");
        arduinoObject.start();
        arduinoObject.ensureInitializationIsDone();

        //getting pin of buzzer
        var buzzer= arduinoObject.getPin(D5);

        //getting pin of button
        var button= arduinoObject.getPin(6);
        //setting button as input
        button.setMode(Pin.Mode.INPUT);

        //getting pin of potentiometer
        var Potentiometer = arduinoObject.getPin(14);
        //setting it as input
        Potentiometer.setMode(Pin.Mode.ANALOG);

        // Use 0x3C for the Grove OLED
        I2CDevice i2cObject = arduinoObject.getI2CDevice((byte) 0x3C);
        // 128x64 OLED SSD1515
        SSD1306 theOledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        // Initialize the OLED (SSD1306) object
        theOledObject.init();
        theOledObject.getCanvas().setTextsize(2);

        //Making a while loop to ensure continuity
        while (true) {
            long i = Potentiometer.getValue();
            Integer potentvalue = Integer.valueOf((int)i);
            if(potentvalue > 500) {//displaying only if potentiometer value is greater than 500
                // getting timezone of place
                ZoneId timezone = ZoneId.of("America/Toronto");
                // making a clock
                Clock clock = Clock.system(timezone);
                // Getting instant time
                Instant timeinstant = clock.instant();
                ZonedDateTime time = timeinstant.atZone(clock.getZone());
                //getting hours and minutes and setting as parameters.
                int hours = time.getHour();
                int minutes = time.getMinute();
                //setting the alarm
                //condition to occur when the time of alarm = actual time
                if (scan == hours && scan2 == minutes) {
                    //second if statement for the button
                    if (button.getValue() == 1) {
                        theOledObject.clear();
                        theOledObject.getCanvas().setTextsize(1);
                        //displaying alarm off temperorily while the button is pressed
                        theOledObject.getCanvas().drawString(40, 25, "ALARM OFF");
                        theOledObject.getCanvas().drawCircle(65, 30, 30, MonochromeCanvas.Color.BRIGHT);
                        theOledObject.display();
                        //Turning off buzzer
                        buzzer.setMode(Pin.Mode.OUTPUT);
                        buzzer.setValue(0);
                    } //keeping the alarm on if the button is not pressed.
                    else {
                        //Turing on buzzer
                        buzzer.setMode(Pin.Mode.SERVO);
                        theOledObject.getCanvas().setTextsize(2);
                        theOledObject.clear();
                        //Flashing 'alarm' on oled screen
                        theOledObject.getCanvas().drawString(35, 20, "ALARM");
                        theOledObject.display();

                    }
                }//else of first if-else statement to make sure the current time is displayed before and after the time of alarm
                else {
                    buzzer.setMode(Pin.Mode.OUTPUT);
                    buzzer.setValue(0);
                    theOledObject.getCanvas().setTextsize(2);
                    theOledObject.clear();
                    theOledObject.getCanvas().drawString(35, 20, (String.valueOf(hours) + ":" + String.valueOf(minutes)));//displaying current time
                    theOledObject.display();
                    Thread.sleep(5000);
                }
            }
            else{//turning off everything if potentiometer value is less than 500
            buzzer.setMode(Pin.Mode.OUTPUT);
            buzzer.setValue(0);
            theOledObject.clear();
            theOledObject.display();
            }
            System.out.println("The value of potentiometer is : "+ Potentiometer.getValue());
        }
    }
}

