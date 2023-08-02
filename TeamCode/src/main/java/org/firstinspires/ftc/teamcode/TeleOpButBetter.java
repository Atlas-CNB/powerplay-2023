package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="TeleOpButBetter", group="Iterative Opmode")
//@Disabled
public class TeleOpButBetter extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx FrontLeftMotor;
    private DcMotorEx FrontRightMotor;
    private DcMotorEx RearLeftMotor;
    private DcMotorEx RearRightMotor;
    private DcMotorEx LiftMotor;

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
    long driveUntil;
    long nivelLift = 0;
    long butonApasat = 0;
    boolean nivelschimbabil;

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
            LiftMotor.setPower(0.15);                                               //                              (amogus)



        if(driveUntil < System.currentTimeMillis() && nivelschimbabil) {            //se da puterea la lift
            LiftMotor.setPower(0);
            nivelLift = butonApasat;
            nivelschimbabil = false;
        }
        else if(driveUntil > System.currentTimeMillis()){
            if(butonApasat > nivelLift)
                LiftMotor.setPower(2.0);
            else
                LiftMotor.setPower(-2.0);
        }



        if(gamepad1.x && nivelLift == 0) {                                                          //butonul x
            driveUntil = System.currentTimeMillis() + 350;
            nivelschimbabil = true;
            butonApasat = 1;
            }
        else if(gamepad1.x && nivelLift > 1){
            driveUntil = (nivelLift - 1) * 250 + System.currentTimeMillis();
            nivelschimbabil = true;
            butonApasat = 1;
            }

        if(gamepad1.y && nivelLift == 0){                                                           //butonul y
            driveUntil = System.currentTimeMillis() + 600;
            nivelschimbabil = true;
            butonApasat = 2;
        }
        else if(gamepad1.y && nivelLift > 2){
            driveUntil = (nivelLift - 2) * 250 + System.currentTimeMillis();
            nivelschimbabil = true;
            butonApasat = 2;
        }
        else if(gamepad1.y && nivelLift < 2){
            driveUntil = (2 - nivelLift) * 250 + System.currentTimeMillis();
            nivelschimbabil = true;
            butonApasat = 2;
        }

        if(gamepad1.b && nivelLift == 0){                                                           //butonul b
            driveUntil = System.currentTimeMillis() + 850;
            nivelschimbabil = true;
            butonApasat = 3;
        }
        else if(gamepad1.b && nivelLift < 3){
            driveUntil = (3 - nivelLift) * 250 + System.currentTimeMillis();
            nivelschimbabil = true;
            butonApasat = 3;
        }

        if(gamepad1.a){                                                                             //butonul a
            driveUntil = nivelLift * 250 + 100 + System.currentTimeMillis();
            nivelschimbabil = true;
            butonApasat = 0;
            }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "frontLeft (%.2f), frontRight (%.2f), rearLeft (%.2f), rearRight (%.2f)", frontLeftPower, frontRightPower, rearLeftPower, rearRightPower);
        telemetry.addData("Nivel", " %d", nivelLift);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
