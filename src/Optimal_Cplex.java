import ilog.concert.IloException;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Optimal_Cplex {
    private Read_Input data;
    public Optimal_Cplex(Read_Input data) {
        this.data = data;
    }
    public double solve() {
        try {
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);
            
            IloNumVar[][] x = new IloNumVar[data.numbFacility][data.numbClient];
            for (int i = 0; i < data.numbFacility; i++) {
                x[i] = cplex.boolVarArray(data.numbClient);
            }

            IloNumVar[] z = cplex.boolVarArray(data.numbFacility);

            IloLinearNumExpr facilityCostExpr = cplex.linearNumExpr();
            for (int i = 0; i < data.numbFacility; i++) {
                facilityCostExpr.addTerm(data.facilityCost[i], z[i]);
            }

            IloLinearNumExpr clientCostExpr = cplex.linearNumExpr();
            for (int i = 0; i < data.numbFacility; i++) {
                for (int j = 0; j < data.numbClient; j++) {
                    clientCostExpr.addTerm(data.clientDemand[j] * data.clientCost[i][j], x[i][j]);
                }
            }

            cplex.addMinimize(cplex.sum(facilityCostExpr, clientCostExpr));

            //#1
            for (int j = 0; j < data.numbClient; j++) {
                IloLinearNumExpr expr = cplex.linearNumExpr();
                for (int i = 0; i < data.numbFacility; i++) {
                    expr.addTerm(1, x[i][j]);
                }
                cplex.addEq(1, expr);
            }

            //#2
            for (int i = 0; i < data.numbFacility; i++) {
                for (int j = 0; j < data.numbClient; j++) {
                    cplex.addLe(x[i][j], z[i]);
                }
            }
            
            cplex.solve();
            
            System.out.println("Upper bound = " + cplex.getObjValue());
            return cplex.getObjValue();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        return -1;
    }
}
