package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "ByteSize TeleOp")
public class bytesizeteleop extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");

        waitForStart();

        rightDrive.setPower(0.5);
        leftDrive.setPower(0.5);
        sleep(1000);

        rightDrive.setPower(0);
        leftDrive.setPower(0);


    }
}
