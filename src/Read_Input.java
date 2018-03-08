import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
public class Read_Input {
	public Read_Input() {
		
	}
	public int numbFacility;
    public int numbClient;
    public int[] facilityCapacity;
    public double [] facilityCost;
    public double[] clientDemand;
    public double [][] clientCost;
    public void readFile(String fileName) {
        try {
            InputStream in = getClass().getResourceAsStream("/" + fileName);
            Scanner scan = new Scanner(in);
            
            numbFacility = scan.nextInt();
            numbClient = scan.nextInt();
            //init
            facilityCapacity = new int[numbFacility];
            facilityCost = new double[numbFacility];
            clientDemand = new double[numbClient];
            clientCost = new double[numbFacility][numbClient];
            
            for (int  i = 0; i < numbFacility; i++) {
                facilityCapacity[i] = scan.nextInt();
                facilityCost[i] = scan.nextDouble();
            }
            
            for (int j = 0; j < numbClient; j++) {
                clientDemand[j] = scan.nextDouble();
                
                for (int i = 0; i < numbFacility; i++) {
                    clientCost[i][j] = scan.nextDouble();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
