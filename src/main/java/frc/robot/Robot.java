// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  
  // Objeto que será utilizado para digirir nossa tração
  DifferentialDrive chassi;

  // Objeto do controle que estaremos usando nas aulas
  XboxController controle = new XboxController(0);

  // Aqui declaramos nossos objetos de motores SparkMax
  public SparkMax motorEsquerdaMestre = new SparkMax(1, MotorType.kBrushless);
  public SparkMax motorEsquerda = new SparkMax(2, MotorType.kBrushless);
  public SparkMax motorDireitaMestre = new SparkMax(3, MotorType.kBrushless);
  public SparkMax motorDireita = new SparkMax(4, MotorType.kBrushless);


  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    /*
    * Crie novos objetos de configuração para os SPARK MAX. 
    * Eles irão armazenar os parâmetros de configuração 
    * para os SPARK MAX que definiremos abaixo.
    */
    SparkMaxConfig globalConfig = new SparkMaxConfig();
    SparkMaxConfig ConfigDireitaMestre = new SparkMaxConfig();
    SparkMaxConfig ConfigEsquerda = new SparkMaxConfig();
    SparkMaxConfig ConfigDireita = new SparkMaxConfig();
    /*
     * Coloque os parametros que vão ser aplicados para todos os
     * SparkMAXs, também será utilizado para a configuração do Esquerda Mestre
     */
    globalConfig
        .smartCurrentLimit(50)
        .idleMode(IdleMode.kBrake);
        // Aplique a configuração global e inverta, já que está no lado oposto
    ConfigDireitaMestre
        .apply(globalConfig)
        .inverted(true);
    // Aplique a configuração global e configure o SPARK líder para o modo seguidor
    ConfigEsquerda
        .apply(globalConfig)
        .follow(motorEsquerdaMestre);
    // Aplique a configuração global e configure o SPARK líder para o modo seguidor
    ConfigDireita
        .apply(globalConfig)
        .follow(motorDireitaMestre);

    /*
    * Aplique a configuração aos SPARKs.
    *
    * kResetSafeParameters é usado para colocar o SPARK MAX em um estado conhecido. 
    * Isso é útil caso o SPARK MAX precise ser substituído.
    *
    * kPersistParameters é usado para garantir que a configuração não seja perdida 
    * quando o SPARK MAX perde energia. Isso é útil para ciclos de energia que podem 
    * ocorrer durante a operação.
    */
    motorEsquerdaMestre.configure(globalConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    motorEsquerda.configure(ConfigEsquerda, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    motorDireitaMestre.configure(ConfigDireitaMestre, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    motorDireita.configure(ConfigDireita, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    // Iniciamos o objeto com os motores de cada lado
    chassi = new DifferentialDrive(motorEsquerdaMestre, motorDireitaMestre);


  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // Variáveis que serão usadas para dirigir o chassi
    double y = -controle.getLeftY();
    double x = -controle.getRightX();

    // Função que comandara os motores 
    chassi.arcadeDrive(y, x);
    // chassi.arcadeDrive(x, x, true); // Esse trecho ativa um filtro nos inputs, onde nossas entradas são reduzidas por raiz quadrada
    // chassi.curvatureDrive(y, x, true); // Esse trecho aplica um arcade drive que rotaciona em elipse, mas que é possível girar no local também

    // Colocamos os valores na shuffleboard
    SmartDashboard.putNumber("Valor eixo Y", y);
    SmartDashboard.putNumber("Valor eixo X", x);
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
