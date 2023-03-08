package com.z3db0y.flagship;


import android.util.Log;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.z3db0y.flagship.pid.PIDCoeffs;
import com.z3db0y.flagship.pid.VelocityPIDController;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Motor extends DcMotorImplEx {
	VelocityPIDController velocityPidController = new VelocityPIDController();
	boolean usePID = false;
	boolean holdPosition = false;
	boolean currentlyHoldingPosition = false;
	RunMode runMode = RunMode.RUN_WITHOUT_ENCODER;
	double power = 0.0;
	double holdPower = 1;
	double velocity = 0.0;
	AngleUnit veloUnit = null;
	int targetPosition = 0;
	double gearRatio;
	boolean stallDetect;
	int targetPositionTolerance = 10;
	public Motor(DcMotorImplEx motor) {
		super(motor.getController(), motor.getPortNumber());
	}

	// TODO: methods
	void updateMotorState() {
		Log.i("RunMode", String.valueOf(this.runMode));
		Log.i("Power", String.valueOf(this.power));
		Log.i("Velocity", String.valueOf(this.velocity));
		if(this.holdPosition && Math.abs(this.power) < 0.2 && Math.abs(this.velocity) < 20) {
			if(this.runMode == RunMode.STOP_AND_RESET_ENCODER) super.setMode(RunMode.STOP_AND_RESET_ENCODER);
			if(!this.currentlyHoldingPosition) {
				currentlyHoldingPosition = true;
				super.setTargetPosition(super.getCurrentPosition());
				super.setMode(RunMode.RUN_TO_POSITION);
				super.setPower(holdPower);
			}
		}
		else {
			this.currentlyHoldingPosition = false;
			if (super.getTargetPosition() != this.targetPosition)
				super.setTargetPosition(this.targetPosition);
			if (super.getMode() != this.runMode) super.setMode(this.runMode);
			if (this.velocity != 0) {
				int dir = this.direction == Direction.FORWARD ? 1 : -1;
				if (veloUnit == null)
					((DcMotorControllerEx) super.controller).setMotorVelocity(super.getPortNumber(), dir * this.velocity);
				else
					((DcMotorControllerEx) super.controller).setMotorVelocity(super.getPortNumber(), dir * this.velocity, this.veloUnit);
			} else {
				if (super.getPower() != this.power) super.setPower(this.power);
			}
			if(this.stalled() && stallDetect) {
				super.setPower(0);
			}
		}
	}

	public void setStallDetect(boolean stallDetect){
		this.stallDetect = stallDetect;
	}

	long lastStallCheck = 0;
	int lastTicks = 0;
	boolean lastStallOutput = false;
	boolean stalled() {
		if(lastStallCheck == 0) {
			lastStallCheck = System.currentTimeMillis();
			lastTicks = this.getCurrentPosition();
		}
		if(System.currentTimeMillis() < lastStallCheck + 12.5) return lastStallOutput;
		Log.i("lastTicks", String.valueOf(lastTicks));
		Log.i("currTicks", String.valueOf(this.getCurrentPosition()));
		Log.i("minTicksChange", String.valueOf(0.0 * (this.power != 0 ? this.power * this.getMotorType().getTicksPerRev() : this.getVelocity())));
		boolean stalled = Math.abs(lastTicks - this.getCurrentPosition()) <= Math.abs(0.005 * (this.power != 0 ? this.power * this.getMotorType().getTicksPerRev() : this.getVelocity()));
		lastStallCheck = System.currentTimeMillis();
		lastTicks = this.getCurrentPosition();
		lastStallOutput = stalled;
		return stalled;
	}

	public void setHoldPower(double holdPower) {
		this.holdPower = holdPower;
	}

	public void setGearRatio(double gearRatio){
		this.gearRatio = gearRatio;
	}

	public void setHoldPosition(boolean holdPosition) {
		this.holdPosition = holdPosition;
		updateMotorState();
	}

	@Override
	public void setMode(@NonNull RunMode runMode) {
		this.runMode = runMode;
		updateMotorState();
	}

	@Override
	public void setPower(double power) {
		this.velocity = 0;
		this.power = this.usePID ? this.velocityPidController.getOutput(power) : power;
		updateMotorState();
	}

	@Override
	public void setTargetPosition(int targetPosition) {
		this.targetPosition = targetPosition;
		updateMotorState();
	}

	public void setRelativeTargetPosition(int targetPosition) {
		this.targetPosition = super.getCurrentPosition() + targetPosition;
		updateMotorState();
	}

	@Override
	public void setVelocity(double angularRate) {
		this.power = 0;
		this.velocity = angularRate;
		this.veloUnit = null;
		updateMotorState();
	}

	@Override
	public void setVelocity(double angularRate, AngleUnit unit) {
		this.power = 0;
		this.velocity = angularRate;
		this.veloUnit = unit;
		updateMotorState();
	}

	public boolean getHoldPosition() {
		return this.holdPosition;
	}

	@Override
	public RunMode getMode() {
		return this.runMode;
	}

	@Override
	public double getPower() {
		return this.power;
	}

	@Override
	public int getTargetPosition() {
		return this.targetPosition;
	}

	public void runToPosition(int ticks, double power, boolean relative) {
		if(relative) this.resetEncoder();
		this.setTargetPosition(ticks);
		this.setMode(RunMode.RUN_TO_POSITION);
		this.setPower(power);
		while (!this.atTargetPosition()){
			Log.i("motor is at: ", this.getCurrentPosition() + "should be at: " + this.getTargetPosition());
		}
		this.setPower(0);
	}

	public void runToPosition(int ticks, double power) {
		this.runToPosition(ticks, power, true);
	}

	public void runToPositionAsync(int ticks, double power, boolean relative) {
		this.setMode(RunMode.RUN_USING_ENCODER);
		if(relative) this.resetEncoder();
		this.setTargetPosition(ticks);
		this.setMode(RunMode.RUN_TO_POSITION);
		this.setPower(power);
	}

	public void runToPositionAsync(int ticks, double power) {
		this.runToPositionAsync(ticks, power, true);
	}

	public void setTargetPositionTolerance(int targetPositionTolerance){
		this.targetPositionTolerance = targetPositionTolerance;
	}

	public int getTargetPositionTolerance(){
		return this.targetPositionTolerance;
	}

	public boolean atTargetPosition(){
		return Math.abs(this.getCurrentPosition() - this.getTargetPosition()) < targetPositionTolerance;
	}

	public void setPIDCoeffs(PIDCoeffs coeffs) {
		this.velocityPidController.updateCoeffs(coeffs);
	}

	public void setPIDCoeffs(double kP, double kI, double kD) {
		this.velocityPidController.updateCoeffs(kP, kI, kD);
	}

	public void initPID(int ticksPerRev, int maxRPM) {
		this.velocityPidController.bind(this, ticksPerRev, maxRPM);
	}

	public void enablePID() {
		this.usePID = true;
	}

	public void disablePID() {
		this.usePID = false;
	}

	public void resetEncoder() {
		RunMode mode = this.getMode();
		this.setMode(RunMode.STOP_AND_RESET_ENCODER);
		this.setMode(mode);
	}
}
