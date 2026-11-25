package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Main TeleOp", group = "TeleOp")
public class MainTeleOp extends LinearOpMode {

    static final double TICKS_PER_REV   = 28;
    static final double START_RPM       = 3000;
    static final double MAX_RPM         = 5800;
    static final double RPM_STEP        = 100;
    static final double SLOW_MODE_SCALE = 0.4;
    static final double FEEDER_REST     = 0.0;
    static final double FEEDER_PUSH     = 0.5;
    static final double FEED_TIME_SEC   = 0.3;
    static final double LOADER_POWER    = 1.0;
    static final double INTAKE_POWER    = 1.0;
    static final double SERVO1_POWER    = 1.0;    // make -1.0 to spin the other way
    static final double SERVO2_POWER    = 1.0;    // make -1.0 to spin the other way
    static final double HITTER_POWER    = 1.0;    // make -1.0 to spin the other way

    @Override
    public void runOpMode() {
        DcMotor leftDrive  = hardwareMap.get(DcMotor.class, "leftDrive");
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        DcMotorEx flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorSimple.Direction.FORWARD);
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Spinning servos. Works if config says Servo OR Continuous Rotation Servo.
        CRServo servo1CR = hardwareMap.tryGet(CRServo.class, "servo1");
        Servo   servo1   = hardwareMap.tryGet(Servo.class, "servo1");
        CRServo servo2CR = hardwareMap.tryGet(CRServo.class, "servo2");
        Servo   servo2   = hardwareMap.tryGet(Servo.class, "servo2");
        CRServo hitterCR = hardwareMap.tryGet(CRServo.class, "hitter");
        Servo   hitter   = hardwareMap.tryGet(Servo.class, "hitter");

        // INTAKE: looks for "intake" first, then "intakeRight"
        DcMotor intakeMotor = hardwareMap.tryGet(DcMotor.class, "intake");
        CRServo intakeCR    = hardwareMap.tryGet(CRServo.class, "intake");
        Servo   intakeServo = hardwareMap.tryGet(Servo.class, "intake");
        if (intakeMotor == null && intakeCR == null && intakeServo == null) {
            intakeMotor = hardwareMap.tryGet(DcMotor.class, "intakeRight");
            intakeCR    = hardwareMap.tryGet(CRServo.class, "intakeRight");
            intakeServo = hardwareMap.tryGet(Servo.class, "intakeRight");
        }

        String intakeType = "NOT FOUND";
        if (intakeMotor != null) intakeType = "MOTOR";
        if (intakeCR != null) intakeType = "CR SERVO";
        if (intakeServo != null) intakeType = "SERVO: change to Continuous Rotation";

        CRServo feederCR    = hardwareMap.tryGet(CRServo.class, "feeder");
        DcMotor feederMotor = hardwareMap.tryGet(DcMotor.class, "feeder");
        Servo   feeder      = hardwareMap.tryGet(Servo.class, "feeder");
        if (feeder != null) {
            feeder.setPosition(FEEDER_REST);
        }

        String loaderType = "NOT FOUND";
        if (feederCR != null) loaderType = "CR SERVO";
        if (feederMotor != null) loaderType = "MOTOR";
        if (feeder != null) loaderType = "SERVO";

        boolean servo1Found = servo1CR != null || servo1 != null;
        boolean servo2Found = servo2CR != null || servo2 != null;
        boolean hitterFound = hitterCR != null || hitter != null;

        ElapsedTime feedTimer = new ElapsedTime();
        boolean feeding = false;
        boolean flywheelOn = false;
        double targetRPM = START_RPM;
        boolean hitterOn = false;
        boolean lastX = false;
        boolean lastY = false;
        boolean lastB = false;
        boolean lastUp = false;
        boolean lastDown = false;

