package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "accurateturns")
public class accurateturns extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();
        double targetDistance = 50;
        int target = (int) (targetDistance * 17.83);

        rightDrive.setPower(0.5);
        leftDrive.setPower(0.5);

        while (opModeIsActive() && leftDrive.getCurrentPosition() < target && rightDrive.getCurrentPosition() < target) {

        }

        rightDrive.setPower(0);
        leftDrive.setPower(0);

        while (opModeIsActive()) {
            telemetry.addData("Left ticks", leftDrive.getCurrentPosition());
            telemetry.addData("Right ticks", rightDrive.getCurrentPosition());
            telemetry.update();
        }


    }
}