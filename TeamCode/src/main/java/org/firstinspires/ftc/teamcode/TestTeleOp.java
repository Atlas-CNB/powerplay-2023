/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="TestTeleOp", group="Iterative Opmode")
//@Disabled
public class TestTeleOp extends OpMode
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

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
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

        // Tell the driver that initialization is complete.
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
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double frontLeftPower;
        double rearLeftPower;
        double frontRightPower;
        double rearRightPower;

        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        double drive = gamepad1.left_stick_y;
        double turn = gamepad1.left_stick_x;
        double strafe = gamepad1.right_stick_x;

        frontLeftPower = Range.clip(drive + turn, -0.69, 0.69);
        rearLeftPower = Range.clip(drive + turn, -0.69, 0.69);
        frontRightPower = Range.clip(drive - turn, -0.69, 0.69);
        rearRightPower = Range.clip(drive - turn, -0.69, 0.69);

        if(frontLeftPower == 0 && frontRightPower == 0 && rearLeftPower == 0 && rearRightPower == 0){
            frontLeftPower = Range.clip(strafe, -0.69, 0.69);
            rearLeftPower = Range.clip(-strafe, -0.69, 0.69);
            frontRightPower = Range.clip(-strafe, -0.69, 0.69);
            rearRightPower = Range.clip(strafe, -0.69, 0.69);
        }

        if(gamepad1.a){
            heightOne();
        }

        // Send calculated power to wheels
//        leftDrive.setPower(leftPower);
//        rightDrive.setPower(rightPower);
        FrontRightMotor.setPower(frontRightPower);
        FrontLeftMotor.setPower(frontLeftPower);
        RearRightMotor.setPower(rearRightPower);
        RearLeftMotor.setPower(rearLeftPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "frontLeft (%.2f), frontRight (%.2f), rearLeft (%.2f), rearRight (%.2f)", frontLeftPower, frontRightPower, rearLeftPower, rearRightPower);
    }

    public void heightOne(){
        LiftMotor.setPower(2.0);
        long driveUntil = System.currentTimeMillis() + 500;
        while(true){
            if(driveUntil < System.currentTimeMillis()) {
                LiftMotor.setPower(0);
                return;
                }
            double frontLeftPower;
            double rearLeftPower;
            double frontRightPower;
            double rearRightPower;

            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = gamepad1.left_stick_y;
            double turn = gamepad1.left_stick_x;
            double strafe = gamepad1.right_stick_x;

            frontLeftPower = Range.clip(drive + turn, -0.69, 0.69);
            rearLeftPower = Range.clip(drive + turn, -0.69, 0.69);
            frontRightPower = Range.clip(drive - turn, -0.69, 0.69);
            rearRightPower = Range.clip(drive - turn, -0.69, 0.69);

            if (frontLeftPower == 0 && frontRightPower == 0 && rearLeftPower == 0 && rearRightPower == 0) {
                frontLeftPower = Range.clip(strafe, -0.69, 0.69);
                rearLeftPower = Range.clip(-strafe, -0.69, 0.69);
                frontRightPower = Range.clip(-strafe, -0.69, 0.69);
                rearRightPower = Range.clip(strafe, -0.69, 0.69);
            }

            FrontRightMotor.setPower(frontRightPower);
            FrontLeftMotor.setPower(frontLeftPower);
            RearRightMotor.setPower(rearRightPower);
            RearLeftMotor.setPower(rearLeftPower);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "frontLeft (%.2f), frontRight (%.2f), rearLeft (%.2f), rearRight (%.2f)", frontLeftPower, frontRightPower, rearLeftPower, rearRightPower);

        }

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