        telemetry.addLine("Ready. Press START.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            // DRIVE
            double DEADZONE = 0.1;      // ignores tiny stick drift
            double LEFT_SCALE = 1.0;    // lower this if robot swerves RIGHT
            double RIGHT_SCALE = 0.95;  // lower this if robot swerves LEFT

            double forward = -gamepad1.left_stick_y;
            if (Math.abs(gamepad2.left_stick_y) > Math.abs(forward)) {
                forward = -gamepad2.left_stick_y;
            }
            double turn = gamepad1.right_stick_x;
            if (Math.abs(gamepad2.right_stick_x) > Math.abs(turn)) {
                turn = gamepad2.right_stick_x;
            }
            if (Math.abs(forward) < DEADZONE) forward = 0;
            if (Math.abs(turn) < DEADZONE) turn = 0;

            double leftPower = (forward + turn) * LEFT_SCALE;
            double rightPower = (forward - turn) * RIGHT_SCALE;
            double max = Math.max(1.0, Math.abs(leftPower));
            max = Math.max(max, Math.abs(rightPower));
            double speed = 1.0;
            if (gamepad1.right_trigger > 0.5 || gamepad2.right_trigger > 0.5) {
                speed = SLOW_MODE_SCALE;
            }
            leftDrive.setPower(leftPower / max * speed);
            rightDrive.setPower(rightPower / max * speed);

            // BUTTONS
            boolean xPressed = gamepad1.x || gamepad2.x;
            boolean yPressed = gamepad1.y || gamepad2.y;
            boolean bPressed = gamepad1.b || gamepad2.b;
            boolean upPressed = gamepad1.dpad_up || gamepad2.dpad_up;
            boolean downPressed = gamepad1.dpad_down || gamepad2.dpad_down;

            // FLYWHEEL: X turns on / off
            if (xPressed && !lastX) flywheelOn = !flywheelOn;
            lastX = xPressed;

            if (upPressed && !lastUp) targetRPM += RPM_STEP;
            if (downPressed && !lastDown) targetRPM -= RPM_STEP;
            lastUp = upPressed;
            lastDown = downPressed;
            targetRPM = Math.max(0, Math.min(MAX_RPM, targetRPM));

            if (flywheelOn) {
                flywheel.setVelocity(targetRPM * TICKS_PER_REV / 60.0);
            } else {
                flywheel.setPower(0);
            }

            double currentRPM = flywheel.getVelocity() * 60.0 / TICKS_PER_REV;

            // LOADER: hold Y
            double loaderPower = 0;
            if (yPressed) loaderPower = LOADER_POWER;
            if (feederCR != null) feederCR.setPower(loaderPower);
            if (feederMotor != null) feederMotor.setPower(loaderPower);

            if (yPressed && !lastY && !feeding && feeder != null) {
                feeder.setPosition(FEEDER_PUSH);
                feedTimer.reset();
                feeding = true;
            }
            lastY = yPressed;

            if (feeding && feedTimer.seconds() > FEED_TIME_SEC) {
                feeder.setPosition(FEEDER_REST);
                feeding = false;
            }

            // INTAKE: hold Y (runs with the loader)
            double intakePower = 0;
            if (yPressed) intakePower = INTAKE_POWER;
            if (intakeMotor != null) intakeMotor.setPower(intakePower);
            if (intakeCR != null) intakeCR.setPower(intakePower);

            // SERVO1 + SERVO2: always spinning
            spin(servo1CR, servo1, SERVO1_POWER);
            spin(servo2CR, servo2, SERVO2_POWER);

            // HITTER: B turns on / off
            if (bPressed && !lastB) hitterOn = !hitterOn;
            lastB = bPressed;
            if (hitterOn) {
                spin(hitterCR, hitter, HITTER_POWER);
            } else {
                spin(hitterCR, hitter, 0);
            }

            // TELEMETRY
            telemetry.addData("Flywheel", flywheelOn ? "ON" : "OFF");
            telemetry.addData("Target RPM", "%.0f", targetRPM);
            telemetry.addData("Actual RPM", "%.0f", currentRPM);
            telemetry.addData("Loader type", loaderType);
            telemetry.addData("Loader", yPressed ? "RUNNING" : "off");
            telemetry.addData("Intake type", intakeType);
            telemetry.addData("servo1", servo1Found ? "SPINNING" : "NOT FOUND");
            telemetry.addData("servo2", servo2Found ? "SPINNING" : "NOT FOUND");
            telemetry.addData("Hitter", !hitterFound ? "NOT FOUND" : (hitterOn ? "ON" : "OFF"));
            telemetry.update();
        }
    }

    // Spins a servo at power -1 to 1, whether it's set up as CR Servo or regular Servo
    private void spin(CRServo cr, Servo s, double power) {
        if (cr != null) cr.setPower(power);
        if (s != null) s.setPosition(0.5 + power * 0.5);
    }
}
