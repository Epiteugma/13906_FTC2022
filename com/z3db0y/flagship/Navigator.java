package com.z3db0y.flagship;

import static android.os.SystemClock.sleep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.Axis;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Navigator {
	public BNO055IMU imu;
	public CustomAccelerationIntegrator accelerationIntegrator;

	enum AxesSigns {
		PPP(0,0,0), PPN(0,0,1), PNP(0,1,0), PNN(0,1,1), NPP(1,0,0), NPN(1,0,1), NNP(1,1,0), NNN(1,1,1);

		int x;
		int y;
		int z;

		AxesSigns(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public void remapIMU(AxesOrder axesOrder, AxesSigns axesSigns) {
		Axis[] axes = axesOrder.axes();
		byte axesOrderByte = (byte)(
				(0x02 & axes[0].index) |
				(0x0C & axes[1].index << 2) |
				(0x30 & axes[2].index << 4)
		);
		byte axesSignByte = (byte)(
				(0x01 & axesSigns.x) |
				(0x02 & axesSigns.y) |
				(0x04 & axesSigns.z)
		);

		// Change mode to config
		this.imu.write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.CONFIG.bVal);
		sleep(100);
		// Remap axes
		this.imu.write8(BNO055IMU.Register.AXIS_MAP_CONFIG, axesOrderByte);
		// Remap signs
		this.imu.write8(BNO055IMU.Register.AXIS_MAP_SIGN, axesSignByte);
		// Change back to IMU mode
		this.imu.write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.IMU.bVal);
		sleep(100);
	}

	static class CustomAccelerationIntegrator implements BNO055IMU.AccelerationIntegrator {
		public boolean trackMovements = false;
		public List<Movement> lastMovements = new ArrayList<>();

		Acceleration accel;
		Position pos;
		Velocity velo;
		BNO055IMU.Parameters params;

		public CustomAccelerationIntegrator() {
			this.accel = new Acceleration();
			this.pos = new Position();
			this.velo = new Velocity();
		}

		@Override
		public Position getPosition() {
			return pos;
		}

		@Override
		public Velocity getVelocity() {
			return velo;
		}

		@Override
		public Acceleration getAcceleration() {
			return accel;
		}

		@Override
		public void initialize(@NonNull BNO055IMU.Parameters params, @Nullable Position pos, @Nullable Velocity velo) {
			this.params = params;
			this.pos = pos != null ? pos : new Position();
			this.velo = velo != null ? velo : new Velocity();
		}

		@Override
		public void update(Acceleration accel) {
			if(accel.xAccel < 0.1) accel.xAccel = 0;
			if(accel.yAccel < 0.1) accel.yAccel = 0;
			if(accel.zAccel < 0.1) accel.zAccel = 0;
			
			this.pos = calculatePos(accel, this.accel);
			this.velo = calculateVelo(accel, this.accel);
			this.accel = accel;
		}

		// IMPORTANT: FILTERING!

		protected Velocity calculateVelo(Acceleration accel, Acceleration lastAccel) {
			// TODO: calculate velocity based on delta in acceleration
			// v = u + at
			double timeDelta = (accel.acquisitionTime - lastAccel.acquisitionTime) / 1_000_000_000.0;
			Velocity lastVelo = this.getVelocity();
			return new Velocity(
				accel.unit,
				lastVelo.xVeloc + accel.xAccel * timeDelta,
				lastVelo.yVeloc + accel.yAccel * timeDelta,
				lastVelo.zVeloc + accel.zAccel * timeDelta,
				accel.acquisitionTime

			);
		}

		protected Position calculatePos(Acceleration accel, Acceleration lastAccel) {
			// TODO: calculate position based on velocity
			// s = 1/2 * (u + v) * t
			Velocity initialVelo = this.velo;
			Velocity finalVelo = calculateVelo(accel, lastAccel);
			double timeDelta = (accel.acquisitionTime - lastAccel.acquisitionTime) / 1_000_000_000.0;

			return new Position(
				accel.unit,
					this.pos.x + (0.5 * (initialVelo.xVeloc + finalVelo.xVeloc) * timeDelta),
					this.pos.y + (0.5 * (initialVelo.yVeloc + finalVelo.yVeloc) * timeDelta),
					this.pos.z + (0.5 * (initialVelo.zVeloc + finalVelo.zVeloc) * timeDelta),
				accel.acquisitionTime
			);
		}
	}

	public Navigator(BNO055IMU imu) {
		this(imu, AxesOrder.XYZ, AxesSigns.PPP);
	}

	public Navigator(BNO055IMU imu, AxesSigns axesSigns) {
		this(imu, AxesOrder.XYZ, axesSigns);
	}

	public Navigator(BNO055IMU imu, AxesOrder axesOrder) {
		this(imu, axesOrder, AxesSigns.PPP);
	}

	public Navigator(BNO055IMU imu, AxesOrder axesOrder, AxesSigns axesSigns) {
		this.imu = imu;
		this.accelerationIntegrator = new CustomAccelerationIntegrator();
		BNO055IMU.Parameters params = new BNO055IMU.Parameters();
		params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
		params.accelerationIntegrationAlgorithm = this.accelerationIntegrator;
		this.remapIMU(axesOrder, axesSigns);
		imu.initialize(params);
	}

	public void startTracking() {
		this.accelerationIntegrator.trackMovements = false;
		this.accelerationIntegrator.lastMovements = new ArrayList<>();
		this.accelerationIntegrator.trackMovements = true;
		this.imu.startAccelerationIntegration(new Position(), new Velocity(), 1);
	}

	public void stopTracking() {
		this.accelerationIntegrator.trackMovements = false;
		this.imu.stopAccelerationIntegration();
	}

	public Position getPosition() {
		return this.accelerationIntegrator.getPosition();
	}

	public Velocity getVelocity() {
		return this.accelerationIntegrator.getVelocity();
	}

	public Acceleration getAcceleration() {
		return this.accelerationIntegrator.getAcceleration();
	}

	// should be imu-based so as to not require robot width, ticks per second etc.
	void doMovements(List<Movement> movements, DriveTrain driveTrain, @Nullable double power) {
		power = Math.abs(power);
		if(power == 0) throw new IllegalArgumentException("Power cannot be 0");
		for(Movement movement : movements) {
			double angle = this.imu.getAngularOrientation().firstAngle;
			switch(movement.type) {
				case ROTATIONAL:
					// TODO: direction
					while(this.imu.getAngularOrientation().firstAngle != angle + movement.value) {
						driveTrain.driveRobotCentric(0, power, 0);
					}
					driveTrain.driveRobotCentric(0, 0, 0);
					break;
				case LINEAR:
					Position current = this.getPosition();
					Position target = new Position(
						current.unit,
						Math.sin(Math.toRadians(angle)) * movement.value,
						Math.cos(Math.toRadians(angle)) * movement.value,
						current.z,
						current.acquisitionTime
					);
					do {
						current = this.getPosition();
						driveTrain.driveRobotCentric(power, 0, 0);
					} while(Math.abs(current.x - target.x) > 0 || Math.abs(current.y - target.y) > 0);
					driveTrain.driveRobotCentric(0, 0, 0);
					break;
			}
		}
	}

	public void retraceMovements(@NonNull DriveTrain driveTrain) {
		// TODO: re-trace saved movements
	}

	public void goTo(@NonNull DriveTrain driveTrain, double x, double y) {
		// TODO: go to a specific point + pathfinding
	}
}
