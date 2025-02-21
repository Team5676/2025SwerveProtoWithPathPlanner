package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.utils.ShuffleboardContent;

public class RotateArmSubsystem extends SubsystemBase {

  public double m_positionRadians;
  public static final double kGearRatio = 100 / 1 * 46 / 14;
  public static final double kIntakeArmEncoderPositionFactor = (2 * Math.PI) / kGearRatio;

  private final int m_lowerArmCanId = 52;

  private final RelativeEncoder m_driveEncoder;
  private final SparkMax m_driveMotor;
  private final SparkClosedLoopController m_driveController;

  private final double minRotations = Units.degreesToRadians(-270);
  private final double maxRotations = Units.degreesToRadians(270);

  public RotateArmSubsystem() {
    // Drive Motor setup
    m_driveMotor = new SparkMax(m_lowerArmCanId, MotorType.kBrushless);

    // drive encoder setup
    m_driveEncoder = m_driveMotor.getEncoder();

    m_driveController = m_driveMotor.getClosedLoopController();

    SparkMaxConfig config = new SparkMaxConfig();
    config.closedLoop
    .p(1.5)
    .i(0)
    .d(0)
    .outputRange(-1, 1);
    config.encoder.positionConversionFactor(kIntakeArmEncoderPositionFactor);

    m_driveMotor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_positionRadians = m_driveEncoder.getPosition();

    ShuffleboardContent.initRotateArm(this);
  }

  @Override
  public void periodic() {
  }

  public void moveToPosition(double position) {
    setReferenceValue(position);
    setReferencePeriodic();
  }

  public void moveToFarPosition() {
    setReferenceValue(maxRotations);
    setReferencePeriodic();
  }

  public void moveToMidPosition() {
    setReferenceValue(22);
    setReferencePeriodic();
  }

  public void moveToBackPosition() {
    setReferenceValue(0);
    setReferencePeriodic();
  }

  public double getMinRotations() {
    return minRotations;
  }

  public double getMaxRotations() {
    return maxRotations;
  }

  public double getPosition() {
    return m_driveEncoder.getPosition();
  }

  public void driveArm(double throttle) {
    if (Math.abs(throttle) > 0.05) {
      m_positionRadians += Units.degreesToRadians(throttle);
    }
    setReferencePeriodic();
  }

  public void stop() {
    m_driveMotor.set(0);
  }

  public void setReferenceValue(double rotation) {
    m_positionRadians = Units.degreesToRadians(rotation);
  }

  public void setReferencePeriodic() {
    m_positionRadians = MathUtil.clamp(m_positionRadians, minRotations, maxRotations);
    m_driveController.setReference(m_positionRadians, ControlType.kPosition);
  }
}