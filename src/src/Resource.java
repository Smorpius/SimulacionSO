package src;
public class Resource {
  private String name;
  private int totalUnits;
  private int availableUnits;
 

  public Resource(String name, int totalUnits) {
  this.name = name;
  this.totalUnits = totalUnits;
  this.availableUnits = totalUnits;
  }
 

  public String getName() {
  return name;
  }
 

  public int getTotalUnits() {
  return totalUnits;
  }
 

  public int getAvailableUnits() {
  return availableUnits;
  }
 

  public void setAvailableUnits(int availableUnits) {
  this.availableUnits = availableUnits;
  }
 

  public boolean allocate(int units) {
  if (availableUnits >= units) {
  availableUnits -= units;
  return true;
  }
  return false;
  }
 

  public void release(int units) {
  availableUnits += units;
  }
 }