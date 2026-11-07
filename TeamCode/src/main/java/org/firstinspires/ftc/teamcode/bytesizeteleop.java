package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "ByteSize TeleOp")
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
    }
}
