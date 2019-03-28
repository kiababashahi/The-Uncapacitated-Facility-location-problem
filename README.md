
The objective of this project was to solve the The-Uncapacitated-Facility-location-problem(UFLP) via the Lagranian relaxation technique. The reader who is not familiar with Lagrangian relaxations is referred to the book:" Integer Programming" by Laurence A.Wolsey

@copyright statement: 
I have used some parts of the code of professor Ivan Contreras who was teaching this course in this project. ( mostly for the heuristic class.) 

Problem statement: 

A company wants to open "n" facility sites to support "m" customers by paying the minimum amount of price possible. The final price that the company has to pay is affected by two other costs: The cost of opening a facility "j" which is indicated by "f_j" and the cost of serving customer "i" through facility "j" which is indicated by "c_ij".

 In order to model the problem the company needs to know whether or not to open a facility j which can be done via a binary decision variable "z_j". And whether or not costumer i will be serviced by facility j which can be done by another binary variable called "x_ij". The assumptions are that each customer can only be serviced through one facility and that our facilities are incapacitated. Meaning that they can serve as many commuters as possible. 

Then the UFLP problem can be modeled in the following way:

![](https://github.com/kiababashahi/The-Uncapacitated-Facility-location-problem/blob/master/UFLP.png)

In this project I implemented and compared two different Lagrangian relaxations resulting from relaxing the first and the second set of constraints of the UFLP. 

By relaxing the first set of constraints we have to solve the following optimization problem:

![](https://github.com/kiababashahi/The-Uncapacitated-Facility-location-problem/blob/master/FirstRelaxation.png)

Taking a closer look at the objective function of the first relaxation one can observe that it consists of the summation of two different components which can be minimized independently of each other.

Hence the following model can be decomposed into two different sub-problems for which the first sub-problem will deal with minimizing the first component, i.e., 

![](https://github.com/kiababashahi/The-Uncapacitated-Facility-location-problem/blob/master/Subproblem1.png).

With respect to the integrality conditions on z( which was solved in** LR_C2.java** ). The following block of code will compute the values for the decision variable z: 

```ruby

			for (int i = 0; i < N; i++) { // Solve ith 0-1 z_i subproblem
				f_cost = f[i];
				for (int j = 0; j < M; j++)
					f_cost -= Lambda[i][j];
				if (f_cost < 0) {
					z[i] = 1;
					L_value += f_cost;
				}
			}
```

and the second sub-problem which minimizes the second component ,i.e. , 

![](https://github.com/kiababashahi/The-Uncapacitated-Facility-location-problem/blob/master/Subproblem2.png)

With respect to constraints number 2 and the integrality condition on x. The values of x and the values of Lambdas in the block of code above (the Lagrangian multipliers) where calculated via: 

```ruby
	for (int j = 0; j < M; j++) {
				min_cost = d[j] * c[0][j] + Lambda[0][j];
				best_i = 0;
				for (int i = 1; i < N; i++) {
					if (d[j] * c[i][j] + Lambda[i][j] < min_cost) {
						min_cost = d[j] * c[i][j] + Lambda[i][j];
						best_i = i;
					}
				}
				x[best_i][j] = 1;
				L_value += min_cost;
			}

```








 
Relaxing the second set of constraints would result in the following optimization model:

![](https://github.com/kiababashahi/The-Uncapacitated-Facility-location-problem/blob/master/lagrangian2.png)

which can be solved via solving i independent sub-problems by first calculating the value of 

![](https://github.com/kiababashahi/The-Uncapacitated-Facility-location-problem/blob/master/relaxation2subproblem.png)
This problem was solved in **LR_CR1.java**. 


The first step will be to set the value of the decision variables and then calculate the sum of the duals. 

For each i, if the value of the formula above was negative, I stored those j`s which caused the value to become negative in a vector called costumer assign. After, I added the value above to its corresponding f, if the result were negative, I only assigned 1 to those x`s that their corresponding value in the costumer assign vector was 1. The following block of code is charge of generating the solution.
```ruby
			for (int i = 0; i < N; i++) { // solves each of the i subproblems
				customer_assign = new int[M];
				min_cost = 0;
				for (int j = 0; j < M; j++) {
					if (d[j] * c[i][j] + Lambda[j] < 0) {
						min_cost += (d[j] * c[i][j] + Lambda[j]);
						customer_assign[j] = 1;
					}
				}

				if (f[i] + min_cost < 0) {
					z[i] = 1;
					L_value += (f[i] + min_cost);
					for (int j = 0; j < M; j++) {
						x[i][j] = customer_assign[j];
					}
				} else {
					for (int j = 0; j < M; j++)
						x[i][j] = 0;
				}

			}
                        L_value = L_value - LamSum; //the lagrangian values are being updated
```
The upper bounds were found using a heuristic which can be found in class** Heuristic. Java**. Yet the lower bounds were generated by the Relaxations (pay attention that the following problem is the relaxation of a minimization hence the Lagrangian would provide us with a lower bound). 

The Lagrangian value is updated in the piece of code above and the upper bounds were selected using  the heuristic. Afterwards the sub gradient method was used to solve the problem and the stopping criteria were checked : 
```ruby
if (L_value > best_lower_bound) { // Update best lower bound
				best_lower_bound = L_value;
				for (int i = 0; i < N; i++)
					open_facility[i] = z[i];
				double UB = h.Reassign_customers(); // Lagrangian heuristic
				if (UB != -1 && UB < upper_bound)
					upper_bound = UB;
			}
			System.out.println(t + 1 + ": " + " L_value: " + L_value + " " + " BLB: " + best_lower_bound + " UB: "
					+ upper_bound + " gap: " + (upper_bound - best_lower_bound) / upper_bound * 100 + "epsilon is+ "
					+ epsilon);
			double SqrNorm = 0;
			for (int j = 0; j < M; j++) {
				for (int i = 0; i < N; i++) {
					s[j] += x[i][j];
				}

				s[j] -= 1;
				SqrNorm += (double) (s[j] * s[j]);

			}
	// Check stopping criteria
			if ((upper_bound - best_lower_bound) / upper_bound * 100 < 0.005)
				break;
			// Compute steplength
			if (t - last > interv_dec_lambda) {
				last = t;
				epsilon = epsilon / 2;
			}
			if (epsilon < 0.0001)
				epsilon = 2;

			double StepLength = epsilon * (upper_bound - L_value) / SqrNorm;

			// Update Lagrange multipliers
			for (int j = 0; j < M; j++) {
				Lambda[j] = Lambda[j] + StepLength * s[j];
			}
```
We have also solved the problem using CPlex to see how close or far we are from the value which can be obtained by Cplex. The following block of code is charge of solving the ILP problem using CPlex:

 ```ruby
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
```
A comparative study of the two relaxations using technical details of the relaxations can found in the pdf file attached to the code. 
