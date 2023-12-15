package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="TeleOpFinal", group="Iterative Opmode")
//@Disabled
public class TeleOpButTest extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx FrontLeftMotor;
    private DcMotorEx FrontRightMotor;
    private DcMotorEx RearLeftMotor;
    private DcMotorEx RearRightMotor;
    private DcMotorEx LiftMotor;
    private Servo ServoClaw;

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
        ServoClaw = hardwareMap.get(Servo.class, "ServoClaw");
        int startPosititon = 0;
        ServoClaw.setPosition(startPosititon);

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

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    long driveUntil, driveClaw;
    long currentLevel = 0, wantedLevel = 0;
    int useClaw = -1;
    int numberLeftPresses = 0;
    int numberXPresses = 0;
    int numberBPresses = 0;
    boolean esteActivatClaw = false;
    boolean LeftBumperPressed = false;


    @Override
    public void loop(){
        double frontLeftPower;
        double rearLeftPower;
        double frontRightPower;
        double rearRightPower;

        double drive = gamepad1.left_stick_y;
        double turn = gamepad1.left_stick_x;
        double strafe = gamepad1.right_stick_x;

        frontLeftPower = Range.clip(drive + turn, -0.69, 0.69);                  //motoarele care
        if(frontLeftPower == 0)                                                                  //deplaseaza robotul
            frontLeftPower = Range.clip(strafe, -0.69, 0.69);
        FrontLeftMotor.setPower(frontLeftPower);

        rearLeftPower = Range.clip(drive + turn, -0.69, 0.69);
        if(rearLeftPower == 0)
            rearLeftPower = Range.clip(-strafe, -0.69, 0.69);
        RearLeftMotor.setPower(rearLeftPower);

        frontRightPower = Range.clip(drive - turn, -0.69, 0.69);
        if(frontRightPower == 0)
            frontRightPower = Range.clip(-strafe, -0.69, 0.69);
        FrontRightMotor.setPower(frontRightPower);

        rearRightPower = Range.clip(drive - turn, -0.69, 0.69);
        if(rearRightPower == 0)
            rearRightPower = Range.clip(strafe, -0.69, 0.69);
        RearRightMotor.setPower(rearRightPower);

        if(LiftMotor.getPower() == 0)                                               //sa nu se coboare liftul cand e sus
            LiftMotor.setPower(-0.15);

        if (wantedLevel == currentLevel){
            if (gamepad1.a){
                wantedLevel = 0;
                if (currentLevel > wantedLevel){
                    driveUntil =  System.currentTimeMillis() + 450 * (currentLevel-wantedLevel);
                }
                else if(currentLevel < wantedLevel){
                    driveUntil =  System.currentTimeMillis() + 450 * (wantedLevel-currentLevel);
                }
            }

            if (gamepad1.x){
                numberXPresses++;
                wantedLevel = 1;
                if (currentLevel > wantedLevel){
                    driveUntil =  System.currentTimeMillis() + 450 * (currentLevel-wantedLevel);
                }
                else if(currentLevel < wantedLevel){
                    driveUntil =  System.currentTimeMillis() + 450 * (wantedLevel-currentLevel);
                }
            }

            if (gamepad1.y){
                wantedLevel = 2;
                if (currentLevel > wantedLevel){
                    driveUntil =  System.currentTimeMillis() + 450 * (currentLevel-wantedLevel);
                }
                else if(currentLevel < wantedLevel){
                    driveUntil =  System.currentTimeMillis() + 450 * (wantedLevel-currentLevel);
                }
            }

            if (gamepad1.b){
                numberBPresses++;
                wantedLevel = 3;
                if (currentLevel > wantedLevel){
                    driveUntil =  System.currentTimeMillis() + 450 * (currentLevel-wantedLevel);
                }
                else if(currentLevel < wantedLevel){
                    driveUntil =  System.currentTimeMillis() + 450 * (wantedLevel-currentLevel);
                }
            }
        }
        if (driveUntil > System.currentTimeMillis() ){
            if (currentLevel > wantedLevel){
                LiftMotor.setPower(0.888);
            }
            else if (currentLevel < wantedLevel){
                LiftMotor.setPower(-1.5);
            }

        }
        else{
            LiftMotor.setPower(0);
            currentLevel = wantedLevel;
        }

        if (gamepad1.left_bumper && !esteActivatClaw) {
            esteActivatClaw = true;
            LeftBumperPressed = true;
            numberLeftPresses++;
            driveClaw = System.currentTimeMillis() + 400;
        }
        if (driveClaw > System.currentTimeMillis()) {
            if (useClaw == -1)
                ServoClaw.setPosition(1);
            else ServoClaw.setPosition(0);
        }
        else if(LeftBumperPressed) {
            LeftBumperPressed = false;
            useClaw = -useClaw;
            esteActivatClaw = false;
        }


        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "frontLeft (%.2f), frontRight (%.2f), rearLeft (%.2f), rearRight (%.2f)", frontLeftPower, frontRightPower, rearLeftPower, rearRightPower);
        telemetry.addData("currentLevel", " %d", currentLevel);
        telemetry.addData("wantedLevel", " %d", wantedLevel);
        telemetry.addData("number of left bumper presses", " %d", numberLeftPresses);
        telemetry.addData("number of x presses", " %d", numberXPresses);
        telemetry.addData("number of b presses", " %d", numberBPresses);
        telemetry.addData("driveClaw = ", "%d", driveClaw);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
