package frc.robot;
/*
 *******************************************************************************************
 * Copyright (C) 2022 FRC Team 1736 Robot Casserole - www.robotcasserole.org
 *******************************************************************************************
 *
 * This software is released under the MIT Licence - see the license.txt
 *  file in the root of this repo.
 *
 * Non-legally-binding statement from Team 1736:
 *  Thank you for taking the time to read through our software! We hope you
 *   find it educational and informative! 
 *  Please feel free to snag our software for your own use in whatever project
 *   you have going on right now! We'd love to be able to help out! Shoot us 
 *   any questions you may have, all our contact info should be on our website
 *   (listed above).
 *  If you happen to end up using our software to make money, that is wonderful!
 *   Robot Casserole is always looking for more sponsors, so we'd be very appreciative
 *   if you would consider donating to our club to help further STEM education.
 */

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.Constants;
import frc.lib.Calibration.Calibration;
import frc.lib.Signal.Annotations.Signal;

public class Elevator {
    VictorSPX lowerElevatorMotor;

	DigitalInput lowerSensor;
	DigitalInput upperSensor;

	@Signal
	boolean upperBallPresent;

	@Signal
	boolean lowerBallPresent;

	@Signal ( units = "cmd")
	double lowerElevatorMotorCmd;

	private static Elevator elevator = null;
	public static synchronized Elevator getInstance() {
		if(elevator == null)
			elevator = new Elevator();
		return elevator;
	}

	// This is the private constructor that will be called once by getInstance() and it should instantiate anything that will be required by the class
	private Elevator() {
        lowerElevatorMotor = new VictorSPX(Constants.ELEVATOR_LOWER_CANID);
		advance = new Calibration("elevator advance speed", "cmd", 0.5);
		eject = new Calibration("elevator eject speed", "cmd", 0.5);

		lowerSensor = new DigitalInput(Constants.ELEVATOR_LOWER_BALL_SENSOR);
		upperSensor = new DigitalInput(Constants.ELEVATOR_UPPER_BALL_SENSOR);

	}

	Calibration advance;
    Calibration eject;
	@Signal(units = "cmd")
	elevatorCmdState cmdState = elevatorCmdState.STOP;

		
	// Possible states we could want the elevator to be running in
	// STOP: don't move
	// INTAKE: move cargo into and through the singulator into the elevator
	// EJECT: move cargo toward the intake 
	public enum elevatorCmdState{
		STOP(0),
		INTAKE(1),
		SHOOT(2),
		EJECT(-1);

		public final int value;

		private elevatorCmdState(int value) {
			this.value = value;
		}

		public int toInt() {
			return this.value;
		}
	}

	public void setCmd(elevatorCmdState cmd_in){
		cmdState = cmd_in;

	}

	public void update(){
		upperBallPresent = upperSensor.get();
		lowerBallPresent = lowerSensor.get();
		
		if(cmdState == elevatorCmdState.STOP) {
			lowerElevatorMotorCmd = 0;
		} else if(cmdState == elevatorCmdState.INTAKE) {
			lowerElevatorMotorCmd = advance.get();
		} else if(cmdState == elevatorCmdState.SHOOT) {
			lowerElevatorMotorCmd = advance.get();
		} else if(cmdState == elevatorCmdState.EJECT) {
			lowerElevatorMotorCmd = -1.0 * eject.get();
		} else {
			lowerElevatorMotorCmd = 0;
		}

		lowerElevatorMotor.set(ControlMode.PercentOutput,lowerElevatorMotorCmd);


	}

	public boolean isFull(){
		return upperBallPresent && lowerBallPresent;
	}

}


    

