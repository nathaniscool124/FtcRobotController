package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "ByteSize TeleOp");
// This will be the "auto" stage of the game.

public class bytesizeteleop extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Connect to the motors (names must match the robot configuration)
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");

        waitForStart();

        // Drive forward at half power for 1 second
        rightDrive.setPower(0.5);
        leftDrive.setPower(0.5);
        sleep(1000);


        // Stop
        rightDrive.setPower(0);
        leftDrive.setPower(0);

        if (gamepad1.dpad_up) {
            leftDrive.setPower(1);
        }else{
            leftDrive.setPower(0);
        }

        if (gamepad1.dpad_down) {
            leftDrive.setDirection(DcMotor.Direction.REVERSE);
            leftDrive.setPower(1);
        }else{
            leftDrive.setDirection(DcMotor.Direction.FORWARD);
            leftDrive.setPower(0);
        }

        if (gamepad1.triangle) {
            rightDrive.setPower(1);
        }else{
            rightDrive.setPower(0);
        }

        if (gamepad1.cross) {
            rightDrive.setDirection(DcMotor.Direction.REVERSE);
            rightDrive.setPower(1);
        }else{
            rightDrive.setDirection(DcMotor.Direction.FORWARD);
            rightDrive.setPower(0);
        }

    }
}
