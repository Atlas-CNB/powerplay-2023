/*      CONTROL SCHEME
 *   left stick - miscare noramla; right stick - miscare inceata
 *   dpad din dreapta - daca se vrea U-turn
 *   y - ridica sau coboara liftul
 *   b - ridica sau coboara farasul
 *   x - ridica sau coboara bratul superior
 *   a - proneste sau opreste axul rotativ
 * */

package org.firstinspires.ftc.teamcode;

import android.opengl.Visibility;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="TeleOP_2024_V1", group="Iterative Opmode")
//@Disabled
public class TeleOP_2024_V1 extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx FrontLeftMotor;
    private DcMotorEx FrontRightMotor;
    private DcMotorEx RearLeftMotor;
    private DcMotorEx RearRightMotor;
    private DcMotorEx LiftMotor;
    private DcMotorEx AxleMotor;
    private Servo ServoFaras;
    private Servo ServoBratSuperior;

    double startPositionBrat;

    double startPositionFaras;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        FrontLeftMotor = hardwareMap.get(DcMotorEx.class, "FrontLeftMotor");
        FrontRightMotor = hardwareMap.get(DcMotorEx.class, "FrontRightMotor");
        RearLeftMotor = hardwareMap.get(DcMotorEx.class, "RearLeftMotor");
        RearRightMotor = hardwareMap.get(DcMotorEx.class, "RearRightMotor");
        LiftMotor = hardwareMap.get(DcMotorEx.class, "LiftMotor");
        AxleMotor = hardwareMap.get(DcMotorEx.class, "AxleMotor");
        ServoFaras = hardwareMap.get(Servo.class, "ServoFaras");
        ServoBratSuperior = hardwareMap.get(Servo.class, "ServoBratSuperior");

        startPositionFaras = 0.43;
        startPositionBrat = 0.00;
        telemetry.addData("startPositionFaras este ", "%f", startPositionFaras);
        telemetry.addData("startPositionBratSup este ", "%f", startPositionBrat);
        ServoFaras.setPosition(startPositionFaras);
        ServoBratSuperior.setPosition(startPositionBrat);

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        FrontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        RearLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }


    long driveUntil = 0;
    long driveFaras = 0;
    long driveBrat = 0;
    long AxleSafety = 0;
    long pozitieExtraFaras = 0;

    int wantedLevel = 0;
    int wantedFarasLevel = 0;
    int wantedBratLevel = 0;
    boolean AxleRun = false;
    boolean isIncremented = false;
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop(){
        double frontLeftPower = 0;
        double rearLeftPower = 0;
        double frontRightPower = 0;
        double rearRightPower = 0;

        //pt operatii cu valorile joystick-ului
        double yAxis = gamepad1.left_stick_y;
        double xAxis = gamepad1.left_stick_x;

        //asemenea yAxis si xAxis
        double yAxisFin = gamepad1.right_stick_y;
        double xAxisFin = gamepad1.right_stick_x;

        //sa semnalizeze daca se doreste un U-turn in loc de miscare normala
        boolean leftUTurn = false;
        boolean rightUturn = false;

        if(gamepad1.dpad_right)
            rightUturn = true;
        if(gamepad1.dpad_left)
            leftUTurn = true;

        if(!leftUTurn && !rightUturn) {

            //miscare normala
            if (yAxisFin == 0 && xAxisFin == 0) {
                frontRightPower = Range.clip(yAxis + xAxis, -0.7, 0.7);
                frontLeftPower = Range.clip(yAxis - xAxis, -0.7, 0.7);
                rearRightPower = Range.clip(yAxis - xAxis, -0.7, 0.7);
                rearLeftPower = Range.clip(yAxis + xAxis, -0.7, 0.7);
            }

            //miscare fina (inceata)
            if (yAxis == 0 && xAxis == 0) {
                frontRightPower = Range.clip(yAxisFin + xAxisFin, -0.3, 0.3);
                frontLeftPower = Range.clip(yAxisFin - xAxisFin, -0.3, 0.3);
                rearRightPower = Range.clip(yAxisFin - xAxisFin, -0.3, 0.3);
                rearLeftPower = Range.clip(yAxisFin + xAxisFin, -0.3, 0.3);
            }

        }

        else{

            //daca se doreste un U-turn spre dreapta
            if(rightUturn) {
                frontRightPower = 0.7;
                frontLeftPower = -0.7;
                rearLeftPower = -0.7;
                rearRightPower = 0.7;
            }

            //daca se doreste un U-turn spre stanga
            else if(leftUTurn){
                frontRightPower = -0.7;
                frontLeftPower = 0.7;
                rearLeftPower = 0.7;
                rearRightPower = -0.7;
            }

        }


        if(gamepad1.a && (!AxleRun) && AxleSafety <= System.currentTimeMillis()){                 //daca vrem sa rotim axul
            AxleSafety = System.currentTimeMillis() + 200;                          //sa nu simta o apasare dret mai multe
            AxleRun = true;
        }
        if(gamepad1.a && AxleRun && AxleSafety <= System.currentTimeMillis()) {       //daca vrem sa oprim axul
            AxleRun = false;
            AxleSafety = System.currentTimeMillis() + 200;
            }


        //sa nu se coboare liftul cand e sus
        /*if(LiftMotor.getPower() == 0)
            LiftMotor.setPower(0.15);

        //daca se apasa y, se doreste schimbarea pozitiei liftului
        if(gamepad1.y){
            //daca nu i s-a dat deja o astfel de comanda
            if(driveUntil <= System.currentTimeMillis()) {
                //daca litul e sus, vrem sa ajunga jos si viceversa
                wantedLevel++;
                //se va misca timp de 750 milisecunde
                driveUntil = System.currentTimeMillis() + 900;
            }
        }
        //daca trebuie miscat liftul
        if(driveUntil > System.currentTimeMillis()){
            if(wantedLevel % 2 == 1)
                LiftMotor.setPower(0.7);
            else LiftMotor.setPower(-0.20);
        }*/
        //else LiftMotor.setPower(0.10);             //sa vad daca merge fara asta


        //daca trebuie ridicat sau coborat bratul
        if(gamepad1.x){
            if (driveBrat <= System.currentTimeMillis()) {
                if(wantedFarasLevel % 2 == 1) {
                    driveBrat = System.currentTimeMillis() + 250;
                    wantedBratLevel++;
                }
            }
        }
        if(driveBrat > System.currentTimeMillis()){
            if(wantedBratLevel % 2 == 1) {
                ServoBratSuperior.setPosition(1.00);
                if(!isIncremented) {
                    pozitieExtraFaras = System.currentTimeMillis() + 175;
                    wantedFarasLevel++;
                    isIncremented = true;
                }
            }
            else ServoBratSuperior.setPosition(startPositionBrat);
        }
        else isIncremented = false;

        if(pozitieExtraFaras <= System.currentTimeMillis() && isIncremented)
            ServoFaras.setPosition(0.7);


        //daca trebuie ridicat sau coborat farasul
        if(gamepad1.b) {
            if(driveFaras <= System.currentTimeMillis()) {
                driveFaras = System.currentTimeMillis() + 350;
                wantedFarasLevel++;
            }
        }
        if(driveFaras > System.currentTimeMillis()){
            if(wantedFarasLevel % 2 == 1)
                ServoFaras.setPosition(0.2);
            else ServoFaras.setPosition(startPositionFaras);
        }


        //Se da putere motoarelor
        if(AxleRun)
            AxleMotor.setPower(-0.70);
        else if(!AxleRun)
            AxleMotor.setPower(0);
        FrontRightMotor.setPower(frontRightPower);
        FrontLeftMotor.setPower(frontLeftPower);
        RearRightMotor.setPower(rearRightPower);
        RearLeftMotor.setPower(rearLeftPower);


        // Show the elapsed game time and wheel power.
        telemetry.addData("acum bratul este in pozitia", "%f", ServoBratSuperior.getPosition());
        telemetry.addData("acum farasul este in pozitia", "%f", ServoFaras.getPosition());
        telemetry.addData("Wanted lift level este ", "%d", wantedLevel);
        telemetry.addData("Wanted faras level este ", "%d", wantedFarasLevel);
        telemetry.addData("driveuntil la lift este ", "%d", driveUntil);
        telemetry.addData("AxleRun este pe modul ", "%b", AxleRun);
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "frontLeft (%.2f), frontRight (%.2f), rearLeft (%.2f), rearRight (%.2f)", frontLeftPower, frontRightPower, rearLeftPower, rearRightPower);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}