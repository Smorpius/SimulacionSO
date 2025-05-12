package src;

import java.util.ArrayList;
 import java.util.List;
 

 public class ProcessControlBlock {
  private int pid;
  private String state;
  private int priority;
  private int cpuBurst;
  private int memoryRequired;
  private List<Integer> memoryFrames = new ArrayList<>();
 

  public ProcessControlBlock(int pid, String state, int priority, int cpuBurst, int memoryRequired) {
  this.pid = pid;
  this.state = state;
  this.priority = priority;
  this.cpuBurst = cpuBurst;
  this.memoryRequired = memoryRequired;
  }
 

  // Getters y Setters
  public int getPid() {
  return pid;
  }
 

  public void setPid(int pid) {
  this.pid = pid;
  }
 

  public String getState() {
  return state;
  }
 

  public void setState(String state) {
  this.state = state;
  }
 

  public int getPriority() {
  return priority;
  }
 

  public void setPriority(int priority) {
  this.priority = priority;
  }
 

  public int getCpuBurst() {
  return cpuBurst;
  }
 

  public void setCpuBurst(int cpuBurst) {
  this.cpuBurst = cpuBurst;
  }
 

  public int getMemoryRequired() {
  return memoryRequired;
  }
 

  public void setMemoryRequired(int memoryRequired) {
  this.memoryRequired = memoryRequired;
  }
 

  public List<Integer> getMemoryFrames() {
  return memoryFrames;
  }
 

  public void setMemoryFrames(List<Integer> memoryFrames) {
  this.memoryFrames = memoryFrames;
  }
 

  @Override
  public String toString() {
  return "PID: " + pid + ", Estado: " + state + ", Prioridad: " + priority +
  ", CPU Burst: " + cpuBurst + ", Memoria: " + memoryRequired +
  ", Marcos: " + memoryFrames;
  }
 }








